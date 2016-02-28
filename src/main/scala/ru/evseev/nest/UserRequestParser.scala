package ru.evseev.nest

import ru.evseev.nest.actors.SlackActor._

object UserRequestParser {

  val HelpPattern = "(?i)nest help".r
  val ListPattern = "(?i)nest list".r
  val SetThresholdPattern = "(?i)nest threshold (.*) (\\d+)".r
  val OffThresholdPattern = "(?i)nest threshold off (.*)".r

  def parseCommand(cmd: String): Option[SlackUserRequest] = {

    cmd match {

      case HelpPattern() => Some(Help())

      case ListPattern() => Some(ListThermostats())

      case SetThresholdPattern(name, value) => Some(SetThreshold(name, value.toInt))

      case OffThresholdPattern(n) => Some(RemoveThreshold(n))

      case _ => None
    }
  }

}
