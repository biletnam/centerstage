# CenterStage
A simple ticket service that facilitates the discovery, temporary hold, and final reservation of seats within a high-demand performance venue.


The service can perform the following functions:

1. Find the number of seats available within the venue

    _Note: available seats are seats that are neither held nor reserved._

2. Find and hold the best available seats on behalf of a customer

    _Note: each ticket hold should expire within a set number of seconds._

3. Reserve and commit a specific group of held seats for a customer

The system implements the following interface.
    
    public interface TicketService {
        /**
        * The number of seats in the venue that are neither held nor reserved
        *
        * @return the number of tickets available in the venue
        */
        int numSeatsAvailable();
        /**
        * Find and hold the best available seats for a customer
        *
        * @param numSeats the number of seats to find and hold
        * @param customerEmail unique identifier for the customer
        * @return a SeatHold object identifying the specific seats and related
        information
        */
        SeatHold findAndHoldSeats(int numSeats, String customerEmail);
        /**
        * Commit seats held for a specific customer
        *
        * @param seatHoldId the seat hold identifier
        * @param customerEmail the email address of the customer to which the
        seat hold is assigned
        * @return a reservation confirmation code
        */
        String reserveSeats(int seatHoldId, String customerEmail);
    }

Most of the heavy lifting is done by the **_com.challenge.centerstage.ticketservice.domain.PerformanceVenue.java_** class. Required variables and methods have been made Thread-Safe to handle concurrent requests.

There are few **assumptions** that the system makes.

* Since it is a performance venue, the system tries to assign seats as close to the stage as possible.
* The system always assigns contiguous seating and does not allow the number of seats to hold more than the number of seats per row.
* A customer can hold seats for a certain number of seconds (pre-defined by the system) after which the hold will expire and the seats can be reassigned to the subsequent requests.
* The service can stay open till all seats have been reserved. 


#Installing
