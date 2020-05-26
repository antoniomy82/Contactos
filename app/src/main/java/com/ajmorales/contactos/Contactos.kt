package com.ajmorales.contactos

import android.graphics.Bitmap

class Contactos (foto: Bitmap, nombre: String, apellidos: String, nacimiento: String, telefono1: String, spinner_tlf1: Int, telefono2: String,spinner_tlf2: Int,
                 email: String, direccion: String, web: String, social: String, notas: String) {

    var foto: Bitmap? = null
    var nombre: String = ""
    var apellidos: String = ""
    var nacimiento: String = ""
    var telefono1: String = ""
    var spinner_tlf1: Int = 0
    var telefono2: String = ""
    var spinner_tlf2: Int = 0
    var email: String = ""
    var direccion: String = ""
    var web: String = ""
    var social: String = ""
    var notas: String = ""

    init {
        this.foto  = foto
        this.nombre = nombre
        this.apellidos = apellidos
        this.nacimiento  = nacimiento
        this.telefono1 = telefono1
        this.spinner_tlf1 = spinner_tlf1
        this.telefono2 = telefono2
        this.spinner_tlf2 = spinner_tlf2
        this.email = email
        this.direccion = direccion
        this.web = web
        this.social = social
        this.notas = notas
    }
}