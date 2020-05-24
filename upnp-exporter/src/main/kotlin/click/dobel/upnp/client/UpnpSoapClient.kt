package click.dobel.upnp.client

import click.dobel.upnp.model.soap.UpnpConstants.CONTENT_TYPE
import click.dobel.upnp.model.soap.UpnpRequest
import click.dobel.upnp.model.soap.common.SoapEnvelope
import click.dobel.upnp.model.soap.device.UpnpDeviceDescription
import click.dobel.upnp.model.soap.service.UpnpServiceDescription
import click.dobel.upnp.util.logger
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.cache.annotation.Cacheable
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.client.exceptions.ReadTimeoutException
import io.micronaut.retry.annotation.Retryable
import io.reactivex.Single
import java.net.URI
import javax.inject.Singleton

@Singleton
open class UpnpSoapClient(
  objectMapper: ObjectMapper, // workaround for https://github.com/micronaut-projects/micronaut-core/issues/2905
  @Client("UPNPClient") val client: RxHttpClient
) {

  companion object {
    val LOGGER = logger()
    const val HEADER_SOAP_ACTION = "SOAPAction"
    const val CACHE_NAME_UPNP_REQUESTS = "upnp-requests"
    const val CACHE_NAME_UPNP_DESCRIPTIONS = "upnp-descriptions"

    const val RETRY_DELAY = "200ms"
    const val RETRY_ATTEMPTS = "3"
  }

  @Cacheable(cacheNames = [CACHE_NAME_UPNP_DESCRIPTIONS], atomic = true)
  @Retryable(delay = RETRY_DELAY, attempts = RETRY_ATTEMPTS, includes = [ReadTimeoutException::class])
  open fun getDeviceDescriptionBlocking(location: URI) = getDeviceDescription(location).blockingGet()

  @Cacheable(cacheNames = [CACHE_NAME_UPNP_DESCRIPTIONS], atomic = true)
  @Retryable(delay = RETRY_DELAY, attempts = RETRY_ATTEMPTS, includes = [ReadTimeoutException::class])
  open fun getServiceDescriptionBlocking(location: URI) = getServiceDescription(location).blockingGet()

  @Cacheable(cacheNames = [CACHE_NAME_UPNP_REQUESTS], atomic = true)
  open fun executeSoapActionBlocking(request: UpnpRequest): Map<String, String>? {
    try {
      LOGGER.trace("Sending: {}", request)
      val result = doExecuteAction(request)
      LOGGER.trace("Received: {}", result.response)
      return result.response
    } catch (e: HttpClientResponseException) {
      when (e.status.code) {
        in 400..499 -> LOGGER.error("Client error {} {} for {}//{}.", e.status.code, e.status, request.endpointURL, request.soapAction)
        in 500..599 -> LOGGER.warn("Server error {} {} for {}//{}.", e.status.code, e.status, request.endpointURL, request.soapAction)
        else -> LOGGER.error("Unexpected error {} {} for {}//{}.", e.status.code, e.status, request.endpointURL, request.soapAction, e)
      }
    } catch (e: Exception) {
      LOGGER.error("Unexpected SOAP action error {} for {}//{}.", e.message, request.endpointURL, request.soapAction, e)
    }
    return null
  }

  @Retryable(delay = RETRY_DELAY, attempts = RETRY_ATTEMPTS, includes = [ReadTimeoutException::class])
  protected open fun doExecuteAction(request: UpnpRequest) = exchange<SoapEnvelope>(request).blockingGet()

  private fun getDeviceDescription(location: URI): Single<UpnpDeviceDescription> {
    val httpRequest = HttpRequest.GET<Any>(location)
      .accept(CONTENT_TYPE)
    return client.retrieve(httpRequest, UpnpDeviceDescription::class.java).firstOrError()
  }

  private fun getServiceDescription(location: URI): Single<UpnpServiceDescription> {
    val httpRequest = HttpRequest.GET<Any>(location).accept(CONTENT_TYPE)
    return client.retrieve(httpRequest, UpnpServiceDescription::class.java).firstOrError()
  }

  inline fun <reified T> exchange(request: UpnpRequest): Single<T> {
    val httpRequest = HttpRequest
      .POST(request.endpointURL.toURI(), request.requestBody)
      .header(HEADER_SOAP_ACTION, request.soapAction)
      .contentType(CONTENT_TYPE)

    return client.retrieve(httpRequest, T::class.java).firstOrError()
  }
}
