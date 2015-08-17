package com.newmotion.core.invoices

import com.newmotion.core.overviews.Overview

case class Invoice(overview: Overview) {
  private val name = overview.transactions.head.id
  private val total = Option(10.00D)


  val template =
    s"""
      |Dear $name,
      |
      |In October 2014, you have charged:
      |from 2014-10-15 09:00 to 2014-10-15 17:16: 4.17 kWh @ â‚¬ 3.21
      |from 2014-10-27 13:32 to 2014-10-27 14:32: 13.21 kWh @ â‚¬ 4.50
      |from 2014-10-30 14:23 to 2014-10-30 15:17: 1.5 kWh @ â‚¬ 1.20
      |
      |Total amount: â‚¬ ${total.getOrElse(0.00D)}
      |
      |Kind regards, Your dearest mobility provider, The Venerable Inertia""".stripMargin
}

