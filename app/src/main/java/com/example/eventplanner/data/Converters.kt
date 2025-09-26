/**
 * Type converters for Room database.
 * Converts complex types (List<String>) to storable types and back.
 */

package com.example.eventplanner.data

import androidx.room.TypeConverter

class Converters {

    private val delimiter = "__,__" // Unlikely sequence to separate list items

    @TypeConverter
    fun fromString(value: String?): List<String> {
        return value?.split(delimiter) ?: emptyList()
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return list?.joinToString(delimiter) ?: ""
    }
}