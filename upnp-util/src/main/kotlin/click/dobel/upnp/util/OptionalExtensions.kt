package click.dobel.upnp.util

import java.util.Optional

inline fun <reified T> Optional<T>.orNull(): T? =
  this.orElse(null)
