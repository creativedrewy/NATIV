package com.creativedrewy.solananft.database

import androidx.room.TypeConverter
import com.creativedrewy.solananft.dto.DasAttribute
import com.creativedrewy.solananft.dto.DasCreator
import com.creativedrewy.solananft.dto.DasFile
import com.creativedrewy.solananft.dto.DasGrouping
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromListDasGrouping(list: List<DasGrouping>?): String = gson.toJson(list)

    @TypeConverter
    fun toListDasGrouping(json: String): List<DasGrouping> =
        gson.fromJson(json, object : TypeToken<List<DasGrouping>>() {}.type) ?: emptyList()

    @TypeConverter
    fun fromListDasCreator(list: List<DasCreator>?): String = gson.toJson(list)

    @TypeConverter
    fun toListDasCreator(json: String): List<DasCreator> =
        gson.fromJson(json, object : TypeToken<List<DasCreator>>() {}.type) ?: emptyList()

    @TypeConverter
    fun fromListDasFile(list: List<DasFile>?): String = gson.toJson(list)

    @TypeConverter
    fun toListDasFile(json: String): List<DasFile> =
        gson.fromJson(json, object : TypeToken<List<DasFile>>() {}.type) ?: emptyList()

    @TypeConverter
    fun fromListDasAttribute(list: List<DasAttribute>?): String = gson.toJson(list)

    @TypeConverter
    fun toListDasAttribute(json: String): List<DasAttribute> =
        gson.fromJson(json, object : TypeToken<List<DasAttribute>>() {}.type) ?: emptyList()

    @TypeConverter
    fun fromMapStringString(map: Map<String, String>?): String = gson.toJson(map)

    @TypeConverter
    fun toMapStringString(json: String): Map<String, String> =
        gson.fromJson(json, object : TypeToken<Map<String, String>>() {}.type) ?: emptyMap()
}
