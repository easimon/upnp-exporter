package click.dobel.upnp.model.soap.device

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL

data class UpnpDevice(
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
  /** e.g. avm */
  val modelNumber: String?,
  /** e.g http://www.avm.de */
  val modelURL: String?,
  /** does not exist on FritzBox devices */
  val serialNumber: String?,
  @JsonProperty("UDN")
  /** e.g. uuid:12345678-abcd-12ab-12ab-1234567890AB */
  val udn: String,
  @JsonProperty("UPC")
  /** e.g. AVM IGD2 */
  val upc: String?,
  /** icons of the device */
  val iconList: List<UpnpDeviceIcon> = emptyList(),
  /** services of this device */
  val serviceList: List<UpnpService> = emptyList(),
  /** sub-devices */
  val deviceList: List<UpnpDevice> = emptyList(),
  /** e.g. http://fritz.box */
  val presentationURL: URL?
)
