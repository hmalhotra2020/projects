package com.example.models
import com.github.nscala_time.time.Imports.*
import scala.collection.mutable

enum BookingStatus { case PENDING, CONFIRMED, CANCELLED }
enum SeatType { case ANY, SILVER, GOLD, PLATINUM, PREMIUM }
enum SeatStatus(val status: Int) { case AVAILABLE extends SeatStatus(0); case BOOKED extends SeatStatus(1) }
enum UserType { case CUSTOMER, STAFF, ADMIN }

case class Theatre(id: Int, name: String, city: String, address: String)
case class Screen(id: Int, theatre: Theatre, name: String, seatsConfig: List[SeatCategory], totalSeats: Int, totalRows: Int, colsPerRow: Int)
case class BookedSeat(id: Int, row: Int, col: Int, seatType: SeatType, screenName: String, price: Int = 220)
case class Booking(id: Int, user: User, show: Show, seats: mutable.Seq[BookedSeat])
case class User(id: Int, name: String, email: String, phone: String, userType: UserType)
case class Movie(id: Int, title: String, synopsis: String, durationInMinutes: Int)
case class Show(id: Int, movie: Movie, screen: Screen, startTime: LocalDateTime, endDateTime: LocalDateTime, available: Int, reserved: Int, var seatMap: Array[Array[Int]])
case class SeatCategory(order: Int, seatType: SeatType, rows: Int, cols: Int, price: Int)
