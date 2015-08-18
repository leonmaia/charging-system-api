package com.chargingsystem.core.tariffs

import com.fasterxml.jackson.databind.JsonMappingException
import com.chargingsystem.util.{DateSupport, JsonSupport}
import com.twitter.finagle.http.Request

object Tariff extends JsonSupport {
  def apply(request: Request): Tariff = {
    try {
      fromJson[Tariff](request.getContentString())
    } catch {
      case e: JsonMappingException => {
        throw e.getCause
      }
    }
  }

  def fromCSV(k: String): Tariff = {
    object Position extends Enumeration {
      val ActiveStarting = 0
      val StartFee       = 1
      val HourlyFee      = 2
      val FeePerKWh      = 3
    }

    val values = k.split(",")

    Tariff(activeStarting = values(Position.ActiveStarting),
           startFee  = BigDecimal(values(Position.StartFee)),
           hourlyFee = BigDecimal(values(Position.HourlyFee)),
           feePerKWh = BigDecimal(values(Position.FeePerKWh)))
  }
}

case class Tariff(startFee: BigDecimal = 0.00D, hourlyFee: BigDecimal = 0.00D, feePerKWh: BigDecimal = 0.00D, activeStarting: String) {
  private val ds = new DateSupport
  private val activeStartingDT = ds.parse(activeStarting)

  require(startFee >= 0.00D)
  require(hourlyFee >= 0.00D)
  require(feePerKWh >= 0.00D)
  require(activeStarting.nonEmpty)
  require(activeStartingDT.isAfterNow)

  def createCSVKey = s"$activeStarting,$startFee,$hourlyFee,$feePerKWh"
}

