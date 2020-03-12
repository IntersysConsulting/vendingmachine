package com.apexsystems.vendingmachine.actor

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import scala.util.Random.nextInt

class VendingMachineTest()
  extends TestKit(ActorSystem("VendingMachineTest", ConfigFactory.parseString(VendingMachineTest.config)))
    with ImplicitSender
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  val MAX_SLOT_SIZE = 15
  //Predefined list of Items
  val items = List(Item("Snickers", 123123, 2.5), Item("Twizzlers", 459837, 3.0), Item("Twix", 345345, 3.5), Item("Butterfingers", 834578, 4.5), Item("Milky Way", 78656567, 5))
  val itemsSize = items.size

  //Method to generate a random list of Items
  def getItems(): ListBuffer[Item] = ListBuffer[Item]().addAll(LazyList.continually(items(nextInt(itemsSize))).take(nextInt(15)))

  //Method to get Slots with Random content
  def getSlots(): Map[String, ListBuffer[Item]] = Map("A1" -> getItems(),"A2" -> getItems(),"A3" -> getItems(),"A4" -> new ListBuffer[Item],"B1" -> getItems(), "B2" -> ListBuffer(items(1)))

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A Vendor Machine actor" must {
    "Ignore invalid inputs" in {
      val vendingMachineActor = system.actorOf(Props(new VendingMachine(0.0, getSlots())))
      vendingMachineActor ! "hello world"
    }
  }

  "A Vendor Machine actor" must {
    "Select invalid slot" in {
      val vendingMachineActor = system.actorOf(Props(new VendingMachine(0.0, getSlots())))
      vendingMachineActor ! Selection("B3")
      expectMsg("Invalid selection")
    }
  }

  "A Vendor Machine actor" must {
    "Select valid item with no money deposited" in {
      val vendingMachineActor = system.actorOf(Props(new VendingMachine(0.0, getSlots())))
      vendingMachineActor ! Selection("B2")
      //receiveWhile(500 millis) {
      //  case msg: String => println(msg)
      //}
      expectMsg(s"Item B2 costs $$${items(1).price}")
    }
  }

  "A Vendor Machine actor" must {
    "Select valid item with not enough money deposited" in {
      val vendingMachineActor = system.actorOf(Props(new VendingMachine(1.0, getSlots())))
      vendingMachineActor ! Selection("B2")
      expectMsg("Please insert more money")
    }
  }

  "A Vendor Machine actor" must {
    "Select empty slot" in {
      val vendingMachineActor = system.actorOf(Props(new VendingMachine(1.0, getSlots())))
      vendingMachineActor ! Selection("A4")
      expectMsg("Item out of stock")
    }
  }

  "A Vendor Machine actor" must {
    "Deposit valid amount of money " in {
      val vendingMachineActor = system.actorOf(Props(new VendingMachine(0.0, getSlots())))
      vendingMachineActor ! Money(1.0)
      vendingMachineActor ! Money(1.0)
      expectMsg("Current amount $1.0")
      expectMsg("Current amount $2.0")
    }
  }

  "A Vendor Machine actor" must {
    "Deposit invalid amount of money " in {
      val vendingMachineActor = system.actorOf(Props(new VendingMachine(0.0, getSlots())))
      vendingMachineActor ! Money(10.0)
      expectMsg("Unsupported money")
    }
  }

  "A Vendor Machine actor" must {
    "Deposit valid amount of money and cancel " in {
      val vendingMachineActor = system.actorOf(Props(new VendingMachine(0.0, getSlots())))
      vendingMachineActor ! Money(1.0)
      vendingMachineActor ! Cancel()
      expectMsg("Current amount $1.0")
      expectMsg("Order canceled")
      expectMsg("Dispensed change: $1.0")
    }
  }

  "A Vendor Machine actor" must {
    "Deposit valid amount of money and select Item with no change" in {
      val vendingMachineActor = system.actorOf(Props(new VendingMachine(0.0, getSlots())))
      vendingMachineActor ! Money(1.0)
      vendingMachineActor ! Money(1.0)
      vendingMachineActor ! Money(1.0)
      vendingMachineActor ! Selection("B2")
      expectMsg("Current amount $1.0")
      expectMsg("Current amount $2.0")
      expectMsg("Current amount $3.0")
      expectMsg("Dispensed item B2")
      //expectMsg("Dispensed change: $1.0")
    }
  }

  "A Vendor Machine actor" must {
    "Deposit valid amount of money and select Item with change" in {
      val vendingMachineActor = system.actorOf(Props(new VendingMachine(0.0, getSlots())))
      vendingMachineActor ! Money(5.0)
      vendingMachineActor ! Selection("B2")
      expectMsg("Current amount $5.0")
      expectMsg("Dispensed item B2")
      expectMsg("Dispensed change: $2.0")
    }
  }
}

object VendingMachineTest {
  val config = """
    akka {
      loglevel = "WARNING"
    }
    """
}