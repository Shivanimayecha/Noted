package com.example.noted.RoomDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class NoteCategory : Serializable {
    /*(
    val cat_title: String,
    val cat_image: String
    ) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var cat_id: Int = 0*/
    @ColumnInfo(name = "cat_id")
    @PrimaryKey(autoGenerate = true)
    var cat_id: Int = 0

    @ColumnInfo(name = "cat_title")
    var cat_title: String? = null

    @ColumnInfo(name = "cat_image")
    var cat_image: String? = null
}

