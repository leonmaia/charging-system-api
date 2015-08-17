package com.newmotion.core.overviews

import com.newmotion.core.fees.Fee
import com.newmotion.core.tariffs.Tariff
import com.newmotion.core.transactions.Transaction
import com.newmotion.server.RedisStore
import com.twitter.finagle.redis.util.CBToString
import com.twitter.util.Future

trait TransactionFees extends RedisStore {
  def build: Future[List[Transaction]] = {
    getAllMembers("transactions") flatMap {
      transactions =>
        getAllMembers("tariffs") map { tariffs =>
          if (tariffs.isEmpty) applyFee(transactions.map(CBToString(_)))
          else applyFee(transactions.map(CBToString(_)), tariffs.map(CBToString(_)))
        }
    }
  }

  def applyFee(transactions: List[String], tariffs: List[String] = List.empty): List[Transaction] = {
    transactions.map { t =>
      val listTariffs = tariffs.map( v => Tariff.fromCSV(v))
      val transaction = Transaction.fromCSV(t)
      Fee(transaction, listTariffs) match {
        case Some(f) => transaction.copy(total = Option(f.total))
        case _ => transaction
      }
    }
  }
}

