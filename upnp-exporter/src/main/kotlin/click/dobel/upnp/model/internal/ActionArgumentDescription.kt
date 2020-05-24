package click.dobel.upnp.model.internal

import click.dobel.upnp.model.soap.service.UpnpArgumentDirection
import click.dobel.upnp.model.soap.service.UpnpStateVariable

data class ActionArgumentDescription(
  /** e.g. NewWANAccessType */
  val name: String,
  /** in, out, inout */
  val direction: UpnpArgumentDirection,
  /** e.g. WANAccessType */
  val relatedStateVariable: UpnpStateVariable
) {
  val dataType = relatedStateVariable.dataType
  val isNumeric = relatedStateVariable.dataType.isNumeric
  val isBoolean = relatedStateVariable.dataType.isBoolean
  val isEnum = relatedStateVariable.dataType.isString && relatedStateVariable.allowedValueList.isNotEmpty()
  val isNumberConvertible = isNumeric || isBoolean || isEnum
  val displayName = relatedStateVariable.name
  val defaultValue = relatedStateVariable.defaultValue
}
