package com.online.booking.online_booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.online.booking.online_booking.model.Movie;
import com.online.booking.online_booking.model.Show;
import com.online.booking.online_booking.model.Theater;
import com.online.booking.online_booking.model.User;
import com.online.booking.online_booking.model.booking.Booking;
import com.online.booking.online_booking.model.booking.BookingStatus;
import com.online.booking.online_booking.model.seat.Seat;
import com.online.booking.online_booking.model.seat.SeatStatus;

public class MovieTicketBookingSystem {
    private static MovieTicketBookingSystem instance;
    private final List<Movie> movies;
    private final List<Theater> theaters;
    private final Map<String, Show> shows;
    private final Map<String, Booking> bookings;
    private final Set<String> citiesWithOffers = Set.of("City1", "City2");

    private static final String BOOKING_ID_PREFIX = "BKG";
    private static final AtomicLong bookingCounter = new AtomicLong(0);

    private MovieTicketBookingSystem() {
        movies = new ArrayList<>();
        theaters = new ArrayList<>();
        shows = new ConcurrentHashMap<>();
        bookings = new ConcurrentHashMap<>();
       
    }

    public static synchronized MovieTicketBookingSystem getInstance() {
        if (instance == null) {
            instance = new MovieTicketBookingSystem();
        }
        return instance;
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }

    public void addTheater(Theater theater) {
        theaters.add(theater);
    }

    public void addShow(Show show) {
        shows.put(show.getId(), show);
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public List<Theater> getTheaters() {
        return theaters;
    }

    public Show getShow(String showId) {
        return shows.get(showId);
    }

    public synchronized Booking bookTickets(User user, Show show, List<Seat> selectedSeats) {
        Theater theater = show.getTheater();

        // Check if the selected theater is in a city with offers
        boolean hasOffer = citiesWithOffers.contains(theater.getCity());
      
        if (areSeatsAvailable(show, selectedSeats)) {
            markSeatsAsBooked(show, selectedSeats);
            //double totalPrice = calculateTotalPrice(selectedSeats);
            double totalPrice = calculateTotalCost(show, selectedSeats, hasOffer);
            String bookingId = generateBookingId();
            Booking booking = new Booking(bookingId, user, show, selectedSeats, totalPrice, BookingStatus.PENDING);
            bookings.put(bookingId, booking);
            return booking;
        }
        return null;
    }

    private boolean areSeatsAvailable(Show show, List<Seat> selectedSeats) {
        for (Seat seat : selectedSeats) {
            Seat showSeat = show.getSeats().get(seat.getId());
            if (showSeat == null || showSeat.getStatus() != SeatStatus.AVAILABLE) {
                return false;
            }
        }
        return true;
    }

    private void markSeatsAsBooked(Show show, List<Seat> selectedSeats) {
        for (Seat seat : selectedSeats) {
            Seat showSeat = show.getSeats().get(seat.getId());
            showSeat.setStatus(SeatStatus.BOOKED);
        }
    }

    // private double calculateTotalPrice(List<Seat> selectedSeats) {
    //     return selectedSeats.stream().mapToDouble(Seat::getPrice).sum();
    // }

    private double calculateTotalCost(Show show, List<Seat> selectedSeats, boolean hasOffer) {
        double totalCost = 0.0;

        // Apply per-seat pricing and discounts based on the offer
        for (int i = 0; i < selectedSeats.size(); i++) {
            double seatPrice = selectedSeats.get(i).getPrice();

            // 50% discount on the third ticket, if applicable
            if (hasOffer && i == 2) {
                seatPrice *= 0.5;
            }

            totalCost += seatPrice;
        }

        // Apply a 20% discount if the show time is in the afternoon (between 12 PM and 5 PM)
        LocalTime showStartTime = show.getStartTime().toLocalTime();
        if (showStartTime.isAfter(LocalTime.NOON) && showStartTime.isBefore(LocalTime.of(17, 0))) {
            totalCost *= 0.8;
        }

        return totalCost;
    }


    private String generateBookingId() {
        long bookingNumber = bookingCounter.incrementAndGet();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return BOOKING_ID_PREFIX + timestamp + String.format("%06d", bookingNumber);
    }

    public synchronized void confirmBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking != null && booking.getStatus() == BookingStatus.PENDING) {
            booking.setStatus(BookingStatus.CONFIRMED);
            // Process payment and send confirmation
            // ...
        }
    }

    public synchronized void cancelBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking != null && booking.getStatus() != BookingStatus.CANCELLED) {
            booking.setStatus(BookingStatus.CANCELLED);
            markSeatsAsAvailable(booking.getShow(), booking.getSeats());
            // Process refund and send cancellation notification
            // ...
        }
    }

    private void markSeatsAsAvailable(Show show, List<Seat> seats) {
        for (Seat seat : seats) {
            Seat showSeat = show.getSeats().get(seat.getId());
            showSeat.setStatus(SeatStatus.AVAILABLE);
        }
    }

    // New method to browse shows based on movie, town, and date
    public List<Show> browseShows(String movieId, String town, LocalDate date) {
        return shows.values().stream()
                .filter(show -> show.getMovie().getId().equals(movieId)
                        && show.getTheater().getCity().equalsIgnoreCase(town)
                        && show.getStartTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }
}
