package com.creativedrewy.nativ.chainsupport

import com.creativedrewy.nativ.chainsupport.nft.NftProperties
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class NftPropertiesDeserializer: JsonDeserializer<NftProperties> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): NftProperties {
        val propsSrc = json.asJsonObject
        val files = propsSrc.get("files").asJsonArray

        return if (!files.isEmpty && files[0].isJsonObject) {
            context.deserialize(json, NftProperties::class.java)
        } else {
            NftProperties("", listOf(), listOf())
        }
    }
}