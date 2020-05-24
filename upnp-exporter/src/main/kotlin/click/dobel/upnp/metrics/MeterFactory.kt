package click.dobel.upnp.metrics

import click.dobel.upnp.client.UpnpSoapClient
import click.dobel.upnp.model.internal.Action
import click.dobel.upnp.model.internal.ActionArgument
import click.dobel.upnp.model.internal.ActionArgumentDescription
import click.dobel.upnp.model.internal.Device
import click.dobel.upnp.model.internal.Service
import click.dobel.upnp.model.soap.UpnpRequest
import click.dobel.upnp.util.logger
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import io.micronaut.context.annotation.Context
import java.net.URL

@Context
class MeterFactory(
  private val meterRegistry: MeterRegistry,
  private val soapClient: UpnpSoapClient
) {

  private val errorCounter: Counter = meterRegistry.counter("${PREFIX}collection.errors")

  fun addMetersForAction(action: Action) {
    val inArguments = action.arguments.filter { it.direction.isInArgument() }
    if (inArguments.isNotEmpty()) {
      LOGGER.debug(
        "Not registering meters for {}//{}#{}, requires in-arguments: [{}]",
        action.service.controlUrl,
        action.service.serviceType,
        action.name,
        inArguments.asLogParam()
      )
      return
    }

    if (!isGetterAction(action.name)) {
      LOGGER.debug(
        "Not registering meters for {}//{}#{}, does not match Getter pattern.",
        action.service.controlUrl,
        action.service.serviceType,
        action.name
      )
      return
    }

    val outArguments = action.arguments.filter { it.direction.isOutArgument() }
    val numericOutArguments = outArguments.filter { it.isNumberConvertible }
    val ignoredOutArguments = outArguments.filter { !it.isNumberConvertible }

    LOGGER.info(
      "Trying to register {} meters for {}//{}#{}. included: [{}], excluded [{}]",
      numericOutArguments.size,
      action.service.controlUrl,
      action.service.serviceType,
      action.name,
      numericOutArguments.asLogParam(),
      ignoredOutArguments.asLogParam()
    )

    numericOutArguments
      .forEach { argumentDescription ->
        val gaugeName = gaugeName(action, argumentDescription)
        val gaugeTags = serviceTags(action.service)
          .and(deviceTags(action.service.device))
          .and(argumentTags(argumentDescription))

        val probeResult = createArgumentGetter(argumentDescription)(action)
        if (probeResult.isNaN()) {
          LOGGER.warn(
            "Probe for {}//{}#{}({}) did not return a number, not adding meter",
            action.service.controlUrl,
            action.service.serviceType,
            action.name,
            argumentDescription.displayName
          )
        } else {
          if (argumentDescription.isEnum) {
            argumentDescription.relatedStateVariable.allowedValueList.forEach { allowedValue ->
              registerGauge(
                name = "$gaugeName.state",
                tags = gaugeTags.and(Tag.of("state", allowedValue)),
                item = action,
                getter = createArgumentGetter(argumentDescription) { it.equalsEnumValueAsDouble(allowedValue) }
              )
            }
          }

          registerGauge(
            name = gaugeName,
            tags = gaugeTags,
            item = action,
            getter = createArgumentGetter(argumentDescription)
          )
        }
      }
  }

  private fun createArgumentGetter(
    argumentDescription: ActionArgumentDescription,
    argumentConversion: ((ActionArgument) -> Double) = { it.asDouble() }
  ): ((Action) -> Double) =
    { action ->
      val request = UpnpRequest(action.service.controlUrl, action.service.serviceType, action.name)
      val response = soapClient.executeSoapActionBlocking(request)
      if (response == null || response.isEmpty()) {
        LOGGER.warn("Error collecting {}//{}/{}: No response", action.service.controlUrl, action.name, argumentDescription.displayName)
        errorCounter.increment()
        Double.NaN
      } else {
        val value = response[argumentDescription.name]
        if (value == null) {
          LOGGER.warn("Error collecting {}//{}/{}: Response did not contain expected argument.", action.service.controlUrl, action.name, argumentDescription.displayName)
          errorCounter.increment()
          Double.NaN
        } else {
          argumentConversion(ActionArgument(argumentDescription, value))
        }
      }
    }

  private fun <T : Any> registerGauge(
    name: String,
    tags: Tags,
    description: String? = "",
    item: T,
    getter: (T) -> Number?
  ): Gauge {
    val builder = Gauge.builder(name, item, numberToDouble(getter)).tags(tags)
    return if (description.isNullOrBlank())
      builder.register(meterRegistry)
    else
      builder.description(description).register(meterRegistry)
  }

  companion object {
    private val LOGGER = logger()
    internal const val PREFIX = "upnp-exporter."
    internal val GETTER_REGEX = Regex("^(X_.*_)?Get(.*)")
    internal const val GETTER_REGEX_CAPTURE = "$1$2"

    private fun gaugeName(action: Action, argument: ActionArgumentDescription) =
      (PREFIX + filterActionName(action.name) + "." + argument.displayName).replace(Regex("[^a-zA-Z0-9.]"), ".")

    internal fun isGetterAction(actionName: String) = actionName.matches(GETTER_REGEX)
    internal fun filterActionName(actionName: String) = actionName.replaceFirst(GETTER_REGEX, GETTER_REGEX_CAPTURE)

    private fun List<ActionArgumentDescription>.asLogParam() = joinToString(", ") { "${it.displayName}(${it.dataType})" }

    private fun <T : Any> numberToDouble(f: (T) -> Number?): (T) -> Double = { n ->
      f(n)?.toDouble() ?: Double.NaN
    }

    private fun serviceTags(service: Service) = Tags.of(
      Tag.of("service_type", service.serviceType),
      Tag.of("scdp_url", service.serviceDescriptionUrl.path),
      Tag.of("control_url", service.controlUrl.path),
      Tag.of("event_sub_url", service.eventSubUrl.path)
    )

    private fun deviceTags(device: Device) = Tags.of(
      Tag.of("base_url", URL(device.descriptionUrl, "/").toString().removeSuffix("/")),
      Tag.of("model_name", device.modelName),
      Tag.of("friendly_name", device.friendlyName),
      Tag.of("device_description_url", device.descriptionUrl.path),
      Tag.of("udn", device.udn)
    )

    private fun argumentTags(argumentDescription: ActionArgumentDescription) =
      Tag.of("type", argumentDescription.dataType.asString)
  }
}
