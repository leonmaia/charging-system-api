package com.newmotion.core.tariffs

import com.fasterxml.jackson.databind.JsonMappingException
import com.newmotion.util.{DateSupport, JsonSupport}
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
}

case class Tariff(startFee: BigDecimal, hourlyFee: BigDecimal, feePerKWh: BigDecimal, activeStarting: String) {
  private val ds = new DateSupport
  private val activeStartingDT = ds.parse(activeStarting)

  require(activeStartingDT.isAfterNow)
}

