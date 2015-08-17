package com.newmotion.core.overviews

import com.newmotion.core.transactions.Transaction

object Overview extends TransactionFees {
  def apply(transactions: List[String], tariffs: List[String] = List.empty): Overview = {
    Overview(applyFee(transactions, tariffs))
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

