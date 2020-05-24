package click.dobel.fritzbox.util

import click.dobel.upnp.util.orNull
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.util.Optional

internal class OptionalExtensionsTest : StringSpec({
  "returns value for non-null optional" {
    Optional.of("Something").orNull() shouldBe "Something"
  }
  "returns value for empty optional" {
    Optional.empty<String>().orNull() shouldBe null
  }
})
