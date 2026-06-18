package niketeck.StayNest.service;


import niketeck.StayNest.entity.Booking;

public interface CheckoutService {

    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);

}
