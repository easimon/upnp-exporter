package click.dobel.upnp.model.internal

data class ActionArgument(
  val description: ActionArgumentDescription,
  val value: String
) {

  fun asDouble(): Double {
    return when {
      description.isBoolean -> booleanAsDouble()
      description.isNumeric -> numberAsDouble()
      description.isEnum -> enumIndexAsDouble()
      else -> Double.NaN
    }
  }

  fun booleanAsDouble() = if (asBoolean()) 1.0 else 0.0
  fun enumIndexAsDouble() = asEnumIndex().toDouble()
  fun equalsEnumValueAsDouble(str: String) = if (equalsEnumValue(str)) 1.0 else 0.0
  fun numberAsDouble() = stringToDouble(valueOrDefault)

  fun equalsEnumValue(str: String) = str == valueOrDefault

  fun asBoolean(): Boolean {
    return description.isBoolean && stringToBoolean(valueOrDefault)
  }

  fun asEnumIndex(): Int {
    return when {
      description.isEnum -> description.relatedStateVariable.allowedValueList.indexOf(valueOrDefault)
      else -> -2
    }
  }

  private fun stringToBoolean(str: String?): Boolean =
    when (str) {
      "1", "yes", "true" -> true
      "0", "no", "false" -> false
      else -> false
    }

  private fun stringToDouble(str: String?): Double =
    str?.toDoubleOrNull() ?: Double.NaN

  private val valueOrDefault: String = if (!value.isNullOrBlank()) value else description.defaultValue

  override fun toString(): String {
    return valueOrDefault
  }
}
