package com.apexsystems.vendingmachine.actor

import akka.actor.Actor

import scala.collection.mutable.ListBuffer


case class Selection(val input : String)
case class Money(amount : Double)
case class Item(val name : String, val sku : Int, val price : Double)
case class Cancel()
case class Error(desc : String)
case class Dispense(position : String, slot : ListBuffer[Item])



class VendingMachine(var currentAmount : Double, slots : Map[String, ListBuffer[Item]]) extends Actor{
  val validAmounts = Array(0.05, 0.10, 0.25, 1, 5)

  override def receive: Receive = {
    case Money(amount) =>
      if(validAmounts contains amount){
        currentAmount+=amount
      } else {
        sender() ! Error("Invalid amount")
      }

    case Selection(input) =>
      val slot = slots(input)


      if(slot.isEmpty){
        sender() ! Error("Invalid amount")
      } else {
        self ! Dispense(input, slot)
      }

    case Dispense(position, slot) =>
      val item = slot(1)

      if (currentAmount < item.price){
        sender() ! Error("Item A7 costs $1.25")
      } else {
        sender() ! s"Dispensed item ${position}"
      }


    case Cancel =>
      if (currentAmount > 0){
        sender ! s"Dispensed change: ${currentAmount}"
        currentAmount
      } else {
        sender !  "Order canceled"
      }
    case _ => print("Error")
  }
}

