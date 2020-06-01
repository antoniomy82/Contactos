package com.ajmorales.contactos

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.*

/**
 *  Creado por Antonio J Morales "el colega informático" on 22/05/2020
 *  Si te interesa, puedes ver como se ha realizado esta App en mi Canal de Youtube: https://www.youtube.com/channel/UC2XTU132H9tHCnM_A3opCzQ
 *  Puedes descargar el código de mi Github : https://github.com/antoniomy82
 */

class ContactoNuevo : AppCompatActivity() {

    private var toolbar: Toolbar? = null
    private var indice = -1
    private var editable = false
    private var contactoEditado: Contactos? = null

    private var year :Int = 0
    private var month:Int = 0
    private var day :Int = 0//Date picker

    private var edFoto: ImageView? = null
    private var edNombre: EditText? = null
    private var edApellidos: EditText? = null
    private var edTelefono1: EditText? = null
    private var spTelefono1: Spinner? = null
    private var edTelefono2: EditText? = null
    private var spTelefono2: Spinner? = null
    private var edEmail: EditText? = null
    private var edDireccion: EditText? = null
    private var edWeb: EditText? = null
    private var tvFechaNacimiento: TextView? = null
    private var bNacimiento: Button? = null
    private var edNotas: EditText? = null
    private var edSocial: EditText? = null

    //Variables estáticas
    private val IMAGE_TYPE = "image/*"
    private val SELECT_SINGLE_PICTURE = 200
    private val REQUEST_IMAGE_CAPTURE = 100
    private val TELEFONOS_LISTA: Array<String> = arrayOf("casa", "trabajo", "móvil", "móvil trabajo", "móvil 2", "casa 2", "otro")

