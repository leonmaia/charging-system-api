package com.chargingsystem.util

import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeConstants, DateTimeZone, Duration}

import scala.math.BigDecimal.RoundingMode

class DateSupport {

  def parse(value: String) = {
    ISODateTimeFormat.dateTimeNoMillis().parseDateTime(value).withZone(DateTimeZone.UTC)
  }

  def getDuration(date1: DateTime, date2: DateTime) = new Duration(date1, date2)

  def durationToDecimal(duration: Duration) = BigDecimal.valueOf(duration.getMillis).
    /(BigDecimal.valueOf(DateTimeConstants.MILLIS_PER_HOUR))
    .setScale(2, RoundingMode.HALF_DOWN)

  def parseWithCustomFmt(value: String, fmt: String) = parse(value).toString(fmt)
}
