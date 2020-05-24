package click.dobel.upnp

import click.dobel.upnp.application.UpnpExporter
import io.resourcepool.ssdp.client.SsdpClient
import io.resourcepool.ssdp.model.DiscoveryListener
import io.resourcepool.ssdp.model.SsdpRequest
import io.resourcepool.ssdp.model.SsdpService
import io.resourcepool.ssdp.model.SsdpServiceAnnouncement
import sun.misc.Signal
import kotlin.system.exitProcess

object SSDPExplorer {

  @JvmStatic
  fun main(args: Array<String>) {
    Signal.handle(Signal(UpnpExporter.SIGINT)) { _ -> exitProcess(0) }
    Signal.handle(Signal(UpnpExporter.SIGTERM)) { _ -> exitProcess(1) }

    val client = SsdpClient.create();
    val all = SsdpRequest.discoverAll();
    client.discoverServices(all, object : DiscoveryListener {
      override fun onFailed(e: Exception) {
        println("Exception caught: $e")
      }

      override fun onServiceDiscovered(service: SsdpService) {
        println("Found service: $service");
      }

      override fun onServiceAnnouncement(announcement: SsdpServiceAnnouncement) {
        println("Service announced something: $announcement");
      }
    });

    while (true) {
      Thread.sleep(1000)
    }
  }
}
