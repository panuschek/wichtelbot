package de.pan.wichtelbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.env.EnumerablePropertySource
import java.util.*
import java.util.stream.StreamSupport

@SpringBootApplication
class WichtelbotApplication

fun main(args: Array<String>) {
	runApplication<WichtelbotApplication>(*args)
}
