package ru.evseev.nest.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import ru.evseev.nest.actors.SlackActor._
import ru.evseev.nest.actors.ThermostatActor.ThermostatState

class DeviceActor extends Actor with ActorLogging {

  val actors: scala.collection.mutable.Map[String, ActorRef] = scala.collection.mutable.Map()

  override def receive: Receive = {

    case s: Seq[ThermostatState] => s.foreach(d => act(d) ! d)

    case r: ListThermostats => actors.foreach(_._2 ! r)

    case r: SetThreshold => actors.filter(matchName(r.name)).foreach(_._2 ! r)

    case r: RemoveThreshold => actors.filter(matchName(r.name)).foreach(_._2 ! r)
  }

  private def matchName(requestStr: String)(t: (String, ActorRef)) = t._1.toLowerCase().contains(requestStr.toLowerCase)

  private def act(d: ThermostatState): ActorRef =
    actors.getOrElseUpdate(d.name, {
      d match {
        case d: ThermostatState => context.actorOf(Props(classOf[ThermostatActor], d), d.deviceId)
        case a: Any => log.warning(s"unknown message [$a]"); ActorRef.noSender
      }
    })

}
