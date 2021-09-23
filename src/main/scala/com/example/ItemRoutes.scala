package com.example

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import com.example.ItemRegistry._
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

//#import-json-formats
//#item-routes-class
class ItemRoutes(itemRegistry: ActorRef[ItemRegistry.Command])(implicit val system: ActorSystem[_]) {

  //#item-routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getItems(): Future[Items] =
    itemRegistry.ask(GetItems)
  def getItem(name: String): Future[GetItemResponse] =
    itemRegistry.ask(GetItem(name, _))
  def createItem(item: Item): Future[ActionPerformed] =
    itemRegistry.ask(CreateItem(item, _))
  def deleteItem(name: String): Future[ActionPerformed] =
    itemRegistry.ask(DeleteItem(name, _))

  //#all-routes
  //#items-get-post
  //#items-get-delete
  val itemRoutes: Route =
    pathPrefix("items") {
      concat(
        //#items-get-delete
        pathEnd {
          concat(
            get {
              complete(getItems())
            },
            post {
              entity(as[Item]) { item =>
                onSuccess(createItem(item)) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            })
        },
        //#items-get-delete
        //#items-get-post
        path(Segment) { name =>
          concat(
            get {
              //#retrieve-item-info
              rejectEmptyResponse {
                onSuccess(getItem(name)) { response =>
                  complete(response.maybeItem)
                }
              }
              //#retrieve-item-info
            },
            delete {
              //#items-delete-logic
              onSuccess(deleteItem(name)) { performed =>
                complete((StatusCodes.OK, performed))
              }
              //#items-delete-logic
            })
        })
      //#items-get-delete
    }
  //#all-routes
}
