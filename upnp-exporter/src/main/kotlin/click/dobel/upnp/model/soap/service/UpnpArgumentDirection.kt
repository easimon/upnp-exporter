package click.dobel.upnp.model.soap.service

import com.fasterxml.jackson.annotation.JsonValue

enum class UpnpArgumentDirection(
  @get:JsonValue
  val value: String
) {
  IN("in"),
  OUT("out"),
  INOUT("inout");

  fun isInArgument() = this == IN || this == INOUT

  fun isOutArgument() = this == OUT || this == INOUT
}
