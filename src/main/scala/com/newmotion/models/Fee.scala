package com.newmotion.models

import com.fasterxml.jackson.databind.JsonMappingException
import com.newmotion.util.JsonSupport
import com.twitter.finagle.http.Request
import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat

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
  private val activeStartingDT = ISODateTimeFormat.dateTimeNoMillis().parseDateTime(activeStarting).withZone(DateTimeZone.UTC)

  require(activeStartingDT.isAfterNow, "cannot retroactively change the charging tariff")
}

