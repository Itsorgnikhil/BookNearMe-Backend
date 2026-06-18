package niketeck.StayNest.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import niketeck.StayNest.entity.Hotel;
import niketeck.StayNest.repository.HotelRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class HotelVectorInitializer {

    private final HotelRepository hotelRepository;
    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeHotelVectors() {
        log.info("Starting hotel vector initialization...");
        try {
            List<Hotel> activeHotels = hotelRepository.findByActive(true);
            List<Document> documents = new ArrayList<>();

            for (Hotel hotel : activeHotels) {
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("hotelId", hotel.getId());
                metadata.put("city", hotel.getCity());
                metadata.put("name", hotel.getName());

                String amenitiesJoined = hotel.getAmenities() != null ? String.join(", ", hotel.getAmenities()) : "";
                String content = String.format("Hotel: %s, City: %s, Amenities: %s", 
                        hotel.getName(), hotel.getCity(), amenitiesJoined);

                Document doc = new Document(String.valueOf(hotel.getId()), content, metadata);
                documents.add(doc);
            }

            if (!documents.isEmpty()) {

                for (Document doc : documents) {

                    float[] vec = embeddingModel.embed(doc.getText());

                    log.info("VECTOR LENGTH = {}", vec.length);

                    vectorStore.add(List.of(doc));

                    log.info("INSERTED SUCCESSFULLY");
                }

                log.info("Hotel embeddings initialized: {} hotels indexed", documents.size());
            } else {
                log.info("No active hotels found to initialize vector store.");
            }
        }catch (Exception e) {

            Throwable root = e;

            while (root.getCause() != null) {
                root = root.getCause();
            }

            root.printStackTrace();

            log.error("ROOT ERROR = {}", root.getMessage(), root);
        }
    }
}
