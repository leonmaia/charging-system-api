package com.chargingsystem.core.fees

import com.chargingsystem.core.tariffs.Tariff
import com.chargingsystem.core.transactions.Transaction
import com.chargingsystem.util.DateSupport
import org.joda.time.DateTime

import scala.math.BigDecimal.RoundingMode._

object Fee {
  val ds = new DateSupport

  def apply(transaction: Transaction, tariffs: List[Tariff]): Option[Fee] = {
    def round(value: BigDecimal): BigDecimal = value.setScale(2, HALF_EVEN)

    val optTariff = bringCorrectTariff(ds.parse(transaction.startTime), tariffs)
    optTariff match {
      case Some(tariff) =>
        val duration = transaction.durationInDecimal
        val hourlyFee = round(duration * tariff.hourlyFee)
        val kWhFee = round(transaction.volume * tariff.feePerKWh)
        val total = round(tariff.startFee + hourlyFee + kWhFee)
        Option(Fee(hourlyFee, kWhFee, total))
      case _ => Option.empty
    }
  }

  def bringCorrectTariff(startTime: DateTime, tariffs: List[Tariff]): Option[Tariff] = {
    val filtered = tariffs.filter( tariff => ds.parse(tariff.activeStarting).isBefore(startTime))
    if (filtered.isEmpty) return Option.empty
    Option(filtered.reduceLeft((tariff1, tariff2) => if(tariff1.activeStarting > tariff2.activeStarting) tariff1 else tariff2))
  }
}

case class Fee(hourlyFee: BigDecimal, kWhFee: BigDecimal, total: BigDecimal)
