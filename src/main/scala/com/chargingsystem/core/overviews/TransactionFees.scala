package com.chargingsystem.core.overviews

import com.chargingsystem.core.fees.Fee
import com.chargingsystem.core.tariffs.Tariff
import com.chargingsystem.core.transactions.Transaction
import com.chargingsystem.server.RedisStore
import com.twitter.finagle.redis.util.CBToString
import com.twitter.util.Future

import scala.math.BigDecimal.RoundingMode._

trait TransactionFees extends RedisStore {
  def requestTransactions: Future[List[Transaction]] = {
    getAllMembers("transactions") flatMap {
      transactions =>
        getAllMembers("tariffs") map { tariffs =>
          if (tariffs.isEmpty) applyFee(transactions.map(CBToString(_)))
          else applyFee(transactions.map(CBToString(_)), tariffs.map(CBToString(_)))
        }
    }
  }

  def applyFee(transactions: List[String], tariffs: List[String] = List.empty): List[Transaction] = {
    transactions.map { transactionKey =>
      val listTariffs = tariffs.map(Tariff.fromCSV)
      val transaction = Transaction.fromCSV(transactionKey)
      Fee(transaction, listTariffs) match {
        case Some(fee) => transaction.copy(total = Option(fee.total.setScale(2, HALF_EVEN)))

        case _ => transaction
      }
    }
  }
}

