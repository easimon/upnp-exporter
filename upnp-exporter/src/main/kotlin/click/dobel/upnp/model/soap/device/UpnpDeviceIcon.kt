package click.dobel.upnp.model.soap.device

import com.fasterxml.jackson.annotation.JsonProperty
import javax.activation.MimeType

data class UpnpDeviceIcon(
  @JsonProperty("mimetype")
  val mimeType: MimeType,
  val width: Int,
  val height: Int,
  val depth: Int,
  val url: String
)
