package com.apexsystems.vendingmachine.actor

import akka.actor.{Actor, ActorRef}

import scala.collection.mutable.ListBuffer

case class Selection(input : String)
case class Money(amount : Double)
case class Item(name : String, sku : Int, price : Double)
case class Cancel()
case class ProcessSelection(input : String, slot : ListBuffer[Item], replyTo: ActorRef)
case class Dispense(input : String, slot : ListBuffer[Item], replyTo: ActorRef)
case class Change(replyTo: ActorRef)

class VendingMachine(var currentAmount : Double, slots : Map[String, ListBuffer[Item]]) extends Actor{
  val validAmounts = Array(0.05, 0.10, 0.25, 1, 5)

  override def receive: Receive = {
    case Money(amount) =>
      amount match {
        case amount if validAmounts contains amount =>
          currentAmount += amount
          sender() ! s"Current amount $$$currentAmount"
        case _ => sender() ! "Unsupported money"
      }

    case Selection(input) =>
      input match {
        case _ if !slots.contains(input) => sender() ! "Invalid selection"
        case _ if slots(input).isEmpty => sender() ! "Item out of stock"
        case _ => self ! ProcessSelection(input, slots(input), sender())
      }

    case ProcessSelection(input, slot, replyTo) =>
      val item = slot(0)
      item.price match {
        case _ if currentAmount == 0 => replyTo ! s"Item $input costs $$${item.price}"
        case price if price > currentAmount =>  replyTo ! "Please insert more money"
        case price if price <= currentAmount =>
          currentAmount -= price
          self ! Dispense(input, slot, replyTo)
      }

    case Dispense(input, slot, replyTo) =>
      slot.drop(1)
      replyTo ! s"Dispensed item $input"
      self ! Change(replyTo)

    case Change(replyTo)=>
      if (currentAmount > 0)
        replyTo ! s"Dispensed change: $$$currentAmount"
      currentAmount = 0.0

    case Cancel() =>
      sender() ! "Order canceled"
      self ! Change (sender)

    case _ =>
      // Ignore
  }
}

