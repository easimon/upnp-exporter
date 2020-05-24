package click.dobel.upnp.model.soap.device

import com.fasterxml.jackson.annotation.JsonValue

data class UpnpServiceId(
  @get:JsonValue
  val id: String
)
