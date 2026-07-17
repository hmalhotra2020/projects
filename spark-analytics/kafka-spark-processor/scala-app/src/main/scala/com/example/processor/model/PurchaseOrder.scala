package com.example.processor.model

/**
 * Petrol pump purchase order model.
 * Wire format: petrolPumpId|machineId|city|purchaseTime|fuelType|qnty|amt|pType
 */
case class PurchaseOrder(
  petrolPumpId: String,
  machineId: String,
  city: String,
  purchaseTime: String,
  fuelType: Int,
  qnty: Int,
  amt: Int,
  pType: Int
)

object PurchaseOrder {
  def fromPipeString(line: String): PurchaseOrder = {
    val f = line.split("\\|")
    require(f.length >= 8, s"Invalid order format: $line")
    PurchaseOrder(
      petrolPumpId = f(0), machineId = f(1), city = f(2), purchaseTime = f(3),
      fuelType = f(4).trim.toInt, qnty = f(5).trim.toInt,
      amt = f(6).trim.toInt, pType = f(7).trim.toInt
    )
  }
}
