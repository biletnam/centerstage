package com.challenge.centerstage.ticketservice.domain;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by debojyoti.
 */
@Component
public class PerformanceVenue {


    private final Map<Integer, SeatHold> seatHoldMap = new HashMap<>();
    private final AtomicInteger seatHoldCount = new AtomicInteger(0);
    @Value("${application.numRows}")
    private int seatingRowCount;
    @Value("${application.numSeats}")
    private int seatsPerRowCount;
    @Value("#{${application.holdExpiry}*1000}")
    private int HOLD_EXPIRY;
    private long[][] seatHoldMatrix;
    private String[][] seatReservedMatrix;

    /**
     * Initialize
     */
    @PostConstruct
    void initializeVenue() {
        if (this.seatHoldMatrix == null) {
            this.seatHoldMatrix = new long[this.getSeatingRowCount()][this.getSeatsPerRowCount()];
            Arrays.stream(seatHoldMatrix).forEach(seatStates -> Arrays.fill(seatStates, 0L));
        }
        if (this.seatReservedMatrix == null) {
            this.seatReservedMatrix = new String[this.getSeatingRowCount()][this.getSeatsPerRowCount()];
            Arrays.stream(seatReservedMatrix).forEach(seatStates -> Arrays.fill(seatStates, "0"));
        }
    }

    /**
     * This will check for expired held sets and decrement the counter
     * holding the total number of seats reserved or held.
     *
     * @return int - Count of available seats excluding the seats that are currently being held or reserved.
     */
    public int seatsAvailable() {
        int rowCounter = 0;
        while (rowCounter < seatingRowCount) {
            long[] holding = seatHoldMatrix[rowCounter];
            int seatCounter = 0;
            while (seatCounter < seatsPerRowCount) {
                long currentTimestamp = System.currentTimeMillis();
                // check if the current seat is being held and if the hold has expired
                if (holding[seatCounter] > 0L && (currentTimestamp > holding[seatCounter])) {
                    holding[seatCounter] = 0L;
                    seatHoldCount.decrementAndGet();
                }
                seatCounter++;
            }
            rowCounter++;
        }
        return (seatingRowCount * seatsPerRowCount) - seatHoldCount.intValue();
    }


    /**
     * Holds requested number of contiguous seats.
     * Requested number of seats cannot be more that the number of seats per row.
     *
     * @param partyCount requested number of seats to hold
     * @param email      Customer's unique identifier.
     * @return {@link SeatHold} that contains an unique hold ID along with held seat position
     */
    public synchronized SeatHold holdSeats(int partyCount, String email) {

        if (partyCount > seatsPerRowCount) {
            return new SeatHold(-1, "Party size cannot exceeds the number of seats per row.");
        }
        // The list of seats seatHoldMatrix
        ArrayList<Seat> seats = new ArrayList<>();

        // Starts lookup for best block of  adjacent seating, starting from the first row.
        int rowCounter = 0;
        while (rowCounter < seatingRowCount) {

            // start seat location for the best block of adjacent free seats for the requested party
            int startSeat = adjacentSeatingLookup(rowCounter, partyCount);

            // Check if adjacent seating was found in the current row.
            if (startSeat >= 0) {

                // create seats to return
                for (int i = 1; i <= partyCount; i++) {
                    seats.add(new Seat(rowCounter + 1, startSeat + i));
                }

                // Seats are seatHoldMatrix now, so we can stop checking rows
                break;
            }
            rowCounter++;
        }
        // Simple check to make sure we could actually markHolding the required number of seats
        long expiresAt = System.currentTimeMillis() + HOLD_EXPIRY;
        if (seats.size() == partyCount) {
            try {
                seats.forEach((Seat seat) -> markHolding(seat.getRowId() - 1, seat.getChairId() - 1, expiresAt));
            } catch (Throwable e) {
                return new SeatHold(-1, e.getMessage());
            }
            // Generate the SeatHold object and return;
            SeatHold seatHold = new SeatHold();
            seatHold.setEmail(email);
            seatHold.setSeats(seats);
            seatHold.setExpires(expiresAt);
            seatHoldMap.put(seatHold.getId(), seatHold);
            return seatHold;
        } else {
            return new SeatHold(-1, "All seats are currently being held or sold out");
        }
    }

