package ru.evseev.nest

import java.util.NoSuchElementException

import akka.actor.{ActorSystem, Props}
import ru.evseev.nest.actors.MainActor

/**
  * Created by anev on 24/02/16.
  */
object Main {

  def main(args: Array[String]) {
  }

  try {
    val slackToken = sys.env("SLACK_TOKEN")
    val nestToken = sys.env("NEST_TOKEN")

    var channelName = sys.env.getOrElse("SLACK_CHANNEL", "#general")
    if (!channelName.startsWith("#")) {
      channelName = "#" + channelName
    }
    val nestUri = sys.env.getOrElse("NEST_URI", "wss://developer-api.nest.com/devices")

    val as = ActorSystem("nest")
    as.actorOf(Props(classOf[MainActor], nestUri, nestToken, slackToken, channelName), "main")

  } catch {
    case e: NoSuchElementException => {
      println("You should set SLACK_TOKEN and NEST_TOKEN env variables.")
      println("Also you can set NEST_URI and SLACK_CHANNEL env variables.")
      sys.exit(1)
    }
  }
}
