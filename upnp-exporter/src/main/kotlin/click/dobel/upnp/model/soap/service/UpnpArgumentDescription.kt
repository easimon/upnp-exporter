package click.dobel.upnp.model.soap.service

data class UpnpArgumentDescription(
  /** e.g. NewWANAccessType */
  val name: String,
  /** in, out, inout */
  val direction: UpnpArgumentDirection,
  /** e.g. WANAccessType */
  val relatedStateVariable: String
)
