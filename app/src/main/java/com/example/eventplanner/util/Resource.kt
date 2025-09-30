/**
 * Generic wrapper to represent async data states.
 * Types: Loading, Success<T>, Error.
 * Used by ViewModel and UI to handle loading, success, and error states.
 */
package com.example.eventplanner.util

sealed class Resource<T> {
    class Loading<T> : Resource<T>() // state - loading
    data class Success<T>(val data: T) : Resource<T>() // state - success with data
    data class Error<T>(val message: String) : Resource<T>() // state - error with message
}