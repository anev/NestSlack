package ru.evseev.nest.actors

import akka.actor.{Actor, ActorLogging, Props}

/**
  * Created by anev on 24/02/16.
  */
class MainActor(val nestUrl: String,
                val nestToken: String,
                val slackToken: String,
                val slackChannelName: String) extends Actor with ActorLogging {

  val nestActor = context.actorOf(Props(classOf[NestActor], nestUrl, nestToken), "nest")
  val devicesActor = context.actorOf(Props(classOf[DeviceActor]), "devices")
  val slackActor = context.actorOf(Props(classOf[SlackActor], slackToken, slackChannelName), "slack")

  nestActor ! NestActor.CONNECT
  slackActor ! SlackActor.Help()
  slackActor ! SlackActor.ListThermostats()

  override def receive: Receive = {

    case a: Any => log.warning("unknown message {}", a.toString)

  }
}


