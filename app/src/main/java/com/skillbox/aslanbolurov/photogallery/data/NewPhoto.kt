package com.skillbox.aslanbolurov.photogallery.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

data class NewPhoto(
    @PrimaryKey
    @ColumnInfo(name="id")
    val id:Int?=null,
    @ColumnInfo(name="path")
    val path:String,
    @ColumnInfo(name="date")
    val date: String
) : Serializable {





}