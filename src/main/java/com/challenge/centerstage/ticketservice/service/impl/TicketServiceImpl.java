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

    @Override
    public int numSeatsAvailable() {
        return performanceVenue.seatsAvailable();
    }

    @Override
    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        return performanceVenue.holdSeats(numSeats, customerEmail);
    }

    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) {
        return performanceVenue.reserveSeats(seatHoldId, customerEmail);
    }
}
