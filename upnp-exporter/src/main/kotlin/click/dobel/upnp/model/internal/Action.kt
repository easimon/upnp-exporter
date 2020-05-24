package click.dobel.upnp.model.internal

data class Action(
  /** e.g. GetCommonLinkProperties */
  val name: String,
  val arguments: List<ActionArgumentDescription>,

  val service: Service
) {
  val hasInArguments = arguments.any { it.direction.isInArgument() }
  val hasOutArguments = arguments.any { it.direction.isOutArgument() }
}
