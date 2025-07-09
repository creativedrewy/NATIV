package com.creativedrewy.nativ.chainsupport

import com.creativedrewy.nativ.chainsupport.nft.FileDetails
import com.creativedrewy.nativ.chainsupport.nft.NftCreator
import com.creativedrewy.nativ.chainsupport.nft.NftProperties
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class NftPropertiesDeserializer: JsonDeserializer<NftProperties> {

    /**
     * TODO: This is the wrong way to do this deserialization; should be made more generalized
     */
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): NftProperties {
        val propsSrc = json.asJsonObject

        val category = propsSrc.get("category")?.asString ?: ""
        val creators = propsSrc.get("creators").asJsonArray.map {
            context.deserialize<NftCreator>(it, NftCreator::class.java)
        }

        val files = propsSrc.get("files").asJsonArray
        val nftFiles = if (!files.isEmpty && files[0].isJsonObject) {
            files.map {
                context.deserialize(it, FileDetails::class.java)
            }
        } else {
            files.map {
                FileDetails(
                    uri = context.deserialize(it, String::class.java),
                    type = ""
                )
            }
        }

        return NftProperties(category, nftFiles, creators)
    }
}