package com.newmotion.core.overviews

import com.newmotion.core.fees.Fee
import com.newmotion.core.tariffs.Tariff
import com.newmotion.core.transactions.Transaction

object Overview {
  def apply(transactions: List[String], tariffs: List[String] = List.empty): Overview = {
    val transactionsWithTotal = transactions.map { t =>
      val listTariffs = tariffs.map( v => Tariff.fromCSV(v))
      val transaction = Transaction.fromCSV(t)
      Fee(transaction, listTariffs) match {
        case Some(f) => s"$t,${f.total}"
        case _ => t
      }
    }
    Overview(transactionsWithTotal.mkString(" "))
  }
}

case class Overview(value: String)

