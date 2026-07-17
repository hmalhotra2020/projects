package com.example

import com.example.generators.*
import com.example.models.*
import com.example.models.SeatType.{ANY, GOLD}
import com.example.services.{BookingService, SearchService, SeatReq, SeatService}
import com.example.store.InMemoryStore
import scala.collection.mutable

@main
def main(): Unit =
  val m1 = Theatre
  val searchService = SearchService
  val seatService = SeatService
  val bookingService = BookingService
  val store = InMemoryStore
  val dg = DataGenerator
  dg.movies()
  dg.users()
  dg.theatres()
  dg.screens()
  dg.shows()

  val user1 = store.users.head
  val user2 = store.users(1)
  val user3 = store.users(2)
  val movie1 = searchService.getMovieById(2)

  println("\nMovies List: ")
  pprint.pprintln(store.movies)
  //println(store.movies)

  val shows = searchService.getAllShowsInTheatresForMovie(1)
  println("\nShows List: ")
  //println(shows)
  pprint.pprintln(shows)

  val moviesMap = searchService.getMoviesByShowsInTheatre
  println("\nMovies List with Theatres: ")
  pprint.pprintln(moviesMap) // ideally to be converted to Map[ Movie, Map[Theatre, Seq[Show without screen&theatre info]] ]
  //println(moviesMap)

  seatService.autoAllocateSeats(user1, movie1, searchService.getShowById(11, movie1), SeatReq(3, ANY))
  pprint.pprintln( "\n" + seatService.seatMap(searchService.getShowById(11, movie1)).mkString(", ") )

  seatService.autoAllocateSeats(user2, movie1, searchService.getShowById(11, movie1), SeatReq(5, ANY))
  pprint.pprintln( "\n" +  seatService.seatMap(searchService.getShowById(11, movie1)).mkString(", ") )

  seatService.autoAllocateSeats(user3, movie1, searchService.getShowById(11, movie1), SeatReq(7, ANY))
  pprint.pprintln( "\n" +  seatService.seatMap(searchService.getShowById(11, movie1)).mkString(", ") )

  seatService.autoAllocateSeats(user1, movie1, searchService.getShowById(11, movie1), SeatReq(5, ANY))
  pprint.pprintln( "\n" +  seatService.seatMap(searchService.getShowById(11, movie1)).mkString(", ") )

  var booking = bookingService.book(
    user = user1,
    movie = movie1,
    show = searchService.getShowById(11, movie1),
    seatReq = SeatReq(noSeats = 4, ANY, mutable.ArraySeq( (5, 0), (5, 1), (5, 2), (5, 3) ) ))

  pprint.pprintln("\nSeats Booked: ")
  //pprint.pprintln(booking)
  println(booking)




  booking = bookingService.book(
    user = user1,
    movie = movie1,
    show = searchService.getShowById(11, movie1),
    seatReq = SeatReq(noSeats = 4, ANY, mutable.ArraySeq( (5, 0), (5, 1), (5, 2), (5, 3) ) ))

  pprint.pprintln(s"\nSeats Booked again: ")
  pprint.pprintln(booking)

  println("\nPrint Show 11 seatmap")
  pprint.pprintln( seatService.seatMap(searchService.getShowById(11, movie1)).mkString(", ") )

  println("\nShow My Bookings for user1: ")
  pprint.pprintln(InMemoryStore.bookings.get(user1.id))
  //println(InMemoryStore.bookings.get(user1.id))

/*
showMoviesList √
getThreatresByMovie √
getMoviesByShowsInTheatre √
autoAllocateSeats √ // loop each row and send back any suitable
seatMap √ // dump seatMap converted to string or show if seats avaliable in a show
book √ // reserve and return composed booking object and keep bookings records per user
showMyBookings √ // dump all bookings for cust

Remaining
  - Seat configuration is flat, could be dynamic like different category of rows hv different no of seats
  - As above, SeatCategory is defined but nt considered while booking or autoAllocation
  - AutoAllocation itself to be utilized while booking
  - SeatConfig should hv (SeatCategory, Block[Row, COl]), Blocks are nt considered
  - Seat preferences to be considered while autoAllocation like corner, category, gaps etc
*/
