package com.apexsystems.vendingmachine.actor

import akka.actor.Actor
import scala.collection.mutable.ListBuffer

case class Selection(input : String)
case class Money(amount : Double)
case class Item(name : String, sku : Int, price : Double)
case class Cancel()
case class Error(desc : String)
case class Dispense(position : String, item : Item)

class VendingMachine(var currentAmount : Double, slots : Map[String, ListBuffer[Item]]) extends Actor{
  val validAmounts = Array(0.05, 0.10, 0.25, 1, 5)

  override def receive: Receive = {
    case Money(amount) =>
      amount match {
        case amount if validAmounts contains amount => { currentAmount += amount
          sender() ! s"Current amount $$${currentAmount}" }
        case _ => sender() ! "Unsupported money"
      }

    case Selection(input) =>
      input match {
        case input if !slots.contains(input) => sender() !  "Invalid selection"
        case input if slots(input).isEmpty => sender() !  "Item out of stock"
        case _ => Dispense(input, slots(input)(1))
      }

    case Dispense(position, item) =>
      item.price match {
        case price if price > currentAmount =>  sender() ! s"Item ${position} costs $$${item.price}"
        case price if price == currentAmount =>  sender() ! s"Dispensed item ${position}"
        case _ =>  sender() ! s"Dispensed change: ${currentAmount}"
      }

    case Cancel =>
      currentAmount match {
        case currentAmount if currentAmount > 0 => sender() ! "Dispensed change: ${currentAmount}"
        case _ => sender() ! "Order canceled"
      }

    case _ =>
      sender() ! "NA"
  }
}

