package com.example.coopt3.sealed

import com.example.coopt3.models.Music

sealed class DataState {
    class Success(val data:MutableList<Music>): DataState()
    class Failure(val message:String): DataState()
    object Loading : DataState()
    object Empty : DataState()
}