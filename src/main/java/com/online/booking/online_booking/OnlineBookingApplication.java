package com.online.booking.online_booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.online.booking.online_booking.model.Movie;
import com.online.booking.online_booking.model.Show;
import com.online.booking.online_booking.model.Theater;
import com.online.booking.online_booking.model.User;
import com.online.booking.online_booking.model.booking.Booking;
import com.online.booking.online_booking.model.seat.Seat;
import com.online.booking.online_booking.model.seat.SeatStatus;
import com.online.booking.online_booking.model.seat.SeatType;

@SpringBootApplication
public class OnlineBookingApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(OnlineBookingApplication.class, args);
	}

	
	@Override
	public void run(String... args) throws Exception {
		MovieTicketBookingSystem bookingSystem = MovieTicketBookingSystem.getInstance();

        // Add movies
        Movie movie1 = new Movie("M1", "Movie 1", "Description 1", 120);
        Movie movie2 = new Movie("M2", "Movie 2", "Description 2", 135);
        bookingSystem.addMovie(movie1);
        bookingSystem.addMovie(movie2);

        // Add theaters
        Theater theater1 = new Theater("T1", "Theater 1", "Location 1", "City1", new ArrayList<>());
        Theater theater2 = new Theater("T1", "Theater 1", "Location 1", "City2", new ArrayList<>());
        bookingSystem.addTheater(theater1);
        bookingSystem.addTheater(theater2);

        // Add shows
        Show show1 = new Show("S1", movie1, theater1, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(movie1.getDurationInMinutes()), createSeats(10, 10));
        Show show2 = new Show("S2", movie2, theater2, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(movie2.getDurationInMinutes()), createSeats(8, 8));

		// Add afternoon show
		Show show3 = new Show("S1", movie1, theater1, LocalDateTime.now().withHour(14),
        LocalDateTime.now().withHour(16), createSeats(10, 10));
		
        bookingSystem.addShow(show1);
        bookingSystem.addShow(show2);
		bookingSystem.addShow(show3);

        // Book tickets
        User user = new User("U1", "Shipra Jain", "shipra@example.com");
        //List<Seat> selectedSeats = Arrays.asList(show1.getSeats().get("1-5"), show1.getSeats().get("1-6"));
		List<Seat> selectedSeats = Arrays.asList(
			show3.getSeats().get("1-5"), 
			show3.getSeats().get("1-6"), 
			show3.getSeats().get("1-7"));
        Booking booking = bookingSystem.bookTickets(user, show1, selectedSeats);
        if (booking != null) {
            System.out.println("Booking successful. Booking ID: " + booking.getId());
			System.out.println("Total cost with discounts: " + booking.getTotalPrice());
            bookingSystem.confirmBooking(booking.getId());
        } else {
            System.out.println("Booking failed. Seats not available.");
        }

		// Test browseShows functionality
        String movieId = "M1";
        String town = "City1";
        LocalDate date = LocalDate.now();

        List<Show> shows = bookingSystem.browseShows(movieId, town, date);
        System.out.println("Shows available for movie ID " + movieId + " in town " + town + " on date " + date + ":");
        for (Show show : shows) {
            System.out.println("Show ID: " + show.getId() + ", Theater: " + show.getTheater().getName() +
                    ", Start Time: " + show.getStartTime() + ", End Time: " + show.getEndTime());
        }

        // Cancel booking
        bookingSystem.cancelBooking(booking.getId());
        System.out.println("Booking canceled. Booking ID: " + booking.getId());
    }

    private static Map<String, Seat> createSeats(int rows, int columns) {
        Map<String, Seat> seats = new HashMap<>();
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= columns; col++) {
                String seatId = row + "-" + col;
                SeatType seatType = (row <= 2) ? SeatType.PREMIUM : SeatType.NORMAL;
                double price = (seatType == SeatType.PREMIUM) ? 150.0 : 100.0;
                Seat seat = new Seat(seatId, row, col, seatType, price, SeatStatus.AVAILABLE);
                seats.put(seatId, seat);
            }
        }
        return seats;
	
	}

	

}
