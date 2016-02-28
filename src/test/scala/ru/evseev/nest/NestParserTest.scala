package ru.evseev.nest

import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}

class NestParserTest extends FlatSpec with Matchers with GivenWhenThen {

  var params: java.util.Map[String, Any] = new java.util.HashMap()
  params.put("humidity", 55L)
  params.put("device_id", "t1")
  params.put("ambient_temperature_c", 22.5)
  params.put("name", "term1")

  val input: Map[String, AnyRef] = Map("BvQZ7MuHzZm-T9vvxGCxVRbW9Kk1XSdk" -> params)

  "Parser" should "parse Map into Seq" in {


    val res = NestParser.parseTermostat(input)
    res.length should be(1)
    res(0).name should be("term1")
    res(0).temp should be(22)
  }

}
