package com.newmotion.core.fees

import com.newmotion.core.tariffs.Tariff
import com.newmotion.core.transactions.Transaction
import com.newmotion.util.DateSupport
import org.joda.time.DateTime

import scala.collection.mutable.ListBuffer
import scala.math.BigDecimal.RoundingMode

object Fee {
  val ds = new DateSupport

  def apply(transaction: Transaction, tariff: Tariff): Fee = {
    def round(value: BigDecimal): BigDecimal = value.setScale(2, RoundingMode.HALF_EVEN)

    val duration = transaction.durationInDecimal
    val hourlyFee = round(duration * tariff.hourlyFee)
    val kWhFee = round(transaction.volume * tariff.feePerKWh)
    val total = round(tariff.startFee + hourlyFee + kWhFee)

    Fee(hourlyFee, kWhFee, total)
  }

  def bringCorrectTariff(startTime: DateTime, tariffs: List[Tariff]): Tariff = {
    val filtered = tariffs.filter( t => ds.parse(t.activeStarting).isBefore(startTime))
    filtered.reduceLeft((tariff1, tariff2) => if(tariff1.activeStarting > tariff2.activeStarting) tariff1 else tariff2)
  }
}

case class Fee(hourlyFee: BigDecimal, kWhFee: BigDecimal, total: BigDecimal)
