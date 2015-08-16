package com.newmotion.core.transactions

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import com.fasterxml.jackson.databind.JsonMappingException
import com.newmotion.util.{DateSupport, JsonSupport}
import com.twitter.finagle.http.Request

object Transaction extends JsonSupport {
  def apply(request: Request): Transaction = {
    try {
      fromJson[Transaction](request.getContentString())
    } catch {
      case e: JsonMappingException => {
        throw e.getCause
      }
    }
  }

  def fromCSV(k: String): Transaction = {
    object Position extends Enumeration {
      val Id        = 0
      val StartTime = 1
      val EndTime   = 2
      val Volume    = 3
    }

    val values = k.split(",")

    Transaction(id = values(Position.Id),
                startTime = values(Position.StartTime),
                endTime = values(Position.EndTime),
                volume = BigDecimal(values(Position.Volume)))
  }
}
case class Transaction(@JsonProperty("customerId") id: String, startTime: String, endTime: String, volume: BigDecimal) {
  require(id.nonEmpty)
  require(startTime.nonEmpty)
  require(endTime.nonEmpty)

  private val ds = new DateSupport()
  private val stDate = ds.parse(startTime)
  private val edDate = ds.parse(endTime)
  private val duration = ds.getDuration(stDate, edDate)

  @JsonIgnore val durationInDecimal = ds.durationToDecimal(duration)
}
