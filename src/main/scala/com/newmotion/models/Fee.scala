package com.newmotion.models

import com.fasterxml.jackson.databind.JsonMappingException
import com.newmotion.util.{DateSupport, JsonSupport}
import com.twitter.finagle.http.Request

object Fee extends JsonSupport {
  def apply(request: Request): Fee = {
    try {
      fromJson[Fee](request.getContentString())
    } catch {
      case e: JsonMappingException => {
        throw e.getCause
      }
    }
  }
}
case class Fee(startFee: Double, hourlyFee: Double, feePerKWh: Double, activeStarting: String) {
  private val ds = new DateSupport
  private val activeStartingDT = ds.parse(activeStarting)

  require(activeStartingDT.isAfterNow, "cannot retroactively change the charging tariff")
}

