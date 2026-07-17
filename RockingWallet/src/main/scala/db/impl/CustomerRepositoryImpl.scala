package db.impl

import db.CustomerRepository
import domain.*
import scalasql.*
import scalasql.PostgresDialect.*
import scalasql.core.SqlStr.SqlStringSyntax
import scalasql.simple.{*, given}

import java.time.Instant

class CustomerRepositoryImpl extends CustomerRepository:

  override def create(customer: Customer, db: DbApi): Unit =
    db.updateSql(
      sql"""
        insert into customers
        (customer_id, name, email, phone, status, created_at, updated_at)
        values (
          ${customer.customerId},
          ${customer.name},
          ${customer.email},
          ${customer.phone},
          ${customer.status.toString},
          ${customer.createdAt},
          ${customer.updatedAt}
        )
      """
    )

  override def findById(customerId: Long, db: DbApi): Option[Customer] =
    val rows =
      db.runSql[(Long, String, String, String, String, Instant, Instant)](
        sql"""
          select customer_id, name, email, phone, status, created_at, updated_at
          from customers
          where customer_id = $customerId
        """
      )

    rows.headOption.map { case (id, name, email, phone, statusStr, createdAt, updatedAt) =>
      Customer(
        id,
        name,
        email,
        phone,
        UserStatus.valueOf(statusStr),
        createdAt,
        updatedAt
      )
    }

  override def updateStatus(customerId: Long, status: String, db: DbApi): Unit =
    db.updateSql(
      sql"""
        update customers
        set status = $status,
            updated_at = now()
        where customer_id = $customerId
      """
    )

  override def findAll(db: DbApi): List[Customer] =
    val rows =
      db.runSql[(Long, String, String, String, String, Instant, Instant)](
        sql"""
          select customer_id, name, email, phone, status, created_at, updated_at
          from customers
        """
      )

    rows.map { case (id, name, email, phone, statusStr, createdAt, updatedAt) =>
      Customer(
        id,
        name,
        email,
        phone,
        UserStatus.valueOf(statusStr),
        createdAt,
        updatedAt
      )
    }.toList
