package com.example.store

import com.example.models.*

import scala.collection.concurrent.TrieMap
import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

trait Store

object InMemoryStore extends Store:
  var movies = ArrayBuffer[Movie]()
  var theatres = ArrayBuffer[Theatre]()
  var screens = ArrayBuffer[Screen]()
  var users= ArrayBuffer[User]()
  var shows = TrieMap[Int, mutable.Seq[Show]]() // movieId, List[Show]
  val bookings = TrieMap[Int, ArrayBuffer[Booking]]()
