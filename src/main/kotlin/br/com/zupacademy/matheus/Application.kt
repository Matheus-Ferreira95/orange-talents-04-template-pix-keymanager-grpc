package br.com.zupacademy.matheus

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.zupacademy.matheus")
		.start()
}

