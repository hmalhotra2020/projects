package db.impl

import db.IntentRepository
import domain.*
import scalasql.*
import scalasql.PostgresDialect.*
import scalasql.core.SqlStr.SqlStringSyntax
import scalasql.simple.{*, given}

import java.time.Instant

class IntentRepositoryImpl extends IntentRepository:

  override def insert(intent: Intent, db: DbApi): Unit =
    db.updateSql(
      sql"""
        insert into intents
        (intent_id, from_account_id, to_account_id, amount, txn_type, state, created_at)
        values (
          ${intent.intentId},
          ${intent.fromAccountId},
          ${intent.toAccountId},
          ${intent.amount.value},
          ${intent.txnType.toString},
          ${intent.currentState.toString},
          ${intent.createdAt}
        )
      """
    )

  override def findById(intentId: Long, db: DbApi): Option[Intent] =
    val rows =
      db.runSql[(Long, Long, Long, BigDecimal, String, String, Instant)](
        sql"""
          select intent_id, from_account_id, to_account_id, amount, txn_type, state, created_at
          from intents
          where intent_id = $intentId
        """
      )

    rows.headOption.map { case (id, fromId, toId, amount, txnTypeStr, stateStr, createdAt) =>
      new Intent(
        id,
        fromId,
        toId,
        Amount(amount),
        TxnType.valueOf(txnTypeStr),
        IntentState.valueOf(stateStr),
        createdAt
      )
    }

  override def save(intent: Intent, db: DbApi): Unit =
    db.updateSql(
      sql"""
        update intents
        set state = ${intent.currentState.toString}
        where intent_id = ${intent.intentId}
      """
    )
