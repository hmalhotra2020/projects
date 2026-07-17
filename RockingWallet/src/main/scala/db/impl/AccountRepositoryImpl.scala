package db.impl

import db.AccountRepository
import domain.*
import dto.AccountRow
import scalasql.*
import scalasql.PostgresDialect.*
import scalasql.core.SqlStr.SqlStringSyntax
import scalasql.simple.{*, given}

class AccountRepositoryImpl extends AccountRepository:

  def findById(accountId: Long, db: DbApi): Option[WalletAccount] =
    val result = db.runSql[AccountRow](
      sql"""
            select account_id, customer_id, state, balance, minimum_balance
            from accounts
            where account_id = $accountId
          """
    )
    result.headOption.map(AccountRow.toDomain)

  override def save(account: WalletAccount, db: DbApi): Unit =
    db.updateSql(
      sql"""
        update accounts
        set balance = ${account.currentBalance}
        where account_id = ${account.accountId}
      """
    )
