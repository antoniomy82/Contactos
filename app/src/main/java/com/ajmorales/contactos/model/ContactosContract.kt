package com.ajmorales.contactos.model

import android.provider.BaseColumns

class ContactosContract {

    companion object {

        val VERSION = 1

        //Mapeamos las clases con las columnas (ORM)
        class Input : BaseColumns {
            companion object {
                val TABLE_NAME = "contactos"
                val COLUMN_PHOTO = "foto"
                val COLUMN_ID = "id"
                val COLUMN_NAME = "nombre"
                val COLUMN_SURNAME = "apellidos"
                val COLUMN_BORN = "nacimiento"
                val COLUMN_TLF1 = "telefono1"
                val COLUMN_SPTLF1 = "spinnerTlf1"
                val COLUMN_TLF2 = "telefono2"
                val COLUMN_SPTLF2 = "spinnerTlf2"
                val COLUMN_EMAIL = "email"
                val COLUMN_ADDRESS = "direccion"
                val COLUMN_WEB = "web"
                val COLUMN_SOCIAL = "social"
                val COLUMN_NOTES = "notas"
            }

        }
    }
}