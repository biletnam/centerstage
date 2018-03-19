package com.challenge.centerstage.ticketservice.service.impl;

import com.challenge.centerstage.ticketservice.domain.PerformanceVenue;
import com.challenge.centerstage.ticketservice.domain.SeatHold;
import com.challenge.centerstage.ticketservice.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by debojyoti.
 */
@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    PerformanceVenue performanceVenue;

    /**
     * Call {@link PerformanceVenue#seatsAvailable()}
     *
     * @return the number of tickets available in the venue
     */
    @Override
    public int numSeatsAvailable() {
        return performanceVenue.seatsAvailable();
    }

    /**
     * Call {@link PerformanceVenue#holdSeats(int, String)}
     * @param numSeats      the number of seats to find and hold
     * @param customerEmail unique identifier for the customer
     * @return a SeatHold object identifying the specific seats and related
     * information
     */
    @Override
    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        return performanceVenue.holdSeats(numSeats, customerEmail);
    }

    /**
     * Call {@link PerformanceVenue#reserveSeats(int, String)}
     * @param seatHoldId    the seat hold identifier
     * @param customerEmail the email address of the customer to which the
     *                      seat hold is assigned
     * @return a reservation confirmation code
     */
    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) {
        return performanceVenue.reserveSeats(seatHoldId, customerEmail);
    }
}
