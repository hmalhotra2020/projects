# theatrebooking
 A fresh theatre booking app in scala3 for system/logical design leaners

**Sample Output**

```declarative
Movies List: 
ArrayBuffer(
  Movie(id = 1, title = "The Matrix", synopsis = "Sci-Fi Fantasy", durationInMinutes = 90),
  Movie(id = 2, title = "Babys Day Out", synopsis = "Comedy", durationInMinutes = 75),
  Movie(id = 3, title = "Dhoom2", synopsis = "Action Drama", durationInMinutes = 150)
)
```

```declarative
Seats Allocated in Row: 0, Starting form Seat: 0
1110000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000
Seats Allocated in Row: 0, Starting form Seat: 3
1111111100, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000
Seats Allocated in Row: 1, Starting form Seat: 0
1111111100, 1111111000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000
Seats Allocated in Row: 2, Starting form Seat: 0
1111111100, 1111111000, 1111100000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000
```

```declarative
Seats Booked:
Some(Booking(1,User(1,Raj Oberoi,raj.oberoi@somemail.com,9988998811,CUSTOMER),Show(11,Movie(2,Babys Day Out,Comedy,75),Screen(1002,Theatre(1000,Alpha,Delhi,Sarojni Nagar),AUDI2,List(SeatCategory(1,GOLD,10,10,200), SeatCategory(2,SILVER,10,10,250), SeatCategory(3,PLATINUM,10,10,300)),260,26,10),2025-03-28T15:39:52.739,2025-03-28T16:54:52.739,260,0,[[I@25d250c6),ArrayBuffer(BookedSeat(1,5,0,ANY,AUDI2,220), BookedSeat(2,5,1,ANY,AUDI2,220), BookedSeat(3,5,2,ANY,AUDI2,220), BookedSeat(4,5,3,ANY,AUDI2,220))))
Seat on row: 5, col: 0 already booked

Seats Booked again:
None
```

```declarative
Print Show 11 seatmap
"1111111100, 1111111000, 1111100000, 0000000000, 0000000000, 1111000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000"
```

```declarative
Show My Bookings for user1: 
Some(
  value = ArrayBuffer(
    Booking(
      id = 1,
      user = User(
        id = 1,
        name = "Raj Oberoi",
        email = "raj.oberoi@somemail.com",
        phone = "9988998811",
        userType = CUSTOMER
      ),
      show = Show(
        id = 11,
        movie = Movie(id = 2, title = "Babys Day Out", synopsis = "Comedy", durationInMinutes = 75),
        screen = Screen(
          id = 1002,
          theatre = Theatre(id = 1000, name = "Alpha", city = "Delhi", address = "Sarojni Nagar"),
          name = "AUDI2",
          seatsConfig = List(
            SeatCategory(order = 1, seatType = GOLD, rows = 10, cols = 10, price = 200),
            SeatCategory(order = 2, seatType = SILVER, rows = 10, cols = 10, price = 250),
            SeatCategory(order = 3, seatType = PLATINUM, rows = 10, cols = 10, price = 300)
          ),
          totalSeats = 260,
          totalRows = 26,
          colsPerRow = 10
        ),
        startTime = 2025-03-28T15:39:52.739,
        endDateTime = 2025-03-28T16:54:52.739,
        available = 260,
        reserved = 0,
        seatMap = Array(
          Array(1, 1, 1, 1, 1, 1, 1, 1, 0, 0),
          Array(1, 1, 1, 1, 1, 1, 1, 0, 0, 0),
          Array(1, 1, 1, 1, 1, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(1, 1, 1, 1, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
          Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        )
      ),
      seats = ArrayBuffer(
        BookedSeat(id = 1, row = 5, col = 0, seatType = ANY, screenName = "AUDI2", price = 220),
        BookedSeat(id = 2, row = 5, col = 1, seatType = ANY, screenName = "AUDI2", price = 220),
        BookedSeat(id = 3, row = 5, col = 2, seatType = ANY, screenName = "AUDI2", price = 220),
        BookedSeat(id = 4, row = 5, col = 3, seatType = ANY, screenName = "AUDI2", price = 220)
      )
    )
  )
)
```