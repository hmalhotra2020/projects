package com.example.services

import com.example.models.{BookedSeat, Booking, Movie, SeatCategory, SeatStatus, SeatType, Show, User}
import com.example.store.InMemoryStore

import scala.collection.mutable
import scala.collection.mutable.{Map, Seq}

trait SearchServiceTrait:
  def getAllShowsInTheatresForMovie(movieId: Int): Option[Seq[Show]]
  def getMoviesByShowsInTheatre: Option[Map[Movie, Seq[Show]]]
  def getShowById(id: Int, movie: Movie): Show
  def getMovieById(id: Int): Movie

trait SeatServiceTrait:
  def seatMap(show: Show): Seq[String]
  def autoAllocateSeats(user: User, movie: Movie, show: Show, seatReq: SeatReq): Unit

trait MovieServiceTrait:
  def moviesList: Seq[Movie]

trait BookingServiceTrait:
  def book(user: User, movie: Movie, show: Show, seatReq: SeatReq): Option[Booking]
  def showMyBookings(userId: Int): Option[_] // Option[scala.collection.mutable.Seq[Booking]

object SearchService extends SearchServiceTrait {
  override def getAllShowsInTheatresForMovie(movieId: Int): Option[Seq[Show]] = {
    val store = InMemoryStore
    store.shows.get(movieId)
  }
  override def getMoviesByShowsInTheatre: Option[Map[Movie, Seq[Show]]] = {
    val store = InMemoryStore
    val newMoviesMap = store.shows.map((k, v) => (store.movies.find(m => (m.id == k)).get, newShow(v)))
    Some(newMoviesMap)
  }
  override def getShowById(id: Int, movie: Movie): Show = {
    val store = InMemoryStore;
    val showsList = store.shows.get(movie.id).get
    val filtered = showsList.filter(s => s.id == id)
    filtered.head
  }
  override def getMovieById(id: Int): Movie = {
    val store = InMemoryStore;
    val filtered = store.movies.filter(m => m.id == id)
    filtered.head
  }
  private def newShow(showSeq: Seq[Show]): Seq[Show] = {
    val newSeq = showSeq.map(s => Show(s.id, s.movie, s.screen, s.startTime, s.endDateTime, s.available, s.reserved, null))
    newSeq
  }
}

object SeatService extends SeatServiceTrait {
  override def seatMap(show: Show): Seq[String] = {
    val printableSeatMap = show.seatMap.map(row => row.mkString)
    printableSeatMap
  }
  override def autoAllocateSeats(user: User, movie: Movie, show: Show, seatReq: SeatReq): Unit = {
    val rowSize = show.screen.totalRows
    val colsSize = show.screen.colsPerRow
    for r <- 0 to rowSize do
      var row = show.seatMap(r)
      val index = checkRow(row, seatReq.noSeats)
      if index > -1 then
        row = row.patch(index, Seq.fill(seatReq.noSeats)(1), seatReq.noSeats)
        show.seatMap(r) = row
        println(s"Seats Allocated in Row: $r, Starting form Seat: $index")
        return
    println(s"Could not find seats, allocate manually.")
  }
  private def checkRow(row: Array[Int], noSeats: Int): Int = {
    var index = 0
    while index < row.length do
      if SeatStatus.AVAILABLE.ordinal == row(index) then // starting empty seat found
        val (found, noOfColsToSkip): (Boolean, Int) = checkNextNSeats(row, index, noSeats)
        if !found then index = index + noOfColsToSkip
        else return index
      index = index + 1
    -1
  }
  private def checkNextNSeats(row: Array[Int], index: Int, noSeats: Int): (Boolean, Int) =
    val max = index + noSeats
    if max > row.length then return (false, index)
    for i <- index to max if i < row.length do if SeatStatus.AVAILABLE.ordinal != row(index) then return (false, i)
    (true, index)
}

object MovieService extends MovieServiceTrait:
  override def moviesList: Seq[Movie] = InMemoryStore.movies

object BookingService extends BookingServiceTrait :
  override def book(user: User, movie: Movie, show: Show, seatReq: SeatReq): Option[Booking] = {
    val seatMap = show.seatMap
    var bookedSeats = mutable.Seq[BookedSeat]()
    seatReq.mapSeats.foreach( (row, col) => {
      if seatMap(row)(col) != SeatStatus.AVAILABLE.ordinal then
        println(s"Seat on row: $row, col: $col already booked")
        return None
    })
    seatReq.mapSeats.foreach( (row, col) => {
      show.seatMap(row)(col) = SeatStatus.BOOKED.ordinal
      val price = getPrice(show, row, col)
      bookedSeats = bookedSeats.appended(BookedSeat(col+1, row, col, SeatType.ANY, show.screen.name))
    })
    val booking = Booking(id = 1, user = user, show = show, seats = bookedSeats)
    val optionalSeq: Option[scala.collection.mutable.Seq[Booking]] = InMemoryStore.bookings.get(user.id)
    optionalSeq match {
      case Some(seq: scala.collection.mutable.ArrayBuffer[_]) => { InMemoryStore.bookings.putIfAbsent(user.id, seq.append(booking)) }
      case None => InMemoryStore.bookings.putIfAbsent(user.id, scala.collection.mutable.ArrayBuffer(booking))
    }
    Some(booking)
  }
  override def showMyBookings(userId: Int): Option[_] = { InMemoryStore.bookings.get(userId) }
  private def getPrice(show: Show, row: Int, col: Int): Unit = {
    //iterate over seatConfig in order and see thru pattern match on row range
    val seatsConfig : List[SeatCategory] = show.screen.seatsConfig
    seatsConfig.foreach(seat => {
      
    })
  }

case class SeatReq(noSeats: Int, seatCategory: SeatType, mapSeats: Seq[Tuple2[Int, Int]] = Seq())
/*
More Auto Allocation use cases
  - In each row just check if next available consecutive seats can be directly allocated
  - Consider while allocating customer don't want to sit just next to other person, thr should be gap
  - If many empty seats, preserve good consecutive free seats for next customers group
  - If many seats ar full, allocate different rows seats
  - Consider user preferences like SeatCategory - GOLD, PREMIUM etc
*/