    private var selectedImagePreview: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacto_nuevo)
        toolbar = findViewById(R.id.toolbar_contacto)
        toolbar!!.title = "Crear contacto"
        setSupportActionBar(toolbar)

        //Activo la flecha Home de vuelta a atrás
        val miBar = supportActionBar
        miBar!!.setDisplayHomeAsUpEnabled(true)

        val calendar = Calendar.getInstance() //Date picker
        year = calendar[Calendar.YEAR]
        month = calendar[Calendar.MONTH]
        day = calendar[Calendar.DAY_OF_MONTH]
        edFoto = findViewById(R.id.ivFoto_Contacto_Nuevo)
        edNombre = findViewById(R.id.edNombre_Contacto)
        edApellidos = findViewById(R.id.edApellidos_Contacto)
        edTelefono1 = findViewById(R.id.edtelefono1)
        edTelefono2 = findViewById(R.id.edtelefono2)
        edEmail = findViewById(R.id.edEmail)
        edDireccion = findViewById(R.id.edDireccion)
        edWeb = findViewById(R.id.edWeb)
        tvFechaNacimiento = findViewById(R.id.tvFechaNacimiento)
        bNacimiento = findViewById(R.id.bNacimiento)
        edNotas = findViewById(R.id.edNotas)
        edSocial = findViewById(R.id.edSocial)

        //Intent desde Contacto Detalle
        editable = intent.getBooleanExtra("Edicion", false)

        //Spinners
        val spAdapter: ArrayAdapter<*> = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, TELEFONOS_LISTA)

        spTelefono1 = findViewById<View>(R.id.sp_telefono1) as Spinner
        spTelefono2 = findViewById(R.id.sp_telefono2)
        spTelefono1!!.adapter = spAdapter
        spTelefono2!!.adapter = spAdapter

        if (editable) {
            indice = intent.getIntExtra("edIndice", 0)
            toolbar!!.title = "Editar contacto"

            //Cargamos el objeto
            contactoEditado = MainActivity.getContacto(intent.getIntExtra("edIndice", 0))
            edFoto!!.setImageBitmap(contactoEditado!!.foto)
            edNombre!!.setText(contactoEditado!!.nombre)
            tvFechaNacimiento!!.text = contactoEditado!!.nacimiento
            bNacimiento!!.text = "Cambiar"
            edApellidos!!.setText(contactoEditado!!.apellidos)
            edTelefono1!!.setText(contactoEditado!!.telefono1)
            spTelefono1!!.setSelection(contactoEditado!!.spinnerTlf1)
            edTelefono2!!.setText(contactoEditado!!.telefono2)
            spTelefono2!!.setSelection(contactoEditado!!.spinnerTlf2)
            edEmail!!.setText(contactoEditado!!.email)
            edDireccion!!.setText(contactoEditado!!.direccion)
            edWeb!!.setText(contactoEditado!!.web)
            edSocial!!.setText(contactoEditado!!.social)
            edNotas!!.setText(contactoEditado!!.notas)
        }

        if (selectedImagePreview != null) {
            val drawable = selectedImagePreview!!.drawable as BitmapDrawable
            val bitmap = drawable.bitmap
            edFoto!!.setImageBitmap(bitmap)
        }
        //Cuando clicamos en la foto
        findViewById<View>(R.id.ivFoto_Contacto_Nuevo).setOnClickListener {
            tomarFotoDialog() //Llamo a mi Alert Dialog personalizado
            selectedImagePreview = findViewById(R.id.ivFoto_Contacto_Nuevo)
        }

        //Cuando obtenemos la dirección GPS
        findViewById<View>(R.id.imGPS).setOnClickListener { gps() }

    } //OnCreate


    //Botón fecha de nacimiento, establecer en su onClick
    fun setDate(view: View?) {
        showDialog(999)
        Toast.makeText(applicationContext, "Calendario", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateDialog(id: Int): Dialog? {
        // TODO Auto-generated method stub
        return if (id == 999) {
            DatePickerDialog(this, myDateListener, year, month, day)
        } else null
    }

    private val myDateListener = OnDateSetListener { arg0, arg1, arg2, arg3 -> // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day

            //Añadimos 0 delante
            val dia: String
            val mesA: String
            val mes: Int
            dia = if (arg3 < 10) {
                "0$arg3"
            } else {
                arg3.toString()
            }
            if (arg2 < 10) {
                mes = arg2 + 1
                mesA = "0$mes"
            } else {
                mes = arg2 + 1
                mesA = mes.toString()
            }
            tvFechaNacimiento!!.text = "$dia/$mes/$arg1"
        }


    //Cuando cambiamos de Portrait a Landscape guardamos los datos
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show()
            if (selectedImagePreview != null) {
                Log.d("ImagePreview: ", "Entra - LandScape")
                val drawable = selectedImagePreview!!.drawable as BitmapDrawable
                val bitmap = drawable.bitmap
                edFoto!!.setImageBitmap(bitmap)
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show()
            if (selectedImagePreview != null) {
                Log.d("ImagePreview: ", "Entra - Portrait")
                val drawable = selectedImagePreview!!.drawable as BitmapDrawable
                val bitmap = drawable.bitmap
                edFoto!!.setImageBitmap(bitmap)
            }
        }
    }


    //Creamos el menú de opciones
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_contacto_nuevo, menu)
        val bGuardar = menu.findItem(R.id.bGuardar)

        //Opción de Menú de toolbar
        bGuardar.setOnMenuItemClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)

            if (editable) {
                val actualizada = Contactos((edFoto!!.drawable as BitmapDrawable).bitmap, edNombre!!.text.toString(), edApellidos!!.text.toString(), tvFechaNacimiento!!.text.toString(),
                    edTelefono1!!.text.toString(), spTelefono1!!.selectedItemPosition, edTelefono2!!.text.toString(), spTelefono2!!.selectedItemPosition, edEmail!!.text.toString(),
                    edDireccion!!.text.toString(), edWeb!!.text.toString(), edSocial!!.text.toString(), edNotas!!.text.toString())

                if(edNombre!!.text.toString()=="" ||edNombre!!.text.toString().isBlank()){
                    dialogNombreVacio()
                }
                else{
                    MainActivity.updateContacto(indice, actualizada)
                    startActivity(intent)
                }

            } else {
                if(edNombre!!.text.toString()=="" ||edNombre!!.text.toString().isBlank()){
                    dialogNombreVacio()
                }
                else {
                    MainActivity.setContacto(
                        (edFoto!!.drawable as BitmapDrawable).bitmap,
                        edNombre!!.text.toString(),
                        edApellidos!!.text.toString(),
                        tvFechaNacimiento!!.text.toString(),
                        edTelefono1!!.text.toString(),
                        spTelefono1!!.selectedItemPosition,
                        edTelefono2!!.text.toString(),
                        spTelefono2!!.selectedItemPosition,
                        edEmail!!.text.toString(),
                        edDireccion!!.text.toString(),
                        edWeb!!.text.toString(),
                        edSocial!!.text.toString(),
                        edNotas!!.text.toString()
                    )
                    startActivity(intent)
                }
            }

            true
        }
        return super.onCreateOptionsMenu(menu)

    }//OnCreateOptionMenu

    //Cargamos imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            val extras = data!!.extras
            val imageBitmap = extras!!["data"] as Bitmap?


            selectedImagePreview!!.setImageBitmap(imageBitmap)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_SINGLE_PICTURE) {
            val selectedImageUri = data!!.data

            try {
                selectedImagePreview!!.setImageBitmap(UserPicture(selectedImageUri!!, contentResolver).bitmap)
               }
            catch (e: IOException) {
                Log.e(MainActivity::class.java.simpleName, "Error al cargar imagen", e)
            }
        }
    }

    //Solicitamos permisos para la cámara
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de cámara autorizado", Toast.LENGTH_LONG).show()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_LONG).show()
            }
        }
    }

    //Alert dialog
    fun tomarFotoDialog() {
        val dialog =
            AlertDialog.Builder(this@ContactoNuevo)
        val items = arrayOfNulls<CharSequence>(2)
        items[0] = "Hacer una foto"
        items[1] = "Elegir foto"
        dialog.setTitle("Cambiar foto")
        dialog.setItems(items) { dialog, which ->
            if (which == 0) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                if (takePictureIntent.resolveActivity(packageManager) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
            if (which == 1) {
                val nuevaImagen = Intent()
                nuevaImagen.type = IMAGE_TYPE
                nuevaImagen.action = Intent.ACTION_GET_CONTENT

                startActivityForResult(Intent.createChooser(nuevaImagen, "Select Picture"), SELECT_SINGLE_PICTURE)
            }
        }

        dialog.setNegativeButton("Cancelar") { dialog, which -> }

        val alert = dialog.create()
        alert.show()
    }


    /**
     * Dialog to Enable GPS
     */
    fun gps() {
        val address: String //Use to paint GeoLocation in edText or Toast
        val gps = GPSTracker(applicationContext, this@ContactoNuevo)

        //Check GPS Permissions
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this@ContactoNuevo, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

        } else { //Tengo permisos
                if (gps.canGetLocation()) { //I have GPS enable
                    val latitude: Double = gps.getLatitude()
                    val longitude: Double = gps.getLongitude()

                    // \n is for new line
                    Toast.makeText(applicationContext, "Ubicación - \nLat: $latitude\nLong: $longitude", Toast.LENGTH_LONG).show()
                    address = gps.getLocationAddress(latitude, longitude)
                    edDireccion!!.setText(address)

                } else { dialogNoGPS() }//GPS deshabilitado o no tengo GPS
            }
    } //onClick


    //GPS desactivado
    private fun dialogNoGPS() {
        val dialog = AlertDialog.Builder(this@ContactoNuevo)
        dialog.setCancelable(false)
        dialog.setTitle("GPS DESACTIVADO")
        dialog.setMessage("¿Desea activar GPS?")
        dialog.setPositiveButton("Aceptar") { dialog, id ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            this@ContactoNuevo.startActivity(intent)
        }
            .setNegativeButton("Cancelar ") { dialog, which -> //Action for "Cancel".
                dialog.cancel()
            }
        val alert = dialog.create()
        alert.show()
    }

    //Nombre vacío
    fun dialogNombreVacio() {
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        dialog.setTitle("NOMBRE VACÍO")
        dialog.setMessage("Debe introducir al menos un nombre")

        dialog.setPositiveButton("Aceptar") { dialog, id -> }
        val alert = dialog.create()
        alert.show()
    }
}
