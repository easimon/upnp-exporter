package click.dobel.upnp.model.soap.device

import com.fasterxml.jackson.annotation.JsonProperty

data class UpnpService(
  /**
   * e.g. urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1
   *
   * This is also the namespace of the request body
   */
  val serviceType: String,
  /** e.g. urn:upnp-org:serviceId:WANCommonIFC1 */
  val serviceId: UpnpServiceId,
  /** e.g. /igdicfgSCPD.xml */
  @JsonProperty("SCPDURL")
  val serviceDescriptionUrl: String,
  /** e.g. /igd2upnp/control/WANCommonIFC1 */
  @JsonProperty("controlURL")
  val controlUrl: String,
  /** e.g. /igd2upnp/control/WANCommonIFC1 */
  @JsonProperty("eventSubURL")
  val eventSubUrl: String
)
