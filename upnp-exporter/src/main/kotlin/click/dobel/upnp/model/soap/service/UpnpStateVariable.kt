package click.dobel.upnp.model.soap.service

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class UpnpStateVariable(
  @JacksonXmlProperty(isAttribute = true)
  val sendEvents: UpnpSendEvents,
  val name: String,
  val dataType: UpnpStateVariableDataType,
  val defaultValue: String = "",
  val allowedValueList: List<String> = emptyList()
)
