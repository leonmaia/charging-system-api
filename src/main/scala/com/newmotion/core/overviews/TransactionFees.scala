package com.newmotion.core.overviews

import com.newmotion.core.fees.Fee
import com.newmotion.core.tariffs.Tariff
import com.newmotion.core.transactions.Transaction
import com.newmotion.server.RedisStore
import com.twitter.finagle.redis.util.CBToString
import com.twitter.util.Future

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

  //todo same method in Overview apply
  def applyFee(transactions: List[String], tariffs: List[String] = List.empty): List[Transaction] = {
    transactions.map { transactionKey =>
      val listTariffs = tariffs.map(Tariff.fromCSV)
      val transaction = Transaction.fromCSV(transactionKey)
      Fee(transaction, listTariffs) match {
        case Some(fee) => transaction.copy(total = Option(fee.total))
        case _ => transaction
      }
    }
  }
}

