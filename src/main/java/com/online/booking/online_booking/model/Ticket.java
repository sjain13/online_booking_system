package com.online.booking.online_booking.model;




public class Ticket {
 
    private Long id;
    private Long showId;
    private int numberOfTickets;
    private boolean isBulkBooking;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShowId() {
        return showId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public void setNumberOfTickets(int numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public boolean isBulkBooking() {
        return isBulkBooking;
    }

    public void setBulkBooking(boolean isBulkBooking) {
        this.isBulkBooking = isBulkBooking;
    }
}