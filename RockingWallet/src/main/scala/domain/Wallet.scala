package domain

import scalasql.simple.SimpleTable

import java.time.Instant

case class Customer(customerId: Long, name: String, email: String, phone: String, status: UserStatus, createdAt: Instant, updatedAt: Instant)
case class Transaction(transactionId: Long, intentId: Long, txnType: TxnType, fromAccountId: Long, toAccountId: Long, amount: BigDecimal, createdAt: Instant)
final case class Ledger(ledgerId: Long, intentId: Long, accountId: Long, entryType: LedgerEntryType, amount: BigDecimal, createdAt: Instant)

sealed trait Account
case class WalletAccount(val accountId: Long,
                    val customerId: Long,
                    val state: AccountState,
                    private var balance: BigDecimal,
                    val minimumBalance: BigDecimal = BigDecimal(0)) extends Account {

  def currentBalance: BigDecimal = balance

  def canDebit(amount: Amount): Either[String, Unit] =
    if state != AccountState.ACTIVE then
      Left(s"Account not active: $state")
    else if balance - amount.value < minimumBalance then Left("Insufficient balance")
    else Right(())

  def debit(amount: Amount): Either[String, Unit] = canDebit(amount).map { _ => balance = balance - amount.value }

  def credit(amount: Amount): Either[String, Unit] =
    if state == AccountState.INACTIVE then
      Left("Cannot credit inactive account")
    else
      balance = balance + amount.value
      Right(())
}



final class Intent(val intentId: Long,
                   val fromAccountId: Long,
                   val toAccountId: Long,
                   val amount: Amount,
                   val txnType: TxnType,
                   private var state: IntentState,
                   val createdAt: Instant) {
  def currentState: IntentState = state

  def validate(): Either[String, Unit] =
    if state != IntentState.Created then Left("Intent not in Created state")
    else
      state = IntentState.Validated
      Right(())

  def markPosted(): Either[String, Unit] =
    if state != IntentState.Validated then Left("Intent must be Validated before Posted")
    else
      state = IntentState.Posted
      Right(())

  def complete(): Either[String, Unit] =
    if state != IntentState.Posted then Left("Intent must be Posted before Completed")
    else
      state = IntentState.Completed
      Right(())

  def fail(): Unit =
    state = IntentState.Failed
}

trait LoanAccount extends Account
case class PersonalLoanAccount() extends LoanAccount
case class PostpaidAccount(creditLimit: Int) extends Account

enum TxnType:
  case Credit, Debit, Transfer

enum UserStatus:
  case ACTIVE, BLOCKED, DORMANT

enum IntentState:
  case Created, Validated, Posted, Completed, Failed, Expired

enum LedgerEntryType:
  case Credit, Debit

enum AccountState:
  case ACTIVE, INACTIVE, BLOCKED, DORMANT

final case class Amount(value: BigDecimal):
  require(value > 0, "Amount must be positive")

