package click.dobel.upnp.hacks

import java.nio.charset.Charset
import java.nio.charset.spi.CharsetProvider

/**
 * The FritzBox SOAP Server responds incorrectly with
 * `Content-Type: text/xml; charset="utf-8"` (with quotes)
 * So this CharsetProvider registers UTF-8 using its name surrounded by quotes.
 */
class FritzboxCharsetProvider : CharsetProvider() {

  companion object {
    private const val FRITZ_BOX_UTF_8 = "\"utf-8\""
    private val CHARSET = Charsets.UTF_8

    private val CHARSETS_MAP = mapOf(FRITZ_BOX_UTF_8 to CHARSET)
    private val CHARSETS = CHARSETS_MAP.values
  }

  override fun charsets(): Iterator<Charset> {
    return CHARSETS.iterator()
  }

  override fun charsetForName(charsetName: String?): Charset? {
    return CHARSETS_MAP[charsetName?.toLowerCase()]
  }
}
