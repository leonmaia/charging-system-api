package unit.com.chargingsystem.core.overviews

import com.chargingsystem.core.overviews.Overview
import com.chargingsystem.core.tariffs.Tariff
import unit.com.chargingsystem.core.BaseSpec

class OverviewSpec extends BaseSpec {

  behavior of "#apply with tariff"

  it should "bring transaction with total formatted as csv" in {
    val transactions = s"pete,${nextYear+1}-10-27T13:32:14Z,${nextYear+1}-10-27T14:32:14Z,13.21" ::
                       s"john,${nextYear+1}-10-28T09:34:17Z,${nextYear+1}-10-28T16:45:13Z,32.03" :: Nil

    val t0 = Tariff(0.20, 1.00, 0.25, s"$nextYear-10-01T00:00:00Z")
    val t1 = Tariff(1.50, 0.50, 0.30, s"$nextYear-10-28T00:00:00Z")
    val tariffList = s"${t0.activeStarting},${t0.startFee},${t0.hourlyFee},${t0.feePerKWh}" ::
                     s"${t1.activeStarting},${t1.startFee},${t1.hourlyFee},${t1.feePerKWh}" :: Nil

    val result = s"pete,${nextYear+1}-10-27T13:32:14Z,${nextYear+1}-10-27T14:32:14Z,13.21,5.96 john,${nextYear+1}-10-28T09:34:17Z,${nextYear+1}-10-28T16:45:13Z,32.03,14.70"
    Overview(transactions, tariffList).asCSV should be(result)
  }

  behavior of "#apply without tariff"

  it should "bring transaction without total formatted as csv" in {
    val transactions = s"john,$nextYear-10-28T09:34:17Z,$nextYear-10-28T16:45:13Z,32.03" :: Nil

    val t1 = Tariff(1.50, 0.50, 0.30, s"$nextYear-11-28T00:00:00Z")
    val tariffList = s"${t1.activeStarting},${t1.startFee},${t1.hourlyFee},${t1.feePerKWh}" :: Nil

    val result = s"john,$nextYear-10-28T09:34:17Z,$nextYear-10-28T16:45:13Z,32.03"
    Overview(transactions, tariffList).asCSV should be(result)
  }
}
