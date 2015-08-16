package com.newmotion.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonMappingException
import com.newmotion.util.JsonSupport
import com.twitter.finagle.http.Request
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTimeZone, DateTimeConstants, Duration, Interval}

import scala.concurrent.duration
import scala.math.BigDecimal.RoundingMode

object Transaction extends JsonSupport {
  def apply(request: Request): Transaction= {
    try {
      fromJson[Transaction](request.getContentString())
    } catch {
      case e: JsonMappingException => {
        throw e.getCause
      }
    }
  }
}
case class Transaction(@JsonProperty("customerId") id: String, startTime: String, endTime: String, volume: Double) {
  private val stDate = ISODateTimeFormat.dateTimeNoMillis().parseDateTime(startTime).withZone(DateTimeZone.UTC)
  private val edDate = ISODateTimeFormat.dateTimeNoMillis().parseDateTime(endTime).withZone(DateTimeZone.UTC)
  private val duration =  new Duration(stDate, edDate)

  val durationInDecimal = BigDecimal.valueOf(duration.getMillis)./(BigDecimal.valueOf(DateTimeConstants.MILLIS_PER_HOUR))
    .setScale(2, RoundingMode.HALF_DOWN)

}
