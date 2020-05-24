package click.dobel.upnp.model.internal

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL

data class Device(
  /** e.g. urn:schemas-upnp-org:device:InternetGatewayDevice:2 */
  val deviceType: String,
  /** e.g. InternetGatewayDeviceV2 - MyFritzBox */
  val friendlyName: String,
  /** e.g. AVM Berlin */
  val manufacturer: String,
  /** e.g. http://www.avm.de */
  val manufacturerURL: String?,
  /** e.g. FRITZ!Box 7590 */
  val modelDescription: String?,
  /** e.g. FRITZ!Box 7590 */
  val modelName: String,
  @JsonProperty("UDN")
  /** e.g. uuid:12345678-abcd-12ab-12ab-1234567890AB */
  val udn: String,

  /** eg. http://192.168.1.1:49000/igd2desc.xml */
  val descriptionUrl: URL
) {
  private val mutableServices: MutableSet<Service> = mutableSetOf()
  val services: Set<Service> get() = mutableServices

  fun addService(service: Service) {
    mutableServices.add(service)
  }

  fun addServices(services: Collection<Service>) {
    mutableServices.addAll(services)
  }
}
