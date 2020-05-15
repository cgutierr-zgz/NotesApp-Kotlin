package es.carlosgutimo.mynotes

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder

class DbManager(context: Context) {
    val dbName = "MyNotes"
    val dbTable = "Notes"
    private val colID = "ID"
    private val colTitle = "Title"
    private val colDesc = "Description"
    val dbVersion = 1 //TODO: Change version on app update
    val sqlCreateTable =
        "CREATE TABLE IF NOT EXISTS $dbTable ($colID INTEGER PRIMARY KEY,$colTitle TEXT, $colDesc TEXT);"
    private var sqlDB: SQLiteDatabase? = null

    init {
        val db = DatabaseHelperNotes(context)
        sqlDB = db.writableDatabase
    }

    inner class DatabaseHelperNotes(context: Context) :
        SQLiteOpenHelper(context, dbName, null, dbVersion) {
        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(sqlCreateTable) //Database is created
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("Drop table IF EXISTS $dbTable")
        }

    }

    fun insert(values: ContentValues): Long {
        return sqlDB!!.insert(dbTable, "", values)
    }

    fun query(
        projection: Array<String>,
        selection: String,
        selectionArgs: Array<String>,
        sortOrder: String
    ): Cursor {
        val qb = SQLiteQueryBuilder()
        qb.tables = dbTable
        return qb.query(sqlDB, projection, selection, selectionArgs, null, null, sortOrder)
    }

    fun delete(selection: String, selectionArgs: Array<String>): Int {
        return sqlDB!!.delete(dbTable, selection, selectionArgs)
    }

    fun update(values: ContentValues, selection: String, selectionArgs: Array<String>): Int {
        return sqlDB!!.update(dbTable, values, selection, selectionArgs)
    }
}