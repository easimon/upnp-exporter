package click.dobel.upnp.model.soap

import io.micronaut.http.MediaType

object UpnpConstants {
  val CONTENT_TYPE = MediaType(MediaType.TEXT_XML, mapOf(MediaType.CHARSET_PARAMETER to Charsets.UTF_8.name().toLowerCase()))

  const val NS_SERVICE = "urn:schemas-upnp-org:service-1-0"
  const val NS_DEVICE = "urn:schemas-upnp-org:device-1-0"
}
