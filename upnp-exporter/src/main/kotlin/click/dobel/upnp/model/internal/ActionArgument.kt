package click.dobel.upnp.model.internal

data class ActionArgument(
  val description: ActionArgumentDescription,
  val value: String
) {

  fun asDouble(): Double {
    return if (description.isBoolean) {
      if (asBoolean()) 1.0 else 0.0
    } else if (!description.isNumeric) {
      Double.NaN
    } else if (value.isNullOrEmpty()) {
      stringToDouble(description.defaultValue)
    } else {
      value.toDoubleOrNull() ?: Double.NaN
    }
  }

  fun asBoolean(): Boolean {
    if (!description.isBoolean) {
      return false
    } else if (value.isNullOrEmpty()) {
      return stringToBoolean(description.defaultValue)
    }
    return stringToBoolean(value)
  }

  private fun stringToBoolean(str: String?): Boolean =
    when (str) {
      "1", "yes", "true" -> true
      "0", "no", "false" -> false
      else -> false
    }

  private fun stringToDouble(str: String?): Double =
    str?.toDoubleOrNull() ?: Double.NaN

  override fun toString(): String {
    return value
  }
}
