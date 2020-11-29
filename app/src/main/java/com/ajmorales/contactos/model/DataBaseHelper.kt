package com.ajmorales.contactos.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    ContactosContract.Companion.Input.TABLE_NAME,
    null,
    ContactosContract.VERSION
) {

    companion object {
        val CREATE_CONTACTOS_TABLE =
            "CREATE TABLE " + ContactosContract.Companion.Input.TABLE_NAME +
                    " (" + ContactosContract.Companion.Input.COLUMN_ID + " INTEGER, " +
                    ContactosContract.Companion.Input.COLUMN_PHOTO + " BLOB, " +
                    ContactosContract.Companion.Input.COLUMN_NAME + " TEXT, " +
                    ContactosContract.Companion.Input.COLUMN_SURNAME + " TEXT, " +
                    ContactosContract.Companion.Input.COLUMN_BORN + " TEXT, " +
                    ContactosContract.Companion.Input.COLUMN_TLF1 + " TEXT, " +
                    ContactosContract.Companion.Input.COLUMN_SPTLF1 + " INTEGER, " +
                    ContactosContract.Companion.Input.COLUMN_TLF2 + " TEXT, " +
                    ContactosContract.Companion.Input.COLUMN_SPTLF2 + " INTEGER, " +
                    ContactosContract.Companion.Input.COLUMN_EMAIL + " TEXT, " +
                    ContactosContract.Companion.Input.COLUMN_ADDRESS + " TEXT, " +
                    ContactosContract.Companion.Input.COLUMN_WEB + " TEXT, " +
                    ContactosContract.Companion.Input.COLUMN_SOCIAL + " TEXT, " +
                    ContactosContract.Companion.Input.COLUMN_NOTES + " TEXT )"

        val REMOVE_CONTACTOS_TABLE =
            "DROP TABLE IF EXISTS " + ContactosContract.Companion.Input.TABLE_NAME

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_CONTACTOS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(REMOVE_CONTACTOS_TABLE)
        onCreate(db)
    }
}