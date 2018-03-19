package com.challenge.centerstage.ticketservice.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class PerformanceVenueTest {

    @Autowired
    PerformanceVenue performanceVenue;
    @Value("${application.numRows}")
    private int seatingRowCount;
    @Value("${application.numSeats}")
    private int seatsPerRowCount;
    @Value("#{${application.holdExpiry}*1000}")
    private int HOLD_EXPIRY;

    @Test
    public void seatsAvailable() {
        performanceVenue.holdSeats(5, "hello@world.com");
        try {
            Thread.sleep(HOLD_EXPIRY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(seatingRowCount * seatsPerRowCount >= performanceVenue.seatsAvailable());
    }

    @Test
    public void holdSeats() {
        performanceVenue.holdSeats(seatsPerRowCount, "hi@there.com");
        Assert.assertEquals(seatsPerRowCount, performanceVenue.holdSeats(seatsPerRowCount, "hello@world.com").seats.size());
        Assert.assertEquals("Party size cannot exceeds the number of seats per row.", performanceVenue.holdSeats(seatsPerRowCount + 1, "hello@world.com").errorMessage);
    }

    @Test
    public void reserveSeats() {
        String regex = "^[a-zA-Z0-9]+$";

        Pattern pattern = Pattern.compile(regex);

        SeatHold seatHold = performanceVenue.holdSeats(seatsPerRowCount, "hi@there.com");

        Matcher matcher = pattern.matcher(performanceVenue.reserveSeats(seatHold.getId(), "hi@there.com"));
        Assert.assertTrue(matcher.matches());
        try {
            Thread.sleep(HOLD_EXPIRY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("Error : Expired", performanceVenue.reserveSeats(seatHold.getId(), "hi@there.com"));
        Assert.assertEquals("Error : The email did not match the our record.", performanceVenue.reserveSeats(seatHold.getId(), "hello@there.com"));
    }

    @TestConfiguration
    static class PerformanceVenueTestContextConfiguration {
        @Bean
        public PerformanceVenue performanceVenue() {
            return new PerformanceVenue();
        }
    }
}