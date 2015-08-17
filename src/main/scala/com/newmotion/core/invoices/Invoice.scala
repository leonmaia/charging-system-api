package com.newmotion.core.invoices

import com.newmotion.core.transactions.Transaction
import com.newmotion.util.DateSupport

import scala.annotation.tailrec

object Invoice {
  val dt = new DateSupport
  val fmt = "yyyy-MM-dd HH:mm"

  def apply(transactions: Option[List[Transaction]]): Option[Invoice] = {
    transactions match {
      case Some(t) =>
        val name = transactions.get.head.id
        val total = sum(t.map(_.total.get))

        val month = dt.parse(t.head.startTime).monthOfYear().getAsText
        val year = dt.parse(t.head.startTime).getYear
        val charges = t map { v =>
          val stDate = dt.parseWithCustomFmt(v.startTime, fmt)
          val endDate = dt.parseWithCustomFmt(v.endTime, fmt)
          s"from $stDate to $endDate: ${v.volume} kWh @ â‚¬ ${v.total.getOrElse(0.00D)}"
        }
        Option(Invoice(name, total, month, year, charges.mkString("\n")))
      case _ => Option.empty
    }
  }

  def sum(xs: List[BigDecimal]): BigDecimal = {
    @tailrec
    def inner(xs: List[BigDecimal], accum: BigDecimal): BigDecimal = {
      xs match {
        case x :: tail => inner(tail, accum + x)
        case Nil => accum
      }
    }
    inner(xs, 0)
  }

}

case class Invoice(name: String, total: BigDecimal, month: String, year: Int, charges: String) {
  def template =
    s"""
      |Dear $name,
      |
      |In $month $year, you have charged:
      |$charges
      |
      |Total amount: â‚¬ $total
      |
      |Kind regards, Your dearest mobility provider, The Venerable Inertia""".stripMargin
}

