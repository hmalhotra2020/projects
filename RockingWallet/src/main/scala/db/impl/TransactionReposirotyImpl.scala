package db.impl

import db.TransactionRepository
import domain.*
import scalasql.*
import scalasql.PostgresDialect.*
import scalasql.core.SqlStr.SqlStringSyntax
import scalasql.simple.{*, given}

import java.time.Instant

class ScalaSqlTransactionRepository extends TransactionRepository:

  override def insert(txn: Transaction, db: DbApi): Unit =
    db.updateSql(
      sql"""
        insert into transactions
        (intent_id, txn_type, from_account_id, to_account_id, amount, created_at)
        values (
          ${txn.intentId},
          ${txn.txnType.toString},
          ${txn.fromAccountId},
          ${txn.toAccountId},
          ${txn.amount},
          ${txn.createdAt}
        )
      """
    )

  override def findAll(db: DbApi): List[Transaction] =
    val rows =
      db.runSql[(Long, Long, String, Long, Long, BigDecimal, Instant)](
        sql"""
          select transaction_id, intent_id, txn_type, from_account_id, to_account_id, amount, created_at
          from transactions
          order by transaction_id
        """
      )

    rows.map { case (id, intentId, txnType, from, to, amount, created) =>
      Transaction(id, intentId, TxnType.valueOf(txnType), from, to, amount, created)
    }.toList

  override def findByAccount(accountId: Long, db: DbApi): List[Transaction] =
    val rows =
      db.runSql[(Long, Long, String, Long, Long, BigDecimal, Instant)](
        sql"""
          select transaction_id, intent_id, txn_type, from_account_id, to_account_id, amount, created_at
          from transactions
          where from_account_id = $accountId
             or to_account_id = $accountId
          order by transaction_id
        """
      )

    rows.map { case (id, intentId, txnType, from, to, amount, created) =>
      Transaction(id, intentId, TxnType.valueOf(txnType), from, to, amount, created)
    }.toList
