package com.challenge.centerstage.ticketservice;

import com.challenge.centerstage.ticketservice.domain.SeatHold;
import com.challenge.centerstage.ticketservice.service.TicketService;
import com.challenge.centerstage.ticketservice.utils.TheaterSeatMapPrintService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class TicketServiceApplication implements CommandLineRunner {

    private final
    TicketService ticketService;
    @Autowired
    TheaterSeatMapPrintService theaterSeatMapPrintService;

    @Autowired
    public TicketServiceApplication(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TicketServiceApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

    }

    @Override
    public void run(String... args) {
        ExecutorService executor;
        while ((ticketService.numSeatsAvailable() > 0)) {
            System.out.println("Seats available : " + ticketService.numSeatsAvailable());
            executor = Executors.newFixedThreadPool(10);
            while (true) {
                int seats = ticketService.numSeatsAvailable();
                //System.out.println("Seats available inner loop: " + seats);
                if ((seats <= 0)) {
                    System.out.println("Currently all seats are being held or sold out.");
                    break;
                }

                Runnable worker = new MyRunnable(RandomStringUtils.random(4, "abcdefghijklmnopqrstuvwxyz") + "@somedomain.com");
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
        private final String email;

        MyRunnable(String email) {
            this.email = email;
        }

        @Override
        public void run() {

            Random r = new Random();
            int random = r.nextInt((34 - 1) + 1) + 1;
            //System.out.println("Trying to buy : " + random + " tickets.");
            SeatHold seatHold = ticketService.findAndHoldSeats(random, email);
            if (seatHold.getId() != -1) {
                random = r.nextInt((5 - 1) + 1) + 1;
                System.out.println("  Holding " + seatHold.getSeats().size() + " seats... HoldId : " + seatHold.getId() + " for " + random + "secs" + " - Customer ID : " + email);
                try {
                    Thread.sleep(random * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String confirmation = ticketService.reserveSeats(seatHold.getId(), email);
                if (confirmation.contains("Expired")) {
                    String format = ("| %s | %s | %s |%n");
                    System.out.printf(format, "  HoldID : " + seatHold.getId(), "Holding for " + random + "secs", " expired.");
                    return;
                }
                String format = ("||| %s | %s | %s | %s | %s |||%n");
                System.out.printf(format, "HoldID : " + seatHold.getId(), "ConfirmationID : " + confirmation, "Customer ID : " + email, "Total seats : " + seatHold.getSeats().size(), "RESERVED");
                return;
            } else {
                //System.out.println(seatHold.getErrorMessage());
            }
        }

    }
}
