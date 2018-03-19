package com.challenge.centerstage.ticketservice.domain;

/**
 * Created by debojyoti.
 */
public class Seat {

    private int rowId;
    private int chairId;

    public Seat(int rowId, int chairId) {
        this.rowId = rowId;
        this.chairId = chairId;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public int getChairId() {
        return chairId;
    }

    public void setChairId(int chairId) {
        this.chairId = chairId;
    }
}
