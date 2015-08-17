package com.newmotion.core.overviews

import com.newmotion.core.fees.Fee
import com.newmotion.core.tariffs.Tariff
import com.newmotion.core.transactions.Transaction

import scala.math.BigDecimal.RoundingMode._

object Overview {
  def apply(transactions: List[String], tariffs: List[String] = List.empty): Overview = {
    val list = transactions.map { transactionKey =>
      val listTariffs = tariffs.map(Tariff.fromCSV)
      val transaction = Transaction.fromCSV(transactionKey)
      Fee(transaction, listTariffs) match {
        case Some(fee) =>
          transaction.copy(total = Option(fee.total.setScale(2, HALF_EVEN)))
        case _ => transaction
      }
    }
    Overview(list)
  }
}

case class Overview(transactions: List[Transaction]) {
  def asCSV = transactions.map { transaction =>
    transaction.total match {
      case Some(total) => s"${transaction.createCSVKey},$total"
      case _ => transaction.createCSVKey
    }
  }.mkString(" ")
}

