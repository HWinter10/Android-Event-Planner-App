/**
 * Generic wrapper to represent async data states.
 * Types: Loading, Success<T>, Error.
 * Used by ViewModel and UI to handle loading, success, and error states.
 */

package com.example.eventplanner.util

sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String) : Resource<T>()
}