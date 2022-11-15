package com.skillbox.aslanbolurov.photogallery.data

import androidx.room.*


@Dao
interface PhotoDao {


    @Query("SELECT * FROM photo")
    fun getAll():List<Photo>

    @Insert(entity=Photo::class)
    suspend fun insert(photo:NewPhoto)

    @Update
    suspend fun update(photo:Photo)

    @Delete
    suspend fun delete(photo:Photo)



}