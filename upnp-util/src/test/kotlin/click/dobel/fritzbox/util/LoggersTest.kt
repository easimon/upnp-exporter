package click.dobel.fritzbox.util

import click.dobel.upnp.util.logger
import io.kotlintest.matchers.types.shouldBeSameInstanceAs
import io.kotlintest.specs.StringSpec

internal class LoggersKtTest : StringSpec({
  "Returns same Logger for Class and Companion" {
    logger(LoggerTestClass::class) shouldBeSameInstanceAs logger(LoggerTestClass::class)
  }

  "Returns Logger for the enclosing class when called without parameters" {
    LoggerTestClass.LOGGER shouldBeSameInstanceAs logger(LoggerTestClass::class)
  }
})

internal class LoggerTestClass {
  companion object {
    val LOGGER = logger()
  }
}
