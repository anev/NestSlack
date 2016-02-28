package ru.evseev.nest.actors

import akka.actor.{Actor, ActorLogging}
import com.fasterxml.jackson.databind.JsonNode
import flowctrl.integration.slack.SlackClientFactory
import flowctrl.integration.slack.rtm.{Event, EventListener, SlackRealTimeMessagingClient}
import flowctrl.integration.slack.webapi.SlackWebApiClient
import ru.evseev.nest.UserRequestParser
import ru.evseev.nest.actors.SlackActor._

class SlackActor(val token: String, val channelName: String) extends Actor with ActorLogging with ActorPath {

  val HelpText =
    """
      Hi, I'm nest bot :) you can use those commands
       * `nest help`
       * `nest list` - list all thermostats
       * `nest threshold <NAME> <VALUE>` - set threshold on <NAME> to <VALUE>
       * `nest threshold off <NAME>` - switch off threshold
    """

  val rtmClient: SlackRealTimeMessagingClient = SlackClientFactory.createSlackRealTimeMessagingClient(token)

  val eventListener = new EventListener {

    override def handleMessage(jsonNode: JsonNode): Unit = {

      Some(jsonNode)
        .filter(isMessage)
        .map(_.get("text").asText())
        .flatMap(UserRequestParser.parseCommand)
        .map { r: SlackUserRequest =>
          r match {
            case h: Help => self ! Help()
            case _ => devicesActor ! r
          }
        }
    }

    def isMessage(j: JsonNode) = (
      j.get("type") != null
        && j.get("type").isTextual
        && j.get("type").asText() == "message"
        && j.get("text") != null
        && j.get("text").isTextual
      )
  }

  rtmClient.addListener(Event.MESSAGE, eventListener)
  rtmClient.connect()

  val webApiClient: SlackWebApiClient = SlackClientFactory.createWebApiClient(token);

  override def receive: Receive = {

    case t: ThermostatValue => {
      val th = t.threshosd match {
        case Some(i) => i.toString
        case None => "off"
      }
      post(s"`${t.name}` commits `${t.temp} C` :sun_small_cloud:, threshold `${th}`")
    }

    case t: ThreasholdCrossed => post(s"`${t.name}` threshold `${t.threashold}` crossed `${t.temp} C` :sun_small_cloud:")

    case h: Help => post(HelpText)

    case ThresholdOn(name, threashold) => post(s"`${name}` set threshold on to `$threashold`")

    case ThresholdOff(name) => post(s"`${name}` set threshold off")
  }

  def post(msg: String) = webApiClient.postMessage(channelName, msg, "NestBot", false)
}

object SlackActor {

  trait SlackMessage

  case class ThermostatValue(val name: String, val temp: Int, val threshosd: Option[Int]) extends SlackMessage

  object ThermostatValue {
    def apply(ts: ThermostatActor.ThermostatState, th: Option[Int]): ThermostatValue = ThermostatValue(ts.name, ts.temp, th)
  }

  case class ThreasholdCrossed(val name: String, val temp: Int, val threashold: Int) extends SlackMessage

  case class BadCommand(val desc: String) extends SlackMessage

  case class ThresholdOn(val name: String, val threashold: Int) extends SlackMessage

  case class ThresholdOff(val name: String) extends SlackMessage

  trait SlackUserRequest extends SlackMessage

  case class Help() extends SlackUserRequest

  case class ListThermostats() extends SlackUserRequest

  case class SetThreshold(val name: String, val threshold: Int) extends SlackUserRequest

  case class RemoveThreshold(val name: String) extends SlackUserRequest

}
