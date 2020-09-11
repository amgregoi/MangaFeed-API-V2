package com.amgregoire.manga

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.Configuration

@Configuration
@EnableAutoConfiguration
@SpringBootApplication
class FableApplication : SpringBootServletInitializer()
{
    companion object
    {
        @JvmStatic
        fun main(args: Array<String>)
        {
            SpringApplication.run(FableApplication::class.java, *args)
        }
    }

    @Override
    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder
    {
        return application.sources(FableApplication::class.java)
    }
}




