package ru.evseev.nest.actors

import akka.actor.{Actor, ActorLogging}
import ru.evseev.nest.actors.SlackActor._

class ThermostatActor(var termostatState: ThermostatActor.ThermostatState) extends Actor with ActorLogging with ActorPath {

  var threashold: Option[Int] = None

  override def receive: Receive = {

    case newVal: ThermostatActor.ThermostatState => {
      sendNewData(newVal)
      termostatState = newVal
    }

    case r: ListThermostats => slackActor ! SlackActor.ThermostatValue(termostatState, threashold)

    case r: SetThreshold => {
      this.threashold = Some(r.threshold)
      slackActor ! SlackActor.ThresholdOn(termostatState.name, r.threshold)
    }

    case r: RemoveThreshold => {
      this.threashold = None
      slackActor ! SlackActor.ThresholdOff(termostatState.name)
    }

    case a: Any => log.info(a.toString)
  }

  def sendNewData(newVal: ThermostatActor.ThermostatState) = threashold match {

    case Some(th) => {
      if (ThermostatActor.thresholdCrossed(th, termostatState.temp, newVal.temp)) {
        slackActor ! SlackActor.ThreasholdCrossed(termostatState.name, newVal.temp, th)
      }
    }

    // no threshold => do nothing
    case None =>
  }
}

object ThermostatActor {

  case class ThermostatState(val deviceId: String, val name: String, val temp: Int)

  case class ThresholdCrossed(val state: ThermostatState, val threshold: Int)

  def thresholdCrossed(threshold: Int, oldVal: Int, newVal: Int) =
    (oldVal <= threshold && newVal > threshold) || (oldVal >= threshold && newVal < threshold)

}