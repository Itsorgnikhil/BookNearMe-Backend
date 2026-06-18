package niketeck.StayNest.service;

import com.stripe.model.Event;
import niketeck.StayNest.dto.BookingDto;
import niketeck.StayNest.dto.BookingRequest;

import niketeck.StayNest.dto.HotelReportDto;
import niketeck.StayNest.entity.enums.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<Long> guestIdList);

    String initiatePayments(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    BookingStatus getBookingStatus(Long bookingId);

    List<BookingDto> getAllBookingsByHotelId(Long hotelId);

    HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);

    List<BookingDto> getMyBookings();
}

