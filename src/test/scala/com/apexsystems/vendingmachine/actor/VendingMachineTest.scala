package com.apexsystems.vendingmachine.actor

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.collection.mutable.ListBuffer
import scala.util.Random.nextInt

class VendingMachineTest()
  extends TestKit(ActorSystem("com.apexsystems.vendingmachine.actor.VendingMachineTest", ConfigFactory.parseString(VendingMachineTest.config)))
    with ImplicitSender
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  val MAX_SLOT_SIZE = 15
  //Predefinied list of Items
  val items = List(Item("Snickers", 123123, 2.5), Item("Twizzlers", 459837, 3.0), Item("Twix", 345345, 3.5), Item("Butterfingers", 834578, 4.5), Item("Milky Way", 78656567, 5))
  val itemsSize = items.size

  //Method to generate a random list of Items
  def getItems(): ListBuffer[Item] = ListBuffer[Item]().addAll(LazyList.continually(items(nextInt(itemsSize))).take(nextInt(15)))

  //Method to get Slots with Random content
  def getSlots(): Map[String, ListBuffer[Item]] = Map("A1" -> getItems(),"A2" -> getItems(),"A3" -> getItems(),"A4" -> getItems(),"B1" -> getItems())

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A Vendor Machine actor" must {
    "send random value" in {
      val echo = system.actorOf(Props(new VendingMachine(0.0, getSlots())))
      echo ! "hello world"
      expectMsg("NA")
    }
  }

  "A Vendor Machine actor" must {
    "Select empty slot" in {
      val echo = system.actorOf(Props(new VendingMachine(0.0, getSlots())))
      echo ! Selection("B3")
      expectMsg("Invalid selection")
    }
  }

  "A Vendor Machine actor" must {
    "Select valid item with no money deposited" in {
      val echo = system.actorOf(Props(new VendingMachine(0.0, getSlots())))
      echo ! Selection("A1")
      expectMsg("Invalid selection")
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