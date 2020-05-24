package click.dobel.upnp.metrics

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class MeterFactoryTest : StringSpec({

  val match = MeterFactory.GETTER_REGEX
  val capture = MeterFactory.GETTER_REGEX_CAPTURE

  "Getter matches getter action name" {
    "GetStuff".matches(match) shouldBe true
  }

  "Getter matches X_getter action name" {
    "X_AVM_GetStuff".matches(match) shouldBe true
  }

  "Getter does not match other action names" {
    "SetStuff".matches(match) shouldBe false
    "Scan".matches(match) shouldBe false
  }

  "Get is cut off from getter name" {
    "GetStuff".replaceFirst(match, capture) shouldBe "Stuff"
  }

  "Get is cut off from X_getter name" {
    "X_AVM_GetStuff".replaceFirst(match, capture) shouldBe "X_AVM_Stuff"
  }

  "Other action names are left unmodified" {
    "SetStuff".replaceFirst(match, capture) shouldBe "SetStuff"
    "Scan".replaceFirst(match, capture) shouldBe "Scan"
  }
})
