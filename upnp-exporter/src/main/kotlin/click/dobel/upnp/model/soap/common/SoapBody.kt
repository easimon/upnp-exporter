package click.dobel.upnp.model.soap.common

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

data class SoapBody(
  @JacksonXmlElementWrapper
  val content: Any
)
