package ru.evseev.nest

import ru.evseev.nest.actors.ThermostatActor.ThermostatState


object NestParser {

  def parseTermostat(thermoMap: Map[String, AnyRef]): Seq[ThermostatState] = {
    (for {
      (deviceId: String, deviceParams: java.util.HashMap[String, Any]) <- thermoMap
    } yield {
      ThermostatState(
        deviceId,
        deviceParams.get("name").asInstanceOf[String],
        deviceParams.get("ambient_temperature_c").asInstanceOf[Double].toInt
      )
    }).toSeq
  }

}
