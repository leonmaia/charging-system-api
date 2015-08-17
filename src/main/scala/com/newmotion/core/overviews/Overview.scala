package com.newmotion.core.overviews

import com.newmotion.core.fees.Fee
import com.newmotion.core.tariffs.Tariff
import com.newmotion.core.transactions.Transaction

import scala.math.BigDecimal.RoundingMode

object Overview {
  def apply(transactions: List[String], tariffs: List[String] = List.empty): Overview = {
    val list = transactions.map { t =>
      val listTariffs = tariffs.map( v => Tariff.fromCSV(v))
      val transaction = Transaction.fromCSV(t)
      Fee(transaction, listTariffs) match {
        case Some(f) =>
          transaction.copy(total = Option(f.total.setScale(2, RoundingMode.HALF_EVEN)))
        case _ => transaction
      }
    }
    Overview(list)
  }
}

case class Overview(transactions: List[Transaction]) {
  def asCSV = transactions.map { t =>
    t.total match {
      case Some(total) => s"${t.createCSVKey},$total"
      case _ => t.createCSVKey
    }
  }.mkString(" ")
}

