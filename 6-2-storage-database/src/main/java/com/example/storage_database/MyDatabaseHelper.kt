package com.example.storage_database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class MyDatabaseHelper(
    private val mContext: Context,
    name: String,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(mContext, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_BOOK)
        db.execSQL(CREATE_CATEGORY)
        Toast.makeText(mContext, "Create Succeeded", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists Book")
        db.execSQL("drop table if exists Category")
        onCreate(db)
    }

    companion object {
        const val CREATE_BOOK =
            "create table Book (" +
                    "id integer primary key autoincrement, " +
                    "author text, " +
                    "price real, " +
                    "pages integer, " +
                    "name text)"

        const val CREATE_CATEGORY =
            "create table Category (" +
                    "id integer primary key autoincrement, " +
                    "category_name text, " +
                    "category_code integer)"
    }
}
