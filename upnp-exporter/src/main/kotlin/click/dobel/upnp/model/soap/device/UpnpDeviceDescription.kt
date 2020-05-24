package click.dobel.upnp.model.soap.device

import click.dobel.upnp.model.soap.common.UpnpSpecVersion

data class UpnpDeviceDescription(
  val specVersion: UpnpSpecVersion,
  val device: UpnpDevice
)
