package com.example.generators

import com.example.models.{SeatCategory, User, UserType}
import com.example.store.InMemoryStore
import org.joda.time.DateTime

import scala.collection.mutable

trait Generator:
  def movies(): Unit
  def theatres(): Unit
  def shows(): Unit
  def users(): Unit
  def screens(): Unit

object DataGenerator extends Generator:

  import com.example.models.{Movie, Screen, SeatType, Show, Theatre}
  val store = InMemoryStore

  override def movies(): Unit =
    store.movies = store.movies :+ Movie(id= 1, title= "The Matrix", synopsis = "Sci-Fi Fantasy", durationInMinutes = 90)
    store.movies = store.movies :+ Movie(id= 2, title= "Babys Day Out", synopsis = "Comedy", durationInMinutes = 75)
    store.movies = store.movies :+ Movie(id = 3, title = "Dhoom2", synopsis = "Action Drama", durationInMinutes = 150)

  override def theatres(): Unit =
    store.theatres = store.theatres :+ Theatre(id= 1000, name= "Alpha", city= "Delhi", address= "Sarojni Nagar")
    store.theatres = store.theatres :+ Theatre(id = 2000, name = "Beta", city = "Delhi", address = "Lajpat Nagar")
    store.theatres = store.theatres :+ Theatre(id = 3000, name = "Gaama", city = "Delhi", address = "Sundar Nagar")

  override def screens(): Unit =
    store.theatres.foreach(theatre => {
      store.screens = store.screens :+ Screen(id = theatre.id +  1, theatre = theatre, name = "AUDI1", seatsConfig = seatConfigs(), totalRows = 26, totalSeats = 260, 10)
      store.screens = store.screens :+ Screen(id = theatre.id +  2, theatre = theatre, name = "AUDI2", seatsConfig = seatConfigs(), totalRows = 26, totalSeats = 260, 10)
      store.screens = store.screens :+ Screen(id = theatre.id +  3, theatre = theatre, name = "AUDI3", seatsConfig = seatConfigs(), totalRows = 26, totalSeats = 260, 10)
    })

  def seatConfigs() =
    var seats = List[SeatCategory]()
    seats = seats :+ SeatCategory(1, SeatType.GOLD, 10, 10, 200)
    seats = seats :+ SeatCategory(2, SeatType.SILVER, 10, 10, 250)
    seats = seats :+ SeatCategory(3, SeatType.PLATINUM, 10, 10, 300)
    seats

  override def shows(): Unit =
    var nextShowId = 1
    val repeatTimes = 3
    val max = store.movies.size * repeatTimes // movies.size = 3, repeat (per screen per day) = 3, max = 3 * 3 = 9
    // n theatres, m screens = 3 th x 3 sc = 9 screens
    // 9 shows per screen x 9 screens = 9*9 = 81 shows
    var movieNum = 0
    for i <- 1 to max do {
      if movieNum == store.movies.size then movieNum = 0 else movieNum = movieNum % store.movies.size
      val movie = store.movies(movieNum)
      var showsList: mutable.Seq[Show] = mutable.ArraySeq.empty
      showsList = createShows(nextShowId, max, movie)
      nextShowId = nextShowId + showsList.size
      if store.shows.get(movie.id) == None then store.shows(movie.id) = showsList
      else store.shows(movie.id) = store.shows.get(movie.id).get :++ showsList
      movieNum = movieNum + 1
    }

  private def createShows(nextShowId: Int, maxShows: Int, movie: Movie): mutable.Seq[Show] = {
    var showsList: mutable.Seq[Show] = mutable.Seq.empty
    var showId = nextShowId

    store.screens.foreach(screen => {
      val show = createShow(showId, movie, screen).get
      showsList = showsList :+ show
      showId = showId + 1
    })

    showsList
  }

  private def createShow(showId: Int, movie: Movie, screen: Screen): Option[Show] = {
    val seatMap = Array.ofDim[Int](screen.totalRows, screen.colsPerRow)
    val show = Show(
      id = showId, movie = movie, screen = screen,
      startTime = DateTime.now().toLocalDateTime,
      endDateTime = DateTime.now().toLocalDateTime.plusMinutes(movie.durationInMinutes),
      available = screen.totalSeats,
      reserved = 0,
      seatMap = seatMap
    )

    Some(show)
  }

  override def users(): Unit = {
    store.users = store.users :+ User(id = 1, name = "Raj Oberoi", email = "raj.oberoi@somemail.com", phone = "9988998811", userType = UserType.CUSTOMER)
    store.users = store.users :+ User(id = 2, name = "Emma", email = "emma@somemail.com", phone = "9988998811", userType = UserType.CUSTOMER)
    store.users = store.users :+ User(id = 3, name = "Sophia", email = "sophia@somemail.com", phone = "9988998811", userType = UserType.CUSTOMER)
  }

