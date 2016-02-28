package ru.evseev.nest.actors

import org.scalatest.{FlatSpec, Matchers}
import ru.evseev.nest.actors.ThermostatActor._

class ThermostatActorTest extends FlatSpec with Matchers {

  "ThermostatActor" should "detect threshold cross" in {
    thresholdCrossed(20, 19, 21) should be(true)
    thresholdCrossed(20, 21, 19) should be(true)
    thresholdCrossed(20, 20, 19) should be(true)
    thresholdCrossed(20, 20, 20) should be(false)
    thresholdCrossed(20, 19, 20) should be(false)
  }
}
