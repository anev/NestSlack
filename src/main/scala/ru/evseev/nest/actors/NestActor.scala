package ru.evseev.nest.actors

import akka.actor.{Actor, ActorLogging}
import com.firebase.client.Firebase.AuthListener
import com.firebase.client.{DataSnapshot, Firebase, FirebaseError, ValueEventListener}
import ru.evseev.nest.NestParser

import scala.collection.JavaConversions._


class NestActor(val url: String, val token: String) extends Actor with ActorLogging with ActorPath {

  override def receive = {

    case NestActor.CONNECT => {
      try {
        connect()
      } catch {
        case e: Exception => log.error(e.toString, e)
      }
    }

    case a: Any => log.error("unknown message: {}", a)
  }


  private def connect(): Unit = {

    val fb: Firebase = new Firebase(url)

    fb.auth(token, new AuthListener {
      def onAuthError(e: FirebaseError) {
        log.error("fb auth error: {}", e)
      }

      def onAuthSuccess(a: AnyRef) {
        log.info("fb auth success: {}", a)
        fb.addValueEventListener(new ValueEventListener {
          def onDataChange(snapshot: DataSnapshot) {

            val devicesMap = snapshot.getValue().asInstanceOf[java.util.Map[String, Object]].toMap

            val seq = NestParser.parseTermostat(
              devicesMap.get("thermostats").get.asInstanceOf[java.util.Map[String, Object]].toMap
            )

            log.debug(s"got update, ${seq.size} thermostats")
            devicesActor ! seq
          }

          def onCancelled(err: FirebaseError) {
            log.warning(err.toString)
          }
        })
      }

      def onAuthRevoked(e: FirebaseError) {
        log.warning("fb auth revoked: {}", e)
      }
    })
  }

}

object NestActor {

  case object CONNECT

}