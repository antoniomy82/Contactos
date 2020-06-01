package com.ajmorales.contactos

import android.graphics.Bitmap

/**
 *  Creado por Antonio J Morales "el colega informático" on 22/05/2020
 *  Si te interesa, puedes ver como se ha realizado esta App en mi Canal de Youtube: https://www.youtube.com/channel/UC2XTU132H9tHCnM_A3opCzQ
 *  Puedes descargar el código de mi Github : https://github.com/antoniomy82
 */

class Contactos (foto: Bitmap, nombre: String, apellidos: String, nacimiento: String, telefono1: String, spinner_tlf1: Int, telefono2: String,spinner_tlf2: Int,
                 email: String, direccion: String, web: String, social: String, notas: String) {

    var foto: Bitmap? = null
    var nombre: String = ""
    var apellidos: String = ""
    var nacimiento: String = ""
    var telefono1: String = ""
    var spinnerTlf1: Int = 0
    var telefono2: String = ""
    var spinnerTlf2: Int = 0
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
        this.spinnerTlf1 = spinner_tlf1
        this.telefono2 = telefono2
        this.spinnerTlf2 = spinner_tlf2
        this.email = email
        this.direccion = direccion
        this.web = web
        this.social = social
        this.notas = notas
    }
}