package niketeck.StayNest.config;

import com.google.genai.Client;
import com.google.genai.types.ContentEmbedding;
import com.google.genai.types.EmbedContentConfig;
import com.google.genai.types.EmbedContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.*;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.*;



/**
 * Provides EmbeddingModel and VectorStore beans manually.
 *
 * Primary datasource (port 5432) → used by JPA / all existing repositories.
 * Vector datasource  (port 5433) → Docker pgvector, used only for hotel_embeddings.
 *
 * If the pgvector extension is not reachable, falls back to a no-op VectorStore
 * so all non-AI features continue to work normally.
 */
@Configuration
@Slf4j
public class VectorStoreConfig {

    // ── Gemini API key ────────────────────────────────────────────────────────
    @Value("${spring.ai.google.genai.api-key}")
    private String apiKey;

    // ── Docker pgvector datasource config ─────────────────────────────────────
    @Value("${vectorstore.datasource.url}")
    private String vectorStoreUrl;

    @Value("${vectorstore.datasource.username}")
    private String vectorStoreUsername;

    @Value("${vectorstore.datasource.password}")
    private String vectorStorePassword;

    // ── EmbeddingModel ────────────────────────────────────────────────────────

    @Bean
    public EmbeddingModel embeddingModel() {
        return new GeminiEmbeddingModel(apiKey);
    }

    // ── Separate DataSource for Docker pgvector (port 5433) ───────────────────

    @Bean(name = "vectorStoreDataSource")
    public DataSource vectorStoreDataSource() {
        return DataSourceBuilder.create()
                .url(vectorStoreUrl)
                .username(vectorStoreUsername)
                .password(vectorStorePassword)
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    // ── JdbcTemplate wired to Docker pgvector DataSource ──────────────────────

    @Bean(name = "vectorStoreJdbcTemplate")
    public JdbcTemplate vectorStoreJdbcTemplate(
            @Qualifier("vectorStoreDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // ── VectorStore (uses Docker pgvector DB) ─────────────────────────────────

    @Bean
    public VectorStore vectorStore(
            @Qualifier("vectorStoreJdbcTemplate") JdbcTemplate jdbcTemplate,
            EmbeddingModel embeddingModel) {
        try {
            float[] vec = embeddingModel.embed("hello");
            log.info("VECTOR SIZE = {}", vec.length);
            PgVectorStore store = PgVectorStore.builder(jdbcTemplate, embeddingModel)
                    .dimensions(3072)
                    .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                    .schemaName("public")
                    .vectorTableName("hotel_embeddings")
                    .initializeSchema(false)
                    .build();

            // Trigger schema init early so we catch missing extension at startup
            store.afterPropertiesSet();
            log.info("✅ PgVectorStore initialised — semantic search ENABLED");
            return store;

        } catch (Exception e) {
            log.warn("⚠️ PgVectorStore unavailable: {}. Falling back to no-op VectorStore — semantic search DISABLED.",
                    e.getMessage());
            return new NoOpVectorStore();
        }
    }

    // =========================================================================
    // No-op VectorStore — fallback when pgvector is not reachable
    // =========================================================================

    static class NoOpVectorStore implements VectorStore {

        @Override
        public void add(List<Document> documents) { /* no-op */ }

        @Override
        public void delete(List<String> idList) { /* no-op */ }

        @Override
        public void delete(Filter.Expression filterExpression) { /* no-op */ }

        @Override
        public List<Document> similaritySearch(SearchRequest request) {
            return List.of();
        }
    }

    // =========================================================================
    // Gemini text-embedding-004 — uses the official google-genai Java SDK
    // so the AQ.xxx key format is handled correctly (same as ChatModel)
    // =========================================================================

    @Slf4j
    static class GeminiEmbeddingModel extends AbstractEmbeddingModel {

        private static final String MODEL = "gemini-embedding-001";

        private final Client genAiClient;

        GeminiEmbeddingModel(String apiKey) {
            this.genAiClient = Client.builder().apiKey(apiKey).build();
        }

        @Override
        public EmbeddingResponse call(EmbeddingRequest request) {
            List<Embedding> embeddings = new ArrayList<>();
            List<String> texts = request.getInstructions();
            try {
                EmbedContentConfig config = EmbedContentConfig.builder()
                        .outputDimensionality(3072)
                        .build();

                EmbedContentResponse response =
                        genAiClient.models.embedContent(MODEL, texts, config);
                List<ContentEmbedding> contentEmbeddings = response.embeddings()
                        .orElseThrow(() -> new RuntimeException("No embeddings returned"));
                for (int i = 0; i < contentEmbeddings.size(); i++) {
                    List<Float> values = contentEmbeddings.get(i).values()
                            .orElseThrow(() -> new RuntimeException("Empty embedding values"));
                    embeddings.add(new Embedding(toFloatArray(values), i));
                }
            } catch (Exception e) {
                log.error("Gemini SDK embedding call failed: {}", e.getMessage(), e);
                throw new RuntimeException("Gemini embedding error: " + e.getMessage(), e);
            }
            return new EmbeddingResponse(embeddings);
        }

        @Override
        public float[] embed(Document document) {
            return embedSingle(document.getText());
        }

        @Override
        public float[] embed(String text) {
            return embedSingle(text);
        }

        private float[] embedSingle(String text) {
            try {
                EmbedContentResponse response = genAiClient.models.embedContent(MODEL, text, null);
                List<ContentEmbedding> embeds = response.embeddings()
                        .orElseThrow(() -> new RuntimeException("No embeddings returned"));
                List<Float> values = embeds.get(0).values()
                        .orElseThrow(() -> new RuntimeException("Empty embedding values"));
                return toFloatArray(values);
            } catch (Exception e) {
                throw new RuntimeException("Gemini embedding error: " + e.getMessage(), e);
            }
        }

        @Override
        public List<float[]> embed(List<String> texts) {
            try {
                EmbedContentResponse response = genAiClient.models.embedContent(MODEL, texts, null);
                List<ContentEmbedding> embeds = response.embeddings()
                        .orElseThrow(() -> new RuntimeException("No embeddings returned"));
                List<float[]> result = new ArrayList<>();
                for (ContentEmbedding ce : embeds) {
                    result.add(toFloatArray(ce.values()
                            .orElseThrow(() -> new RuntimeException("Empty embedding values"))));
                }
                return result;
            } catch (Exception e) {
                throw new RuntimeException("Gemini embedding error: " + e.getMessage(), e);
            }
        }

        private float[] toFloatArray(List<Float> values) {
            float[] arr = new float[values.size()];
            for (int i = 0; i < values.size(); i++) arr[i] = values.get(i);
            return arr;
        }
    }
}