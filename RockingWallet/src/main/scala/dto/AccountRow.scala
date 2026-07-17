package dto

import domain.{AccountState, WalletAccount}
import scalasql.*
import scalasql.namedtuples.SimpleTable

case class AccountRow(
                     accountId: Long,
                     customerId: Long,
                     state: String,
                     balance: BigDecimal,
                     minimumBalance: BigDecimal
                   )

object AccountRow extends SimpleTable[AccountRow]:
  override def tableColumnNameOverride(s: String): String =
    s match
      case "accountId" => "account_id"
      case "customerId" => "customer_id"
      case "state" => "state"
      case "balance" => "balance"
      case "minimumBalance" => "minimum_balance"

  def toDomain(a: AccountRow): WalletAccount =
    new WalletAccount(
      a.accountId,
      a.customerId,
      AccountState.valueOf(a.state),
      a.balance,
      a.minimumBalance
    )

  def toRow(a: WalletAccount): AccountRow =
    AccountRow(
      a.accountId,
      a.customerId,
      a.state.toString,
      a.currentBalance,
      a.minimumBalance
    )