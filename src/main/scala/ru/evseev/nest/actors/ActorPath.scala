package ru.evseev.nest.actors

import akka.actor.Actor

/**
  * Created by anev on 28/02/16.
  */
trait ActorPath {
  actor: Actor =>

  def slackActor = actor.context.actorSelection("/user/main/slack")

  def devicesActor = actor.context.actorSelection("/user/main/devices")
}
