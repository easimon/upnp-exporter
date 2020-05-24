package click.dobel.upnp.model.internal

import click.dobel.upnp.model.soap.device.UpnpServiceId
import click.dobel.upnp.model.soap.service.UpnpStateVariable
import java.net.URL

data class Service(
  /**
   * e.g. urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1
   *
   * This is also the namespace of the request body
   */
  val serviceType: String,
  /** e.g. urn:upnp-org:serviceId:WANCommonIFC1 */
  val serviceId: UpnpServiceId,
  /** e.g. http://129.168.1.1:49000/igdicfgSCPD.xml */
  val serviceDescriptionUrl: URL,
  /** e.g. http://129.168.1.1:49000/igd2upnp/control/WANCommonIFC1 */
  val controlUrl: URL,
  /** e.g. http://129.168.1.1:49000/igd2upnp/control/WANCommonIFC1 */
  val eventSubUrl: URL,

  val serviceStateVariables: Map<String, UpnpStateVariable> = emptyMap(),

  val device: Device
) {
  private val mutableActions: MutableSet<Action> = mutableSetOf()
  val actions: Set<Action> get() = mutableActions

  fun addAction(action: Action) {
    mutableActions.add(action)
  }

  fun addActions(actions: Collection<Action>) {
    mutableActions.addAll(actions)
  }
}
