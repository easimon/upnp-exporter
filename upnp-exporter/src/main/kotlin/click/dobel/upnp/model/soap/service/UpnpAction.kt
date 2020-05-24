package click.dobel.upnp.model.soap.service

data class UpnpAction(
  /** e.g. GetCommonLinkProperties */
  val name: String,
  val argumentList: List<UpnpArgumentDescription> = emptyList()
)
