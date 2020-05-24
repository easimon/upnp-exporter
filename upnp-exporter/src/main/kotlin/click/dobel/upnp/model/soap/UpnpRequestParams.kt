package click.dobel.upnp.model.soap

interface UpnpRequestParams {
  companion object {
    val EMPTY = object : UpnpRequestParams {
      override fun toRequestString() = ""
    }
  }

  fun toRequestString(): String
}

