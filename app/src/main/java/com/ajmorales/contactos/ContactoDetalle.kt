package com.ajmorales.contactos

import android.app.AlertDialog
import android.content.ContentUris
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.util.*

class ContactoDetalle : AppCompatActivity() {

    //Variables estilo camelCase
    private var ivfotoContacto: ImageView? = null
    private var miFoto: Bitmap? = null
    private var tvnombreContacto: TextView? = null
    private var tvnacimientoContacto: TextView? = null
    private var tvEdad: TextView? = null
    private var tvTelefono1: TextView? = null
    private var tvtelefono1Sp: TextView? = null
    private var tvTelefono2: TextView? = null
    private var tvtelefono2Sp: TextView? = null
    private var tvEmail: TextView? = null
    private var tvDireccion: TextView? = null
    private var tvWeb: TextView? = null
    private var tvSocial: TextView? = null
    private var tvNotas: TextView? = null

    private var misContactos: Contactos? = null
    private var miToolbar: Toolbar? = null

    private var posicion = 0
    private var diaN :Int = 0 //Variables para cálculo de fecha
    private var mesN:Int = 0
    private var anioN:Int = 0

    //UPPER_SNAKE_CASE (Constantes)
    private val TELEFONOS_LISTA = arrayOf("casa", "trabajo", "móvil", "móvil trabajo", "móvil 2", "casa 2", "otro") //Spinner
    private val TEST_VACIO: String = "" //util para comprobar si están vacíos los campos


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacto_detalle)

        //Defino toolbar
        miToolbar = findViewById(R.id.toolbar_contacto)
        miToolbar!!.title = "Detalle contacto"
        setSupportActionBar(miToolbar)

        //Activo la flecha Home de vuelta a atrás
        val miBar = supportActionBar
        miBar!!.setDisplayHomeAsUpEnabled(true)

        //Enlazo las variables con sus etiquetas de su vista o XML
        ivfotoContacto = findViewById(R.id.ivFoto_contacto)
        tvnombreContacto = findViewById(R.id.tvNombre_contacto)
        tvTelefono1 = findViewById(R.id.tvTelefono1)
        tvtelefono1Sp = findViewById(R.id.tvTelefono1_sp)
        tvTelefono2 = findViewById(R.id.tvTelefono2)
        tvtelefono2Sp = findViewById(R.id.tvTelefono2_sp)
        tvnacimientoContacto = findViewById(R.id.tvNacimiento_Contacto)
        tvEdad = findViewById(R.id.tvEdad)
        tvEmail = findViewById(R.id.tvEmail)
        tvDireccion = findViewById(R.id.tvDireccion)
        tvWeb = findViewById(R.id.tvWeb)
        tvSocial = findViewById(R.id.tvSocial)
        tvNotas = findViewById(R.id.tvNotas)
        misContactos = MainActivity.getContacto(intent.getIntExtra("miIndice", 0))
        posicion = intent.getIntExtra("miIndice", 0)
        miFoto = misContactos!!.foto
        ivfotoContacto!!.setImageBitmap(misContactos!!.foto)

        //etiquetas de texto con campos concatenados
        val textNombreContacto =misContactos!!.nombre.toString() + " " + misContactos!!.apellidos
        val textTelefono1Sp= "Teléfono " + TELEFONOS_LISTA[misContactos!!.spinnerTlf1] + ":"
        val textTelefono2Sp="Teléfono " + TELEFONOS_LISTA[misContactos!!.spinnerTlf2] + ":"

        tvnombreContacto!!.text = textNombreContacto
        tvnacimientoContacto!!.text = misContactos!!.nacimiento
        tvTelefono1!!.text = misContactos!!.telefono1
        tvtelefono1Sp!!.text = textTelefono1Sp
        tvTelefono2!!.text = misContactos!!.telefono2
        tvtelefono2Sp!!.text = textTelefono2Sp
        tvEmail!!.text = misContactos!!.email
        tvDireccion!!.text = misContactos!!.direccion
        tvWeb!!.text = misContactos!!.web
        tvSocial!!.text = misContactos!!.social
        tvNotas!!.text = misContactos!!.notas

        calculoCargaEdad()
        cargaClickListenerOncreate()
    }


    //Todos los setOnClickListener de OnCreate
    fun cargaClickListenerOncreate() {

        //Ampliar la imagen al hacer click
        findViewById<View>(R.id.ivFoto_contacto).setOnClickListener {
            val intent = Intent(applicationContext, DetalleFotoActivity::class.java) //Activity inicio, activity destino
            intent.putExtra("miIndice2", posicion)
            startActivity(intent)
        }


        //Click sobre calendario, nos abrirá en que fecha cae el próximo año
        findViewById<View>(R.id.imCumple).setOnClickListener {
            val mesActual = Calendar.getInstance()[Calendar.MONTH]
            var anioActual = Calendar.getInstance()[Calendar.YEAR]

            Log.d("Fecha Split raro: ", "$diaN $mesN $anioN")

            if (mesActual >= mesN) {
                anioActual++
            }
            val beginTime = Calendar.getInstance()
            beginTime[anioActual, mesN - 1] = diaN

            val startMillis = beginTime.timeInMillis //Convertimos la fecha a milisegundos
            val builder = CalendarContract.CONTENT_URI.buildUpon()
            builder.appendPath("time")
            ContentUris.appendId(builder, startMillis)
            val intent = Intent(Intent.ACTION_VIEW).setData(builder.build())
            startActivity(intent)
        }

        //Cuando hagamos click sobre el icono de web, se nos abrirá el enlace almacenado
        findViewById<View>(R.id.imWeb).setOnClickListener { //Si el contenido de Web no es vacio (es decir != "")
            if (TEST_VACIO != misContactos!!.web) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(misContactos!!.web))
                )
            } else {
                Toast.makeText(applicationContext, "No hay web o está mal introducida", Toast.LENGTH_LONG).show()
            }
        }

        //Abrir la ubicación de una dirección en maps
        findViewById<View>(R.id.imLocation).setOnClickListener { gpsDialog() }


        //Cuando hagamos click sobre el icono del telefono, llamaremos
        findViewById<View>(R.id.imTelefono).setOnClickListener {
            llamadaDialog() //Alert Dialog para seleccionar el teléfono
        }

        //Cuando se haga click la imagen de mail, abrirá un cliente de correo  con la dirección que tenemos guardada
        // Nota: (declarar intent filter en Manifest), si no tenemos dirección de mail, la dejará en blanco.
        val miEmail = arrayOf<String>(misContactos!!.email) //Tengo que transformarla a final String[]

        findViewById<View>(R.id.imEmail).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:") // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, miEmail)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Te escribo desde mi agenda")

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

        //Cuando hagamos click sobre el icono de web, se nos abrirá el enlace almacenado
        findViewById<View>(R.id.imSocial).setOnClickListener { //Si el contenido de Web no es vacio (es decir != "")
            if (TEST_VACIO != misContactos!!.social) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(misContactos!!.social)))
            } else {
                Toast.makeText(applicationContext, "No hay web o está mal introducida", Toast.LENGTH_LONG).show()
            }
        }
    }


    //Creamos el menú de opciones
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_contacto_detalle, menu)
        val bBorrar = menu.findItem(R.id.bBorrar)
        val bEditar = menu.findItem(R.id.bEditar)

        //Botón borrar
        bBorrar.setOnMenuItemClickListener {
            alertaBorrar()
            true
        }

        //Botón editar
        bEditar.setOnMenuItemClickListener {
            val intent = Intent(applicationContext, ContactoNuevo::class.java) //Activity inicio, activity destino
            intent.putExtra("edIndice", posicion)
            intent.putExtra("Edicion", true)
            startActivity(intent)

            true
        }
        return super.onCreateOptionsMenu(menu)
    }

    //Alert dialog borrar
    fun alertaBorrar() {
        val dialog = AlertDialog.Builder(this@ContactoDetalle)
        dialog.setCancelable(false)
        dialog.setTitle("Atención")
        dialog.setMessage("¿Está seguro que desea borrar este contacto?")
        dialog.setPositiveButton("Aceptar") { dialog, id ->
            MainActivity.delContacto(posicion)
            finish()
        }
        dialog.setNegativeButton("Cancelar") { dialog, which -> }
            val alert = dialog.create()
            alert.show()
    }

    //Alert dialog seleccionar línea a llamar
    fun llamadaDialog() {
        val dialog = AlertDialog.Builder(this@ContactoDetalle)
        val items = arrayOfNulls<CharSequence>(2)

        items[0] = "Llamar a " + TELEFONOS_LISTA[misContactos!!.spinnerTlf1] + ": " + misContactos!!.telefono1
        items[1] = "Llamar a " + TELEFONOS_LISTA[misContactos!!.spinnerTlf2] + ": " + misContactos!!.telefono2

        dialog.setTitle("Seleccione línea de teléfono")
        dialog.setItems(items) { dialog, which ->
            if (which == 0) {
                if (TEST_VACIO != misContactos!!.telefono1) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:" + misContactos!!.telefono1)

                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(applicationContext, "No hay teléfono en ese campo o su formato es erroneo", Toast.LENGTH_LONG).show()
                }
            }
            if (which == 1) {
                if (TEST_VACIO != misContactos!!.telefono2) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:" + misContactos!!.telefono2)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(applicationContext, "No hay teléfono en ese campo o su formato es erroneo", Toast.LENGTH_LONG).show()
                }
            }
        }
        dialog.setNegativeButton(
            "Cancelar"
        ) { dialog, which -> }
        val alert = dialog.create()
        alert.show()
    }

    fun calculoCargaEdad() {
        diaN = Integer.valueOf(java.lang.String.valueOf(misContactos!!.nacimiento[0] - '0') + java.lang.String.valueOf(misContactos!!.nacimiento[1] - '0'))
        mesN = Integer.valueOf(java.lang.String.valueOf(misContactos!!.nacimiento[3] - '0') + java.lang.String.valueOf(misContactos!!.nacimiento[4] - '0'))
        anioN = Integer.valueOf(java.lang.String.valueOf(misContactos!!.nacimiento[6] - '0') + java.lang.String.valueOf(misContactos!!.nacimiento[7] - '0') + java.lang.String.valueOf(misContactos!!.nacimiento[8] - '0') + java.lang.String.valueOf(misContactos!!.nacimiento[9] - '0'))

        val miCumple = Calendar.getInstance()
        val hoy = Calendar.getInstance()
        miCumple[anioN, mesN] = diaN
        var edad = hoy[Calendar.YEAR] - miCumple[Calendar.YEAR]

        if (hoy[Calendar.DAY_OF_YEAR] < miCumple[Calendar.DAY_OF_YEAR]) {
            edad--
        }
        if (edad > 0) {
            tvEdad!!.text = "$edad años"
        }
    }

    //Alert dialog seleccionar línea a llamar
    fun gpsDialog() {
        val dialog = AlertDialog.Builder(this@ContactoDetalle)
        val items = arrayOfNulls<CharSequence>(2)
        items[0] = "Explorar la dirección"
        items[1] = "Ver solo su ubicación geográfica"
        dialog.setTitle("Como desea mostrar Maps")
        dialog.setItems(items) { dialog, which ->
            if (which == 0) {
                if (TEST_VACIO != misContactos?.direccion) {
                    val miPosicion = GeoPosicion()
                    val miGeoPosicion: String? = miPosicion.getLocationFromAddress(misContactos?.direccion, applicationContext)
                    Log.d("GEO: ", miGeoPosicion)

                    val intentUri = Uri.parse("geo:$miGeoPosicion")
                    val intent = Intent(Intent.ACTION_VIEW, intentUri)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "No hay dirección o esta en formato incorrecto", Toast.LENGTH_LONG).show()
                }
            }
            if (which == 1) {
                if (TEST_VACIO != misContactos?.direccion) {
                    val direccion = "Ubicación de " + misContactos?.nombre
                    val miPosicion = GeoPosicion()
                    val miGeoPosicion: String? = miPosicion.getLocationFromAddress(misContactos?.nombre, applicationContext)
                    Log.d("GEO: ", miGeoPosicion)

                    val intentUri = Uri.parse("geo:$miGeoPosicion?z=16&q=$miGeoPosicion(<$direccion>)")
                    val intent = Intent(Intent.ACTION_VIEW, intentUri)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        applicationContext, "No hay dirección o esta en formato incorrecto", Toast.LENGTH_LONG).show()
                }
            }
        }
        dialog.setNegativeButton(
            "Cancelar"
        ) { dialog, which -> }
        val alert = dialog.create()
        alert.show()
    }

}
