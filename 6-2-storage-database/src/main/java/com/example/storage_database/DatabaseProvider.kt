package com.example.storage_database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class DatabaseProvider : ContentProvider() {

    private lateinit var dbHelper: MyDatabaseHelper

    override fun onCreate(): Boolean {
        dbHelper = MyDatabaseHelper(context!!, "BookStore.db", null, 2)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        // Query data from provider
        val db = dbHelper.readableDatabase
        return when (uriMatcher.match(uri)) {
            BOOK_DIR ->
                db.query("Book", projection, selection, selectionArgs, null, null, sortOrder)
            BOOK_ITEM -> {
                val bookId = uri.pathSegments[1]
                db.query("Book", projection, "id = ?", arrayOf(bookId), null, null, sortOrder)
            }
            CATEGORY_DIR ->
                db.query("Category", projection, selection, selectionArgs, null, null, sortOrder)
            CATEGORY_ITEM -> {
                val categoryId = uri.pathSegments[1]
                db.query("Category", projection, "id = ?", arrayOf(categoryId), null, null, sortOrder)
            }
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            BOOK_DIR -> "vnd.android.cursor.dir/vnd.com.example.storage_database. provider.book"
            BOOK_ITEM -> "vnd.android.cursor.item/vnd.com.example.storage_database. provider.book"
            CATEGORY_DIR -> "vnd.android.cursor.dir/vnd.com.example.storage_database. provider.category"
            CATEGORY_ITEM -> "vnd.android.cursor.item/vnd.com.example.storage_database. provider.category"
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // Insert data from provider
        val db = dbHelper.writableDatabase
        return when (uriMatcher.match(uri)) {
            BOOK_DIR, BOOK_ITEM -> {
                val newBookId = db.insert("Book", null, values)
                Uri.parse("content://$AUTHORITY/book/$newBookId")
            }
            CATEGORY_DIR, CATEGORY_ITEM -> {
                val newCategoryId = db.insert("Category", null, values)
                Uri.parse("content://$AUTHORITY/category/$newCategoryId")
            }
            else -> null
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        // Delete data from provider
        val db = dbHelper.writableDatabase
        return when (uriMatcher.match(uri)) {
            BOOK_DIR -> db.delete("Book", selection, selectionArgs)
            BOOK_ITEM -> {
                val bookId = uri.pathSegments[1]
                db.delete("Book", "id = ?", arrayOf(bookId))
            }
            CATEGORY_DIR -> db.delete("Category", selection, selectionArgs)
            CATEGORY_ITEM -> {
                val categoryId = uri.pathSegments[1]
                db.delete("Category", "id = ?", arrayOf(categoryId))
            }
            else -> 0
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        // Update data from provider
        val db = dbHelper.writableDatabase
        return when (uriMatcher.match(uri)) {
            BOOK_DIR -> db.update("Book", values, selection, selectionArgs)
            BOOK_ITEM -> {
                val bookId = uri.pathSegments[1]
                db.update("Book", values, "id = ?", arrayOf(bookId))
            }
            CATEGORY_DIR -> db.update("Category", values, selection, selectionArgs)
            CATEGORY_ITEM -> {
                val categoryId = uri.pathSegments[1]
                db.update("Category", values, "id = ?", arrayOf(categoryId))
            }
            else -> 0
        }
    }

    companion object {
        const val BOOK_DIR = 0
        const val BOOK_ITEM = 1
        const val CATEGORY_DIR = 2
        const val CATEGORY_ITEM = 3

        const val AUTHORITY = "com.example.storage_database.provider"

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "book", BOOK_DIR)
            addURI(AUTHORITY, "book/#", BOOK_ITEM)
            addURI(AUTHORITY, "category", CATEGORY_DIR)
            addURI(AUTHORITY, "category/#", CATEGORY_ITEM)
        }
    }
}
