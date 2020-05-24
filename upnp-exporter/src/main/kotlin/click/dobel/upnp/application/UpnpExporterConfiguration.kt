package click.dobel.upnp.application

import click.dobel.upnp.util.logger
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.micronaut.context.event.BeanCreatedEvent
import io.micronaut.context.event.BeanCreatedEventListener
import javax.inject.Singleton

@Singleton
class UpnpExporterConfiguration {

  companion object {
    private val LOGGER = logger()
  }

  @Singleton
  class KotlinModuleConfigurer : BeanCreatedEventListener<ObjectMapper> {
    override fun onCreated(event: BeanCreatedEvent<ObjectMapper>): ObjectMapper {
      val mapper = event.bean

      mapper.registerModule(
        KotlinModule.Builder()
          .nullIsSameAsDefault(true)
          .nullToEmptyCollection(true)
          .nullToEmptyMap(true)
          .build()
      )
      LOGGER.debug("Registered configured Kotlin module")
      mapper.findAndRegisterModules()

      return mapper
    }
  }
}

