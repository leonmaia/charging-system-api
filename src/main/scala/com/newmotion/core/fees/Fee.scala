package com.newmotion.core.fees

import com.newmotion.core.tariffs.Tariff
import com.newmotion.core.transactions.Transaction
import com.newmotion.util.DateSupport
import org.joda.time.DateTime

import scala.collection.mutable.ListBuffer
import scala.math.BigDecimal.RoundingMode

object Fee {
  val ds = new DateSupport

  def apply(transaction: Transaction, tariffs: List[Tariff]): Option[Fee] = {
    def round(value: BigDecimal): BigDecimal = value.setScale(2, RoundingMode.HALF_EVEN)

    val tariff = bringCorrectTariff(ds.parse(transaction.startTime), tariffs)
    tariff match {
      case Some(t) =>
        val duration = transaction.durationInDecimal
        val hourlyFee = round(duration * t.hourlyFee)
        val kWhFee = round(transaction.volume * t.feePerKWh)
        val total = round(t.startFee + hourlyFee + kWhFee)
        Option(Fee(hourlyFee, kWhFee, total))
      case _ => Option.empty
    }
  }

  def bringCorrectTariff(startTime: DateTime, tariffs: List[Tariff]): Option[Tariff] = {
    val filtered = tariffs.filter( t => ds.parse(t.activeStarting).isBefore(startTime))
    if (filtered.isEmpty) return Option.empty
    Option(filtered.reduceLeft((tariff1, tariff2) => if(tariff1.activeStarting > tariff2.activeStarting) tariff1 else tariff2))
  }
}

case class Fee(hourlyFee: BigDecimal, kWhFee: BigDecimal, total: BigDecimal)
