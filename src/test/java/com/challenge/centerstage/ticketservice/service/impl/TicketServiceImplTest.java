package com.challenge.centerstage.ticketservice.service.impl;

import com.challenge.centerstage.ticketservice.domain.PerformanceVenue;
import com.challenge.centerstage.ticketservice.domain.Seat;
import com.challenge.centerstage.ticketservice.domain.SeatHold;
import com.challenge.centerstage.ticketservice.service.TicketService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class TicketServiceImplTest {

    @Autowired
    TicketService ticketService;
    @MockBean
    PerformanceVenue performanceVenue;
    @MockBean
    SeatHold seatHold;
    @MockBean
    Seat seat;

    @Test
    public void numSeatsAvailable() {

        Mockito.when(performanceVenue.seatsAvailable()).thenReturn(25);

        Assert.assertEquals(25, ticketService.numSeatsAvailable());

    }

    @Test
    public void findAndHoldSeats() {
        Mockito.when(performanceVenue.holdSeats(5, "hello@world.com")).thenReturn(seatHold);
        Assert.assertSame(seatHold, ticketService.findAndHoldSeats(5, "hello@world.com"));
    }

    @Test
    public void reserveSeats() {
        Mockito.when(performanceVenue.reserveSeats(seatHold.getId(), "hello@world.com")).thenReturn("ABC");
        Assert.assertEquals("ABC", ticketService.reserveSeats(seatHold.getId(), "hello@world.com"));
    }

    @TestConfiguration
    static class TicketServiceImplTestContextConfiguration {

        @Bean
        public TicketService ticketService() {
            return new TicketServiceImpl();
        }
    }

}