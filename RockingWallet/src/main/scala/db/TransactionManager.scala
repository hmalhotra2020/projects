package db

import scalasql.*

object TransactionManager:

  def inTransaction[T](block: DbApi => T): T =
    val conn = DbConfig.getConnection()

    conn.transaction { db =>
      block(db)
    }
