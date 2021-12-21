package de.pan.wichtelbot.service

import de.pan.wichtelbot.entity.CleverbotResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(value = "cleverbot", url = "https://www.cleverbot.com/getreply/")
interface CleverbotService {
    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun think(@RequestParam("key") apiKey: String, @RequestParam("cs") cs: String, @RequestParam("input") input: String): CleverbotResponse
}