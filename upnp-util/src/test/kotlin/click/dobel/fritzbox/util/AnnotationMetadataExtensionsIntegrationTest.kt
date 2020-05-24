package click.dobel.fritzbox.util

import click.dobel.upnp.util.stringValue
import click.dobel.upnp.util.stringValues
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.test.annotation.MicronautTest
import javax.inject.Singleton

@MicronautTest
internal class AnnotationMetadataExtensionsTest(
  context: ApplicationContext
) : StringSpec({

  "stringValue translates to java class" {
    val beanDefinition = context.getBeanDefinition(TestAnnotatedClass::class.java)

    beanDefinition.stringValue(TestAnnotation::class, VALUE) shouldBe "test"
  }

  "stringValues translates to java class" {
    val beanDefinition = context.getBeanDefinition(TestAnnotatedClass::class.java)

    beanDefinition.stringValues(TestAnnotation::class, VALUES) shouldBe arrayOf("test1", "test2")
  }
})

private const val VALUE = "value"
private const val VALUES = "values"

internal annotation class TestAnnotation(
  val value: String,
  val values: Array<String>
)

@Singleton
@TestAnnotation(value = "test", values = ["test1", "test2"])
internal class TestAnnotatedClass
