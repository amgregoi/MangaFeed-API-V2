package com.amgregoire.manga

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaAuditing
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}

