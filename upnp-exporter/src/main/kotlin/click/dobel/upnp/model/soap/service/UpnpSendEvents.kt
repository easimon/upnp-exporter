package click.dobel.upnp.model.soap.service

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue

enum class UpnpSendEvents(
  @get:JsonValue
  val asString: String,
  @get:JsonIgnore
  val asBoolean: Boolean
) {
  YES("yes", true),
  NO("no", false)
}
