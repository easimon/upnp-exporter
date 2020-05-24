package click.dobel.upnp.model.soap.common

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(namespace = "http://schemas.xmlsoap.org/soap/envelope/", localName = "env")
data class SoapEnvelope(
  @JsonProperty("Body")
  private val body: Map<String, Map<String, String>>
) {
  @JsonIgnore
  val response = body.values.first()
}
