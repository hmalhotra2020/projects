package db

import scalasql.*
import scalasql.PostgresDialect.*
import scalasql.core.SqlStr.SqlStringSyntax
import scalasql.simple.{*, given}

import java.sql.DriverManager

object DbConfig:

  private val jdbcUrl = "jdbc:postgresql://localhost:5432/rajm"
  private val user = "postgres"
  private val password = "postgres"

  def getConnection(): DbClient.Connection = {
    Class.forName("org.postgresql.Driver")
    val conn = DbClient.Connection(java.sql.DriverManager.getConnection(jdbcUrl, user, password), new Config {override def nameMapper(v: String) = v.toLowerCase()})
    conn
  }

  def getPGConnection(): DbApi = {
    //Class.forName("org.postgresql.Driver")
    val dbClient = getConnection()
    dbClient.getAutoCommitClientConnection
  }