    /**
     * Reserves an existing {@link SeatHold} held seats, if the hold has not expired.
     *
     * @param seatHoldId    the seat hold identifier
     * @param customerEmail the email address of the customer to which the
     *                      seat hold is assigned
     * @return Reservation confirmation code or error messages
     */
    public String reserveSeats(int seatHoldId, String customerEmail) {
        SeatHold seatHold = seatHoldMap.get(seatHoldId);
        if (seatHold.getEmail().equalsIgnoreCase(customerEmail)) {

            final String confirmationCode = RandomStringUtils.randomAlphanumeric(3).toUpperCase();

            long currentTimestamp = System.currentTimeMillis();

            // Check for expiry.
            if (currentTimestamp > seatHold.getExpires()) {
                return "Error : Expired";
            }
            seatHold.getSeats().forEach((Seat seat) -> markReserved(seat.getRowId() - 1, seat.getChairId() - 1, confirmationCode));

            return confirmationCode;
        } else {
            return "Error : The email did not match the our record.";
        }
    }

    /**
     * Find the best available adjacent seats
     *
     * @param row        lookup row position
     * @param partyCount number of seats to hold
     * @return starting position of best adjacent block of seats
     */
    private synchronized int adjacentSeatingLookup(int row, int partyCount) {

        long[] rowToCheckHold = seatHoldMatrix[row]; // Check empty or hold block of seats in this row.
        String[] rowToCheckReserve = seatReservedMatrix[row]; // Check reserved block of seats in this row.


        int seatPointer = -1; // starting seat position to hold the party count.

        int i = 0;
        while (i < rowToCheckHold.length) { // loop while initial free block of adjacent seats are found

            long currentTime = System.currentTimeMillis();

            // Check for SeatHold Expiration time and not reserved.
            if ((rowToCheckHold[i] >= 0L) && (currentTime > rowToCheckHold[i]) && (rowToCheckReserve[i].equals("0"))) {

                // Hold time expired.
                if (seatPointer == -1) {
                    seatPointer = i; // point to the current seat
                }

                if ((partyCount == ((i - seatPointer) + 1))) {
                    return seatPointer; //adjacent seats are available
                }
            } else {
                seatPointer = -1; // reset pointer for next row lookup.
            }
            i++;
        }
        return -1;
    }

    /**
     * record seats being held
     *
     * @param row        row position of seats to hold
     * @param start      first seat position
     * @param expiryTime hold time expiration
     */
    private synchronized void markHolding(int row, int start, long expiryTime) {
        long[] holding = seatHoldMatrix[row];

        int i = start;
        while (i < (start + 1)) {
            holding[i] = expiryTime; // mark being held with expiryTime.
            seatHoldCount.incrementAndGet();
            i++;
        }
    }

    /**
     * record seat reservation
     *
     * @param row              row position of seats to reserve
     * @param start            first seat position
     * @param confirmationCode reservation confirmation code
     */
    private synchronized void markReserved(int row, int start, String confirmationCode) {
        long[] holding = seatHoldMatrix[row];
        String[] reserved = seatReservedMatrix[row];
        int i = start;
        while (i < (start + 1)) {
            // mark reserved spots in seat hold matrix so that the seats cannot be reassigned
            holding[i] = -1L;
            // Add the confirmation code to seat reserved matrix
            reserved[i] = confirmationCode;
            i++;
        }
    }

    public int getSeatingRowCount() {
        return seatingRowCount;
    }

    public int getSeatsPerRowCount() {
        return seatsPerRowCount;
    }

    public synchronized String[][] getSeatReservedMatrix() {
        return seatReservedMatrix;
    }
}
