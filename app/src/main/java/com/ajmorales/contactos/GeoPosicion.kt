package com.ajmorales.contactos

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.io.IOException

/**
 *  Creado por Antonio J Morales "el colega informático" on 22/05/2020
 *  Si te interesa, puedes ver como se ha realizado esta App en mi Canal de Youtube: https://www.youtube.com/channel/UC2XTU132H9tHCnM_A3opCzQ
 *  Puedes descargar el código de mi Github : https://github.com/antoniomy82
 */

class GeoPosicion {

    fun getLocationFromAddress(strAddress: String?, miContexto: Context?): String? {
        val coder = Geocoder(miContexto)
        val address: List<Address>?

        try {
            address = coder.getFromLocationName(strAddress, 5)
            return if (address == null) {
                null
            } else {
                val location = address[0]
                location.latitude
                location.longitude
                location.latitude.toString() + "," + location.longitude.toString()
            }
        } catch (error: Error) {
            Log.e("Error", error.toString())
        } catch (error: IOException) {
            Log.e("Error", error.toString())
        }
        return null
    }
}