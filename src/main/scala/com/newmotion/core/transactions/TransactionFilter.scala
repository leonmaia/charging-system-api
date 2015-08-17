package com.newmotion.core.transactions

import com.newmotion.util.DateSupport

object TransactionFilter {
  val dt = new DateSupport

  def filter(transactions: List[Transaction], filters: List[Filter]): Option[List[Transaction]] = {
    val filtered = transactions.filter ( transaction =>
      transaction.id == filters.find(_.field == "customer_name").get.value &&
      dt.parse(transaction.startTime).getYear == filters.find(_.field == "year").get.value.toInt &&
      dt.parse(transaction.startTime).getMonthOfYear == filters.find(_.field == "month").get.value.toInt
    )

    filtered match {
      case x :: xs => Option(filtered)
      case _ => Option.empty
    }
  }
}

case class Filter(field: String, value: String)

