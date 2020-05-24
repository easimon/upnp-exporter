package click.dobel.upnp.application

import io.micronaut.context.annotation.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("upnp")
class UpnpExporterConfigurationProperties {
  var actionCacheInterval: Duration = Duration.ofSeconds(5)
  var descriptionCacheInterval: Duration = Duration.ofMinutes(15)
  var prometheusStepInterval: String = "PT1M"
  var blacklist: BlackList = BlackList()

  @ConfigurationProperties("blacklist")
  class BlackList {
    var devices: MutableList<String> = mutableListOf()
    var services: MutableList<String> = mutableListOf()
  }
}

