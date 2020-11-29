package com.ajmorales.contactos.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.ByteArrayOutputStream


class ContactoCRUD(context: Context) {

    private var helper: DataBaseHelper? = null
    private var myContext: Context? = null

    init {
        helper = DataBaseHelper(context)
        myContext = context
    }

    fun newContacto(item: Contactos) {
        //Open DB write mode
        val db = helper?.writableDatabase

        val values = ContentValues()

        //Convert bitmap to Blob
        val photoBArray = ByteArrayOutputStream()
        item.foto?.compress(Bitmap.CompressFormat.PNG, 100, photoBArray)
        val mPhotoBlob: ByteArray = photoBArray.toByteArray()

        //map column values
        values.put(ContactosContract.Companion.Input.COLUMN_ID, item.id)
        values.put(ContactosContract.Companion.Input.COLUMN_PHOTO, mPhotoBlob)
        values.put(ContactosContract.Companion.Input.COLUMN_NAME, item.nombre)
        values.put(ContactosContract.Companion.Input.COLUMN_SURNAME, item.apellidos)
        values.put(ContactosContract.Companion.Input.COLUMN_BORN, item.nacimiento)
        values.put(ContactosContract.Companion.Input.COLUMN_TLF1, item.telefono1)
        values.put(ContactosContract.Companion.Input.COLUMN_SPTLF1, item.spinnerTlf1)
        values.put(ContactosContract.Companion.Input.COLUMN_TLF2, item.telefono2)
        values.put(ContactosContract.Companion.Input.COLUMN_SPTLF2, item.spinnerTlf2)
        values.put(ContactosContract.Companion.Input.COLUMN_EMAIL, item.email)
        values.put(ContactosContract.Companion.Input.COLUMN_ADDRESS, item.direccion)
        values.put(ContactosContract.Companion.Input.COLUMN_WEB, item.web)
        values.put(ContactosContract.Companion.Input.COLUMN_SOCIAL, item.social)
        values.put(ContactosContract.Companion.Input.COLUMN_NOTES, item.notas)

        //Insert new row

        Log.d("###Insertado", item.nombre + " - " + item.apellidos)
        //val newRowId = db?.insert(ContactosContract.Companion.Input.TABLE_NAME, null, values)
        db?.insert(ContactosContract.Companion.Input.TABLE_NAME, null, values)
        db?.close()
    }

