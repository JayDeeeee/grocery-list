package com.example

//#Item-registry-actor
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable

//#Item-case-classes
final case class Item(name: String)
final case class Items(items: immutable.Seq[Item])
//#Item-case-classes

object ItemRegistry {
  // actor protocol
  sealed trait Command
  final case class GetItems(replyTo: ActorRef[Items]) extends Command
  final case class CreateItem(Item: Item, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class GetItem(name: String, replyTo: ActorRef[GetItemResponse]) extends Command
  final case class DeleteItem(name: String, replyTo: ActorRef[ActionPerformed]) extends Command

  final case class GetItemResponse(maybeItem: Option[Item])
  final case class ActionPerformed(description: String)

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(items: Set[Item]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetItems(replyTo) =>
        replyTo ! Items(items.toSeq)
        Behaviors.same
      case CreateItem(item, replyTo) =>
        replyTo ! ActionPerformed(s"Item ${item.name} created.")
        registry(items + item)
      case GetItem(name, replyTo) =>
        replyTo ! GetItemResponse(items.find(_.name == name))
        Behaviors.same
      case DeleteItem(name, replyTo) =>
        replyTo ! ActionPerformed(s"Item $name deleted.")
        registry(items.filterNot(_.name == name))
    }
}
//#Item-registry-actor
