package com.challenge.centerstage.ticketservice.domain;


import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by debojyoti.
 */
public class SeatHold implements Serializable {

    long expires;
    ArrayList<Seat> seats;
    String errorMessage;
    private int id;
    private String email;

    public SeatHold() {
        this.id = new Integer(RandomStringUtils.randomNumeric(3));
        this.errorMessage = "";
    }

    public SeatHold(int id, String errorMessage) {
        this.id = id;
        this.errorMessage = errorMessage;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public ArrayList<Seat> getSeats() {
        return seats;
    }

    public void setSeats(ArrayList<Seat> seats) {
        this.seats = seats;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
