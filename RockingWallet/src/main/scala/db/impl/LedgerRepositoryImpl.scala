package db.impl

import db.LedgerRepository
import domain.*
import scalasql.*
import scalasql.PostgresDialect.*
import scalasql.core.SqlStr.SqlStringSyntax
import scalasql.simple.{*, given}

import java.time.Instant

class LedgerRepositoryImpl extends LedgerRepository:

  override def saveAll(entries: List[Ledger], db: DbApi): Unit =
    entries.foreach { entry =>
      db.updateSql(
        sql"""
          insert into ledger_entries
          (intent_id, account_id, entry_type, amount, created_at)
          values (
            ${entry.intentId},
            ${entry.accountId},
            ${entry.entryType.toString},
            ${entry.amount},
            ${entry.createdAt}
          )
        """
      )
    }

  override def findAll(db: DbApi): List[Ledger] =
    val rows =
      db.runSql[(Long, Long, Long, String, BigDecimal, Instant)](
        sql"""
          select ledger_id, intent_id, account_id, entry_type, amount, created_at
          from ledger_entries
          order by ledger_id
        """
      )

    rows.map { case (ledgerId, intentId, accountId, entryType, amount, createdAt) =>
      Ledger(
        ledgerId,
        intentId,
        accountId,
        LedgerEntryType.valueOf(entryType),
        amount,
        createdAt
      )
    }.toList


