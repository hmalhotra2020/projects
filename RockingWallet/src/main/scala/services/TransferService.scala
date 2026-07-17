package services

import db.*
import domain.*
import scalasql.DbApi

import java.time.Instant

class TransferService(
                       accountRepo: AccountRepository,
                       intentRepo: IntentRepository,
                       ledgerRepo: LedgerRepository,
                       transactionRepo: TransactionRepository
                     ):

  val SYSTEM_ACCOUNT_ID = 1

  def transfer(fromAccountId: Long, toAccountId: Long, amount: Amount, txnType: TxnType, db: DbApi
              ): Either[String, Unit] =

    val intentId = java.util.UUID.randomUUID().getMostSignificantBits & Long.MaxValue

    val intent = Intent(intentId, fromAccountId, toAccountId, amount, TxnType.Transfer, IntentState.Created, java.time.Instant.now())
    intentRepo.insert(intent, db)

    TransactionManager.inTransaction { db =>
      for
        _ <- intent.validate()
        sender <- accountRepo.findById(fromAccountId, db).toRight("Sender account not found")
        receiver <- accountRepo.findById(toAccountId, db).toRight("Receiver account not found")
        _ <- sender.debit(amount)
        _ <- receiver.credit(amount)
      yield
        accountRepo.save(sender, db)
        accountRepo.save(receiver, db)
        ledgerRepo.saveAll(
          List(
            Ledger(0, intentId, fromAccountId, LedgerEntryType.Debit, amount.value, java.time.Instant.now()),
            Ledger(0, intentId, toAccountId, LedgerEntryType.Credit, amount.value, java.time.Instant.now())
          ),
          db
        )
        transactionRepo.insert(Transaction(0, intentId, txnType, fromAccountId, toAccountId, amount.value, Instant.now()), db)
        intent.complete()
        intentRepo.save(intent, db)
    }

  def credit(accountId: Long, amount: Amount, db: DbApi) =
    transfer(SYSTEM_ACCOUNT_ID, accountId, amount, TxnType.Credit, db)

  def debit(accountId: Long, amount: Amount, db: DbApi) =
    transfer(SYSTEM_ACCOUNT_ID, accountId, amount, TxnType.Debit, db)