    fun getContactos(): ArrayList<Contactos> {
        val items = ArrayList<Contactos>()

        val db: SQLiteDatabase = helper?.readableDatabase!!

        val column = arrayOf(
            ContactosContract.Companion.Input.COLUMN_ID,
            ContactosContract.Companion.Input.COLUMN_PHOTO,
            ContactosContract.Companion.Input.COLUMN_NAME,
            ContactosContract.Companion.Input.COLUMN_SURNAME,
            ContactosContract.Companion.Input.COLUMN_BORN,
            ContactosContract.Companion.Input.COLUMN_TLF1,
            ContactosContract.Companion.Input.COLUMN_SPTLF1,
            ContactosContract.Companion.Input.COLUMN_TLF2,
            ContactosContract.Companion.Input.COLUMN_SPTLF2,
            ContactosContract.Companion.Input.COLUMN_EMAIL,
            ContactosContract.Companion.Input.COLUMN_ADDRESS,
            ContactosContract.Companion.Input.COLUMN_WEB,
            ContactosContract.Companion.Input.COLUMN_SOCIAL,
            ContactosContract.Companion.Input.COLUMN_NOTES
        )

        //Cursor to rerun the table
        val cursor = db.query(
            ContactosContract.Companion.Input.TABLE_NAME,
            column,
            null,
            null,
            null,
            null,
            null
        )


        //Do rerun cursor into the table
        while (cursor.moveToNext()) {

            val byteArray: ByteArray =
                cursor.getBlob(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_PHOTO))
            val mPhotoBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            items.add(
                Contactos(
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_ID)),
                    mPhotoBitmap,
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_SURNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_BORN)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_TLF1)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_SPTLF1)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_TLF2)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_SPTLF2)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_ADDRESS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_WEB)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_SOCIAL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_NOTES))
                )
            )
        }

        cursor.close() //Esta la he cerrado
        db.close()

        Log.i("getContactos ", "....retrieve CONTACTOS")
        return items

    }

    fun getContacto(nombre: String): Contactos {
        var item: Contactos? = null

        val db: SQLiteDatabase = helper?.readableDatabase!!

        val columns = arrayOf(
            ContactosContract.Companion.Input.COLUMN_ID,
            ContactosContract.Companion.Input.COLUMN_PHOTO,
            ContactosContract.Companion.Input.COLUMN_NAME,
            ContactosContract.Companion.Input.COLUMN_SURNAME,
            ContactosContract.Companion.Input.COLUMN_BORN,
            ContactosContract.Companion.Input.COLUMN_TLF1,
            ContactosContract.Companion.Input.COLUMN_SPTLF1,
            ContactosContract.Companion.Input.COLUMN_TLF2,
            ContactosContract.Companion.Input.COLUMN_SPTLF2,
            ContactosContract.Companion.Input.COLUMN_EMAIL,
            ContactosContract.Companion.Input.COLUMN_ADDRESS,
            ContactosContract.Companion.Input.COLUMN_WEB,
            ContactosContract.Companion.Input.COLUMN_SOCIAL,
            ContactosContract.Companion.Input.COLUMN_NOTES
        )

        //Cursor to rerun the table
        val cursor = db.query(
            ContactosContract.Companion.Input.TABLE_NAME,
            columns,
            " nombre = ? ",
            arrayOf(nombre),
            null,
            null,
            null
        )

        //Do rerun cursor into the table
        while (cursor.moveToNext()) {

            val byteArray: ByteArray =
                cursor.getBlob(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_PHOTO))
            val mPhotoBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            item = Contactos(
                cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_ID)),
                mPhotoBitmap,
                cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_SURNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_BORN)),
                cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_TLF1)),
                cursor.getInt(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_SPTLF1)),
                cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_TLF2)),
                cursor.getInt(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_SPTLF2)),
                cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_ADDRESS)),
                cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_WEB)),
                cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_SOCIAL)),
                cursor.getString(cursor.getColumnIndexOrThrow(ContactosContract.Companion.Input.COLUMN_NOTES))
            )
        }

        cursor.close()
        db.close() //Esta la he cerrado

        return item!!
    }

    //Ver comoo funciona, puede haber error con las consultas al no haber NIF, ni ID
    fun updateContacto(item: Contactos): Contactos {

        val db: SQLiteDatabase = helper?.writableDatabase!!

        val values = ContentValues()


        //Convert bitmap to Blob
        val photoBArray = ByteArrayOutputStream()
        item.foto?.compress(Bitmap.CompressFormat.PNG, 100, photoBArray)
        val mPhotoBlob: ByteArray = photoBArray.toByteArray()

        //map column values
        values.put(ContactosContract.Companion.Input.COLUMN_ID, item.id)
        values.put(ContactosContract.Companion.Input.COLUMN_PHOTO, mPhotoBlob)
        values.put(ContactosContract.Companion.Input.COLUMN_NAME, item.nombre)
        values.put(ContactosContract.Companion.Input.COLUMN_SURNAME, item.apellidos)
        values.put(ContactosContract.Companion.Input.COLUMN_BORN, item.nacimiento)
        values.put(ContactosContract.Companion.Input.COLUMN_TLF1, item.telefono1)
        values.put(ContactosContract.Companion.Input.COLUMN_SPTLF1, item.spinnerTlf1)
        values.put(ContactosContract.Companion.Input.COLUMN_TLF2, item.telefono2)
        values.put(ContactosContract.Companion.Input.COLUMN_SPTLF2, item.spinnerTlf2)
        values.put(ContactosContract.Companion.Input.COLUMN_EMAIL, item.email)
        values.put(ContactosContract.Companion.Input.COLUMN_ADDRESS, item.direccion)
        values.put(ContactosContract.Companion.Input.COLUMN_WEB, item.web)
        values.put(ContactosContract.Companion.Input.COLUMN_SOCIAL, item.social)
        values.put(ContactosContract.Companion.Input.COLUMN_NOTES, item.notas)

        db.update(ContactosContract.Companion.Input.TABLE_NAME, values, "id = ?", arrayOf(item.id))
        Log.d(
            "Contacto Update",
            "id: " + item.id + " Nombre: " + item.nombre + " Apellidos: " + item.apellidos + " Nacimiento:" + item.nacimiento
        )
        db.close()
        return item
    }

    //Ver comoo funciona, puede haber error con las consultas al no haber NIF, ni ID
    fun deleteContacto(item: Contactos) {

        val db: SQLiteDatabase = helper?.writableDatabase!!

        db.delete(ContactosContract.Companion.Input.TABLE_NAME, "id = ?", arrayOf(item.id))

        db.close()
    }

}