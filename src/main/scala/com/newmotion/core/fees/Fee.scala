package com.newmotion.core.fees

import com.newmotion.core.tariffs.Tariff
import com.newmotion.core.transactions.Transaction

import scala.math.BigDecimal.RoundingMode

object Fee {
  def apply(transaction: Transaction, tariff: Tariff): Fee = {
    def round(value: BigDecimal): BigDecimal = value.setScale(2, RoundingMode.HALF_EVEN)

    val duration = transaction.durationInDecimal
    val hourlyFee = round(duration * tariff.hourlyFee)
    val kWhFee = round(transaction.volume * tariff.feePerKWh)
    val total = round(tariff.startFee + hourlyFee + kWhFee)

    Fee(hourlyFee, kWhFee, total)
  }
}

case class Fee(hourlyFee: BigDecimal, kWhFee: BigDecimal, total: BigDecimal)

