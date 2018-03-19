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

- Since it is a performance venue, the system tries to assign seats as close to the stage as possible.
- The system always assigns contiguous seating and does not allow the number of seats to hold more than the number of seats per row.
- A customer can hold seats for a certain number of seconds (pre-defined by the system) after which the hold will expire and the seats can be reassigned to the subsequent requests.
- The service can stay open till all seats have been reserved. 


### Building and running the tests and simulation.
- Requires JDK 8.
- Download the zip or clone the Git repository.
- Unzip the zip file (if you downloaded one)
- (cd) to folder containing pom.xml
- to build and run the tests, use command > mvn clean package
- to run the application (Simulation)
    - cd to **target** folder
    - run command > java -jar -Dapplication.numRows=9 -Dapplication.numSeats=33 -Dapplication.holdExpiry=5 -Dapplication.threadCount=10 ticket-service-0.0.1-SNAPSHOT.jar
        - All environment variables have default values assigned and are optional.
        - application.numRows = Number seating rows (default 9).
        - application.numSeats = Number of seats per row (default 33).
        - application.holdExpiry = How long a customer can hold seats (default 20).
        - application.threadCount= number of threads to simulate the concurrent reservation request (default 20). 
- You are all set to see the simulation.

- The application will run a simulation for concurrent booking requests.
    - The application will generate a random set hold request between (1 and (Number of seats per row + 5)).
    - The application will hold the seats on behalf of customer for an random period of time between (1sec and holdExpiry secs).
    - The application will log all requests and responses.
    - At the end when the Venue is sold out, the application will print the reservation seat map with reservation confirmation ID. 
