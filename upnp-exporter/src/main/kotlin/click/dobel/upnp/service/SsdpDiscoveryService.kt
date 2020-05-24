package click.dobel.upnp.service

import click.dobel.upnp.controller.UpnpServicesController
import click.dobel.upnp.util.logger
import io.micronaut.context.annotation.Context
import io.resourcepool.ssdp.client.SsdpClient
import io.resourcepool.ssdp.model.DiscoveryListener
import io.resourcepool.ssdp.model.SsdpRequest
import io.resourcepool.ssdp.model.SsdpService
import io.resourcepool.ssdp.model.SsdpServiceAnnouncement
import javax.annotation.PostConstruct

@Context
class SsdpDiscoveryService(
  val servicesModel: UpnpServicesController
) {

  companion object {
    private val LOGGER = logger()
  }

  @PostConstruct
  fun startDiscovery() {
    val client = SsdpClient.create();
    val all = SsdpRequest.discoverAll();
    client.discoverServices(all, object : DiscoveryListener {
      override fun onFailed(e: Exception) {
        LOGGER.error("Discovery failure", e)
      }

      override fun onServiceDiscovered(service: SsdpService) {
        LOGGER.debug("Service discovered: {}", service)
        servicesModel.addDeviceBySsdpService(service)
      }

      override fun onServiceAnnouncement(announcement: SsdpServiceAnnouncement) {
        LOGGER.info("Service announcement: {}", announcement)
      }
    });
  }
}
