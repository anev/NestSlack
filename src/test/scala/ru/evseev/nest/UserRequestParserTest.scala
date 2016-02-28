package ru.evseev.nest

import org.scalatest.{FlatSpec, Matchers}
import ru.evseev.nest.UserRequestParser._
import ru.evseev.nest.actors.SlackActor.{ListThermostats, RemoveThreshold, SetThreshold}

class UserRequestParserTest extends FlatSpec with Matchers {

  "UserRequestParser" should "return None" in {
    parseCommand("qwe asd") should be(None)

    parseCommand("nest list1") should be(None)
  }

  it should "return ListThermostats if cmd is [nest list]" in {
    parseCommand("nest list") should be(Some(ListThermostats()))
  }

  it should "return ListThermostats if cmd is [nEst LIST]" in {
    parseCommand("nEst LIST") should be(Some(ListThermostats()))
  }

  it should "return ListThermostats if cmd is [nest threshold off theName]" in {
    parseCommand("nest threshold off theName") should be(Some(RemoveThreshold("theName")))
  }

  it should "return ListThermostats if cmd is [nest threshold OFF theName]" in {
    parseCommand("nest threshold OFF theName") should be(Some(RemoveThreshold("theName")))
  }

  it should "return ListThermostats if cmd is [nest threshold theName 15]" in {
    parseCommand("nest threshold theName 15") should be(Some(SetThreshold("theName", 15)))
  }

  it should "return ListThermostats if cmd is [nest thREshold theName 15]" in {
    parseCommand("nest thREshold theName 15") should be(Some(SetThreshold("theName", 15)))
  }

}
