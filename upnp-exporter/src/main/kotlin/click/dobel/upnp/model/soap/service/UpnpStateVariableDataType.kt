package click.dobel.upnp.model.soap.service

import click.dobel.upnp.util.logger
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime

private val NUMERIC_TYPES = setOf(
  Byte::class.java, Short::class.java, Long::class.java, Int::class.java,
  Float::class.java, Double::class.java
)

enum class UpnpStateVariableDataType(
  @get:JsonValue
  val asString: String,
  val javaType: Class<out Any>
) {
  UI1("ui1", Int::class.java),
  UI2("ui2", Int::class.java),
  UI4("ui4", Long::class.java),
  I1("i1", Byte::class.java),
  I2("i2", Short::class.java),
  I4("i4", Int::class.java),
  INT("int", Int::class.java),
  R4("r4", Float::class.java),
  R8("r8", Double::class.java),
  NUMBER("number", Double::class.java),
  FIXED_14_4("fixed.14.4", Double::class.java),
  FLOAT("float", Double::class.java),
  CHAR("char", Char::class.java),
  STRING("string", String::class.java),
  DATE("date", LocalDate::class.java),
  DATETIME("datetime", LocalDateTime::class.java),
  DATETIME_TZ("datetime.tz", OffsetDateTime::class.java),
  TIME("time", LocalTime::class.java),
  TIME_TZ("time.tz", OffsetTime::class.java),
  BOOLEAN("boolean", Boolean::class.java), // must accept 0, 1, true, false, yes, no
  BIN_BASE64("bin.base64", String::class.java),
  BIN_HEX("bin.hex", String::class.java),
  URI("uri", URL::class.java),
  UUID("uuid", java.util.UUID::class.java);

  companion object {
    private val LOGGER = logger()
    private val ALL_VALUES_MAP = values().map { it.asString to it }.toMap()

    @JvmStatic
    @JsonCreator
    fun safeValueOf(str: String): UpnpStateVariableDataType {
      val result = ALL_VALUES_MAP[str]
      if (result == null) {
        LOGGER.warn("Unsupported Upnp data type: {}, overriding with {}.", str, STRING.asString)
      }
      return result ?: STRING
    }
  }

  @get:JsonIgnore
  val isNumeric = NUMERIC_TYPES.contains(this.javaType) || Number::class.java.isAssignableFrom(this.javaType)

  @get:JsonIgnore
  val isBoolean = Boolean::class.java == this.javaType

  @get:JsonIgnore
  val isString = String::class.java == this.javaType
}

