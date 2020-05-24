package click.dobel.upnp.controller

import click.dobel.upnp.application.UpnpExporterConfigurationProperties
import click.dobel.upnp.client.UpnpSoapClient
import click.dobel.upnp.metrics.MeterFactory
import click.dobel.upnp.model.internal.Action
import click.dobel.upnp.model.internal.ActionArgumentDescription
import click.dobel.upnp.model.internal.Device
import click.dobel.upnp.model.internal.Service
import click.dobel.upnp.model.soap.device.UpnpDevice
import click.dobel.upnp.model.soap.device.UpnpService
import click.dobel.upnp.model.soap.service.UpnpAction
import click.dobel.upnp.model.soap.service.UpnpArgumentDescription
import click.dobel.upnp.model.soap.service.UpnpServiceDescription
import click.dobel.upnp.model.soap.service.UpnpStateVariable
import click.dobel.upnp.util.logger
import io.resourcepool.ssdp.model.SsdpService
import java.net.URL
import javax.inject.Singleton

@Singleton
class UpnpServicesController(
  private val soapClient: UpnpSoapClient,
  private val configuration: UpnpExporterConfigurationProperties,
  private val meterFactory: MeterFactory
) {

  companion object {
    private val LOGGER = logger()
  }

  private val devices: MutableSet<Device> = mutableSetOf()

  fun addDeviceBySsdpService(ssdpService: SsdpService) {
    if (configuration.blacklist.services.contains(ssdpService.serviceType)) {
      LOGGER.info("Ignoring blacklisted service type {} ({}) at {}.",
        ssdpService.serviceType,
        ssdpService.serialNumber,
        ssdpService.location
      )
      return
    }

    addDeviceByLocation(URL(ssdpService.location))
  }

  private fun addDeviceByLocation(deviceDescriptionUrl: URL) {
    val description = soapClient.getDeviceDescriptionBlocking(deviceDescriptionUrl.toURI())
    addDevice(deviceDescriptionUrl, description.device)
  }

  private fun addDevice(deviceDescriptionUrl: URL, upnpDevice: UpnpDevice) {
    upnpDevice.deviceList.forEach { addDevice(deviceDescriptionUrl, it) }

    if (configuration.blacklist.devices.contains(upnpDevice.deviceType)) {
      LOGGER.info("Ignoring blacklisted device {} ({}) / {} / {} at {}.",
        upnpDevice.modelName,
        upnpDevice.friendlyName,
        upnpDevice.deviceType,
        upnpDevice.udn,
        deviceDescriptionUrl
      )
      return
    }

    synchronized(devices) {
      if (devices.any { it.descriptionUrl == deviceDescriptionUrl && it.udn == upnpDevice.udn }) {
        LOGGER.debug("Ignoring duplicate device discovery for {} ({}) / {} / {} at {}.",
          upnpDevice.modelName,
          upnpDevice.friendlyName,
          upnpDevice.deviceType,
          upnpDevice.udn,
          deviceDescriptionUrl
        )
        return
      }
      LOGGER.info("Adding device {} ({}) / {} / {} at {}.",
        upnpDevice.modelName,
        upnpDevice.friendlyName,
        upnpDevice.deviceType,
        upnpDevice.udn,
        deviceDescriptionUrl
      )
      val newDevice = createDevice(deviceDescriptionUrl, upnpDevice)
      newDevice.services
        .flatMap { it.actions }
        .forEach { meterFactory.addMetersForAction(it) }
      devices.add(newDevice)
    }
  }

  private fun createDevice(deviceDescriptionUrl: URL, upnpDevice: UpnpDevice): Device {
    val device = Device(
      deviceType = upnpDevice.deviceType,
      friendlyName = upnpDevice.friendlyName,
      manufacturer = upnpDevice.manufacturer,
      manufacturerURL = upnpDevice.manufacturerURL,
      modelDescription = upnpDevice.modelDescription,
      modelName = upnpDevice.modelName,
      udn = upnpDevice.udn,
      descriptionUrl = deviceDescriptionUrl
    )

    device.addServices(
      upnpDevice.serviceList.map { service ->
        val discoveryUrl = URL(deviceDescriptionUrl, service.serviceDescriptionUrl)
        val description = soapClient.getServiceDescriptionBlocking(discoveryUrl.toURI())
        createService(service, description, device)
      }
    )

    return device
  }

  private fun createService(upnpService: UpnpService, serviceDescription: UpnpServiceDescription, device: Device): Service {
    val service = Service(
      serviceType = upnpService.serviceType,
      serviceId = upnpService.serviceId,
      serviceDescriptionUrl = URL(device.descriptionUrl, upnpService.serviceDescriptionUrl),
      controlUrl = URL(device.descriptionUrl, upnpService.controlUrl),
      eventSubUrl = URL(device.descriptionUrl, upnpService.eventSubUrl),
      serviceStateVariables = serviceDescription.serviceStateTableMap,
      device = device
    )
    service.addActions(serviceDescription.actionList.map { createAction(it, service) })
    return service
  }

  private fun createAction(upnpAction: UpnpAction, service: Service): Action {
    val action = Action(
      name = upnpAction.name,
      arguments = upnpAction.argumentList.map { createActionArgumentDescription(it, service.serviceStateVariables) },
      service = service
    )
    return action
  }

  private fun createActionArgumentDescription(
    upnpArgumentDescription: UpnpArgumentDescription,
    stateTable: Map<String, UpnpStateVariable>
  ): ActionArgumentDescription {
    val relatedStateVariable = stateTable[upnpArgumentDescription.relatedStateVariable]
      ?: throw IllegalArgumentException("Argument [${upnpArgumentDescription.name}] with " +
        "unknown related state variable [${upnpArgumentDescription.relatedStateVariable}]. " +
        "Known state variables: [${stateTable.keys.joinToString(", ")}].")

    return ActionArgumentDescription(
      name = upnpArgumentDescription.name,
      direction = upnpArgumentDescription.direction,
      relatedStateVariable = relatedStateVariable
    )
  }
}
