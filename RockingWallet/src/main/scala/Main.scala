import db.*
import db.impl.*
import domain.*
import scalasql.PostgresDialect.*
import scalasql.core.SqlStr.SqlStringSyntax
import services.TransferService
import java.nio.file.{Files, Paths}
import java.sql.DriverManager
import java.time.Instant
import scala.io.Source

object Main:

  val accountRepo = new AccountRepositoryImpl
  val intentRepo = new IntentRepositoryImpl
  val ledgerRepo = new LedgerRepositoryImpl
  val customerRepo = new CustomerRepositoryImpl
  val transactionRepo = new ScalaSqlTransactionRepository

  val transferService =
    new TransferService(accountRepo, intentRepo, ledgerRepo, transactionRepo)

  def main(args: Array[String]): Unit = {

    val dbClient = DbConfig.getConnection()
    val db = DbConfig.getPGConnection()

    if args.isEmpty then
      println("Provide command")
    else
      args(0) match
        case "init-db" =>
          val db = DbConfig.getPGConnection()
          runSchema()
          println("Database schema initialized")

        case "credit" =>
          val from = args(1).toLong
          val amount = BigDecimal(args(2))
          TransactionManager.inTransaction { db =>
            transferService.credit(from, Amount(amount), db) match
              case Left(err) => println(s"Transfer failed: $err")
              case Right(_) => println("Transfer successful")
          }

        case "debit" =>
          val from = args(1).toLong
          val amount = BigDecimal(args(2))
          TransactionManager.inTransaction { db =>
            transferService.debit(from, Amount(amount), db) match
              case Left(err) => println(s"Transfer failed: $err")
              case Right(_) => println("Transfer successful")
          }

        case "transfer" =>
          val from = args(1).toLong
          val to = args(2).toLong
          val amount = BigDecimal(args(3))

          TransactionManager.inTransaction { db =>
            transferService.transfer(from, to, Amount(amount), TxnType.Transfer, db) match
              case Left(err) => println(s"Transfer failed: $err")
              case Right(_) => println("Transfer successful")
          }

        case "balance" =>
          val accountId = args(1).toLong
          TransactionManager.inTransaction { db =>
            accountRepo.findById(accountId, db)
              .foreach(acc => println(s"Balance: ${acc.currentBalance}"))
          }

        case "create-account" =>
          val accountId = args(1).toLong
          val customerId = args(2).toLong
          val balance = BigDecimal(args(3))

          val db = DbConfig.getPGConnection()

          db.updateSql(
            sql"""
            insert into accounts
            (account_id, customer_id, state, balance, minimum_balance)
            values ($accountId, $customerId, 'ACTIVE', $balance, 0)
            """
          )

          println("Account created")

        case "ledger" =>
          val db = DbConfig.getPGConnection()
          val entries = ledgerRepo.findAll(db)
          entries.foreach { l =>
            println(
              s"${l.intentId} | ${l.accountId} | ${l.entryType} | ${l.amount} | ${l.createdAt}"
            )
          }

        case "create-customer" =>
          val id = args(1).toLong
          val name = args(2)
          val email = args(3)
          val phone = args(4)

          val customer =
            Customer(id, name, email, phone, UserStatus.ACTIVE, Instant.now(), Instant.now())
          customerRepo.create(customer, db)
          println("Customer created")

        case "customers" =>
          val list = customerRepo.findAll(db)
          list.foreach { c =>
            println(
              s"${c.customerId} | ${c.name} | ${c.email} | ${c.status}"
            )
          }

        case "transactions" =>
          val txns = transactionRepo.findAll(db)
          txns.foreach { t =>
            println(
              s"${t.transactionId} | ${t.txnType} | ${t.fromAccountId} -> ${t.toAccountId} | ${t.amount}"
            )
          }

        case "transactions-account" =>
          val accountId = args(1).toLong
          val txns = transactionRepo.findByAccount(accountId, db)
          txns.foreach { t =>
            println(
              s"${t.transactionId} | ${t.txnType} | ${t.fromAccountId} -> ${t.toAccountId} | ${t.amount}"
            )
          }

        case _ =>
          println("Unknown command")
  }

  def runSchema(): Unit =
    val stream = getClass.getClassLoader.getResourceAsStream("sql/schema.sql")

    if stream == null then
      throw new RuntimeException("schema.sql not found in resources/sql")
  
    val sqlText = Source.fromInputStream(stream).mkString

    val conn = DriverManager.getConnection(
      "jdbc:postgresql://localhost:5432/rajm",
      "postgres",
      "postgres"
    )

    val stmt = conn.createStatement()
    stmt.execute(sqlText)

    stmt.close()
    conn.close()

