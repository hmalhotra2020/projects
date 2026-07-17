package db

import domain.*
import scalasql.{DbApi, DbClient}

trait AccountRepository:
  def findById(accountId: Long, db: DbApi): Option[WalletAccount]
  def save(account: WalletAccount, db: DbApi): Unit

trait IntentRepository:
  def insert(intent: Intent, db: DbApi): Unit
  def findById(intentId: Long, db: DbApi): Option[Intent]
  def save(intent: Intent, db: DbApi): Unit

trait LedgerRepository:
  def saveAll(entries: List[Ledger], db: DbApi): Unit
  def findAll(db: DbApi): List[Ledger]

trait CustomerRepository:
  def create(customer: Customer, db: DbApi): Unit
  def findById(customerId: Long, db: DbApi): Option[Customer]
  def updateStatus(customerId: Long, status: String, db: DbApi): Unit
  def findAll(db: DbApi): List[Customer]

trait TransactionRepository:
  def insert(transaction: Transaction, db: DbApi): Unit
  def findAll(db: DbApi): List[Transaction]
  def findByAccount(accountId: Long, db: DbApi): List[Transaction]
