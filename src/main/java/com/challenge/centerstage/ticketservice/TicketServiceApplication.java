package com.challenge.centerstage.ticketservice;

import com.challenge.centerstage.ticketservice.domain.SeatHold;
import com.challenge.centerstage.ticketservice.service.TicketService;
import com.challenge.centerstage.ticketservice.utils.TheaterSeatMapPrintService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class TicketServiceApplication implements CommandLineRunner {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TicketServiceApplication.class);

    @Autowired
    TicketService ticketService;

    @Autowired
    TheaterSeatMapPrintService theaterSeatMapPrintService;

    @Value("${application.numSeats}")
    private int seatsPerRowCount;

    @Value("${application.holdExpiry}")
    private int EXPIRY;

    @Value("${application.threadCount}")
    private int threadCount;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TicketServiceApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

    }

    @Override
    public void run(String... args) {
        ExecutorService executor;

        // Keep the booking open till all seats are reserved
        while ((ticketService.numSeatsAvailable() > 0)) {
            LOGGER.info("Venue is open of reservation");
            LOGGER.info("Seats available : " + ticketService.numSeatsAvailable());
            executor = Executors.newFixedThreadPool(threadCount);
            while (true) {
                int seats = ticketService.numSeatsAvailable();
                if ((seats <= 0)) {
                    System.out.println("Currently all seats are being held or sold out.");
                    break;
                }

                //Generate a random email address and run the booking thread.
                Runnable worker = new MyRunnable(RandomStringUtils.random(4, "abcdefghijklmnopqrstuvwxyz") + "@somedomain.com", seatsPerRowCount, EXPIRY);
                executor.execute(worker);
            }


            executor.shutdown();
            // Wait until all threads are finish
            while (!executor.isTerminated()) {

            }
        }
        System.out.println("\nVENUE IS NOW FULL AND SOLD OUT !!!");
        System.out.println();
        System.out.println(" ----- Venue Seat Map -----");
        System.out.println();
        theaterSeatMapPrintService.printSeatMap();
        System.out.println();


    }

    class MyRunnable implements Runnable {

        private final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MyRunnable.class);
        private final String email;
        private int seatsPerRowCount;
        private int expiry;

        public MyRunnable(String email, int seatsPerRowCount, int expiry) {
            this.email = email;
            this.seatsPerRowCount = seatsPerRowCount;
            this.expiry = expiry;
        }

        @Override
        public void run() {

            Random r = new Random();
            int random = r.nextInt((seatsPerRowCount + 5 - 1) + 1) + 1;
            //System.out.println("Trying to buy : " + random + " tickets.");
            SeatHold seatHold = ticketService.findAndHoldSeats(random, email);
            if (seatHold.getId() != -1) {
                random = r.nextInt((expiry - 1) + 1) + 1;
                LOGGER.info("  Holding " + seatHold.getSeats().size() + " seats... HoldId : " + seatHold.getId() + " for " + random + "secs" + " - Customer ID : " + email);
                try {
                    Thread.sleep(random * 1000);
                } catch (InterruptedException e) {
                }
                String confirmation = ticketService.reserveSeats(seatHold.getId(), email);
                if (confirmation.contains("Expired")) {
                    String format = ("| %s | %s | %s |%n");
                    LOGGER.info("  HoldID : " + seatHold.getId() + " Holding for " + random + "secs" + " - EXPIRED.");
                    return;
                }
                String format = ("||| %s | %s | %s | %s | %s |||%n");
                LOGGER.info("RESERVED - HoldID : " + seatHold.getId() + " ConfirmationID : " + confirmation + " Customer ID : " + email + " Total seats : " + seatHold.getSeats().size());
                return;
            } else {
                if (seatHold.getErrorMessage().contains("Party size")) {
                    LOGGER.info(seatHold.getErrorMessage());
                }
            }
        }

    }
}
