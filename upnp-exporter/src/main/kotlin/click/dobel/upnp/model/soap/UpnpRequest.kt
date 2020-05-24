package click.dobel.upnp.model.soap

import java.net.URL

data class UpnpRequest(
  val endpointURL: URL,
  val serviceType: String,
  val action: String,
  val params: UpnpRequestParams = UpnpRequestParams.EMPTY
) {
  val requestBody =
    """
      <?xml version="1.0" encoding="utf-8" ?>
      <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
         <s:Body>
            <u:$action xmlns:u="${serviceType}">${params.toRequestString()}</u:$action>
         </s:Body>
      </s:Envelope>
    """.trimIndent()

  val soapAction = "${serviceType}#$action"

  companion object {
    fun of(
      endpointURL: URL,
      serviceType: String,
      action: String,
      params: UpnpRequestParams = UpnpRequestParams.EMPTY
    ) =
      UpnpRequest(
        endpointURL,
        serviceType,
        action,
        params
      )
  }
}
