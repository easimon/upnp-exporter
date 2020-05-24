package click.dobel.upnp.model.soap.service

import click.dobel.upnp.model.soap.common.UpnpSpecVersion
import com.fasterxml.jackson.annotation.JsonIgnore

data class UpnpServiceDescription(
  val specVersion: UpnpSpecVersion,
  val actionList: List<UpnpAction> = emptyList(),
  val serviceStateTable: List<UpnpStateVariable> = emptyList()
) {

  @get:JsonIgnore
  val serviceStateTableMap = serviceStateTable.map { it.name to it }.toMap()
}
