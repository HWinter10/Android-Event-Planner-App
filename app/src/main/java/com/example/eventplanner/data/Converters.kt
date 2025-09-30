/**
 * Type converters for Room database.
 * Converts complex types (List<String>) to storable types and back.
 */
package com.example.eventplanner.data

import androidx.room.TypeConverter

class Converters {
    // joining & splitting lists
    private val delimiter = "__,__"
    // converts stored String to List<String>
    @TypeConverter
    fun fromString(value: String?): List<String> {
        return value?.split(delimiter) ?: emptyList()
    }
    // converts List<String> to storable String
    @TypeConverter
    fun fromList(list: List<String>?): String {
        return list?.joinToString(delimiter) ?: ""
    }
}