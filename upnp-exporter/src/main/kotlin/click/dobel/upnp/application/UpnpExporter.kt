package click.dobel.upnp.application

import io.micronaut.runtime.Micronaut
import sun.misc.Signal
import kotlin.system.exitProcess

object UpnpExporter {

  internal const val SIGINT = "INT"
  internal const val SIGTERM = "TERM"

  @JvmStatic
  fun main(args: Array<String>) {
    Signal.handle(Signal(SIGINT)) { _ -> exitProcess(0) }
    Signal.handle(Signal(SIGTERM)) { _ -> exitProcess(1) }
    Micronaut.build()
      .packages("click.dobel")
      .mainClass(UpnpExporter.javaClass)
      .start()
  }
}
