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

  def fromCSV(key: String): Transaction = {
    object Position extends Enumeration {
      val Id        = 0
      val StartTime = 1
      val EndTime   = 2
      val Volume    = 3
    }

    val values = key.split(",")

    Transaction(id = values(Position.Id),
                startTime = values(Position.StartTime),
                endTime = values(Position.EndTime),
                volume = BigDecimal(values(Position.Volume)))
  }
}
case class Transaction(@JsonProperty("customerId") id: String, startTime: String, endTime: String, volume: BigDecimal, total: Option[BigDecimal] = Option.empty) {
  require(id.nonEmpty)
  require(startTime.nonEmpty)
  require(endTime.nonEmpty)

  private val dateSupport = new DateSupport()
  private val startDate = dateSupport.parse(startTime)
  private val endDate = dateSupport.parse(endTime)
  private val duration = dateSupport.getDuration(startDate, endDate)

  @JsonIgnore val durationInDecimal = dateSupport.durationToDecimal(duration)

  def createCSVKey = s"$id,$startTime,$endTime,$volume"
}
