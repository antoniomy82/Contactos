package com.ajmorales.contactos.activities

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajmorales.contactos.R
import com.ajmorales.contactos.model.ContactoCRUD
import com.ajmorales.contactos.model.Contactos
import com.ajmorales.contactos.ui.AdaptadorRecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *  Creado por Antonio J Morales on 22/05/2020
 *  Si te interesa, puedes ver como se ha realizado esta App en mi Canal de Youtube: https://www.youtube.com/channel/UC2XTU132H9tHCnM_A3opCzQ
 *  Puedes descargar el código de mi Github : https://github.com/antoniomy82
 */

class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var adapter: AdaptadorRecyclerView? = null
    private var manager: RecyclerView.LayoutManager? = null
    private var switchOn = false //Lo usamos para saber como se encuentra el Switch
    private var tipoVista: String? = null//Usamos como flag para saber si es grid o list
    private var crud: ContactoCRUD? = null
    private var newId: Int? = 0
    private var loadDB: Boolean = false


    companion object {
        private var listaContactos = ArrayList<Contactos>()
        private var listaAuxiliar = ArrayList<Contactos>()
        private var esBusqueda = false //Flag para saber si hemos realizado una búsqueda
        var progressBar: ProgressBar? = null
        var mCRUD: ContactoCRUD? = null
        var tvLoad: TextView? = null

        fun setCRUD(crud: ContactoCRUD) {
            this.mCRUD = crud
        }

        fun getContacto(indice: Int): Contactos {
            return listaContactos[indice]
        }

        fun getImagen(indice: Int): Bitmap {
            return listaContactos[indice].foto!!
        }

        fun setContacto(
            id: String,
            foto: Bitmap,
            nombre: String,
            apellidos: String,
            nacimiento: String,
            telefono1: String,
            spinner_tlf1: Int,
            telefono2: String,
            spinner_tlf2: Int,
            email: String,
            direccion: String,
            web: String,
            social: String,
            notas: String
        ) {
            val item = Contactos(
                id,
                foto,
                nombre,
                apellidos,
                nacimiento,
                telefono1,
                spinner_tlf1,
                telefono2,
                spinner_tlf2,
                email,
                direccion,
                web,
                social,
                notas
            )
            mCRUD?.newContacto(item)

        }

        fun delContacto(posicion: Int, contacto: Contactos) {
            listaAuxiliar = listaContactos
            listaAuxiliar.removeAt(posicion)
            mCRUD?.deleteContacto(contacto)
            listaContactos =
                listaAuxiliar //Lista para borrados y actualizaciones, que no recarguemos la inicial
        }

        fun updateContacto(posicion: Int, nueva: Contactos) {
            mCRUD?.updateContacto(nueva)
            listaContactos[posicion] = nueva
            listaAuxiliar = listaContactos

        }

        fun setEsBusqueda(listaFull: ArrayList<Contactos>?) {
            esBusqueda = true
            if (listaFull != null) {
                listaAuxiliar = listaFull
            }
        }

        fun stopProgressBar() {
            progressBar?.visibility = View.GONE
            tvLoad?.visibility = View.GONE

        }

        fun getListaAuxiliar(): ArrayList<Contactos> {
            return listaAuxiliar
        }

        //Ordenamos la lista de contactos por nombre
        fun ordenarListaContactos(lista: ArrayList<Contactos>?) {
            lista?.sortWith({ c1, c2 -> c1.nombre.compareTo(c2.nombre) })
            if (lista != null) {
                listaContactos = lista
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById<View>(R.id.circularProgress) as ProgressBar
        tvLoad = findViewById(R.id.tvLoading)

        val toolbar: Toolbar?
        toolbar = findViewById(R.id.toolbar_main)

        toolbar.title = "  Contactos"
        toolbar.setLogo(R.drawable.ico_personal)
        setSupportActionBar(toolbar)
        val fbNuevo = findViewById<FloatingActionButton>(R.id.fbnuevo)

        crud = ContactoCRUD(this)
        setCRUD(crud!!)

        listaContactos = ArrayList<Contactos>()
        listaAuxiliar = ArrayList<Contactos>()

        if (!loadDB) {
            listaContactos = crud?.getContactos() ?: ArrayList()
            Log.i("load DB", "....#")
        }

        //Carga BD por defecto
        if (listaContactos.size == 0) {
            cargarListaContactos()

            val runnable = Runnable {
                listaContactos = crud!!.getContactos()
                setUpRecyclerView() //Cargamos datos
            }
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed(runnable, 20000)
        } else {
            setUpRecyclerView() //Cargamos datos
        }

        // setUpRecyclerView()  // Si no quiere cargar BD por defecto

        //Floating button nuevo
        fbNuevo.setOnClickListener {
            listaContactos =
                listaAuxiliar //Lista para borrados y actualizaciones, que no recarguemos la inicial
            Log.i("@@ ID_VALUE", newId.toString())
            this.newId = listaContactos.size
            val intent = Intent(
                applicationContext,
                ContactoNuevo::class.java
            ) //Activity inicio, activity destino
            intent.putExtra("ID", newId ?: 0) //Envío la posición dentro de lista
            startActivity(intent)
        }

        progressBar?.visibility = View.VISIBLE
        tvLoad?.visibility = View.VISIBLE
    }

    //Cuando cambiamos de Portrait a Landscape guardamos los datos
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("OnConfigurationChanged", "NO IF....@")

            loadDB = true
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            loadDB = true
        }

    }

    private fun setUpRecyclerView() {

        //Según esté el switch seleccionamos la vista (grid o list)
        if (!switchOn) {
            manager = LinearLayoutManager(this) //Mostrar como LinearLayout
            tipoVista = "lista"
        } else if (switchOn) {
            manager = GridLayoutManager(this, 3)
            tipoVista = "grid"
        }

        if (listaContactos.size > 0) {
            listaAuxiliar = listaContactos
        }

        if (esBusqueda) { //Caso busquedas
            listaContactos = getListaAuxiliar()
            esBusqueda = false
        }


        Log.i("setupRecyclerView", "onCreate")
        ordenarListaContactos(listaContactos)
        recyclerView =
            findViewById(R.id.rvListItems) //Aquí definimos dónde tenemos la vista del recyclerView XML
        recyclerView?.setHasFixedSize(true) //con tamaño fijo, para que tarde menos el renderizado.
        recyclerView?.layoutManager = manager
        adapter = AdaptadorRecyclerView(
            this,
            listaContactos,
            tipoVista!!
        ) //lista linearLayout o grid para grid Layout
        recyclerView?.adapter = adapter
    }

    //Creamos el menú de opciones
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val miSwitch = menu.findItem(R.id.switchVista) //Switch
        val switchOnOff: SwitchCompat = miSwitch.actionView.findViewById(R.id.switchVista)
        switchOnOff.setThumbResource(R.drawable.ic_list)

        switchOnOff.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("SWITCH ->On", "Esta On")
                switchOn = true
                switchOnOff.setThumbResource(R.drawable.ic_grid_)
                setUpRecyclerView()
            } else {
                Log.d("SWITCH ->Off", "Esta Off")
                switchOn = false
                switchOnOff.setThumbResource(R.drawable.ic_list)
                setUpRecyclerView()
            }
        }

        //Barra de busqueda por nombre
        val searchItem = menu.findItem(R.id.ab_busqueda)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter?.filter?.filter(newText)
                return false
            }
        })
        return true
    }

    //finalizo todas las actividades
    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

    //Actualizo recicleView despues de un añadido , un borrado o busqueda
    override fun onResume() {
        super.onResume()
        progressBar?.visibility = View.VISIBLE
        adapter?.notifyDataSetChanged()
    }


    private fun cargarListaContactos() {
        val listaCarga = ArrayList<Contactos>()
        listaCarga.add(
            Contactos(
                "0",
                BitmapFactory.decodeResource(resources, R.drawable.foto01),
                "Luis",
                "Martin Perez",
                "03/02/1996",
                "65897972",
                0,
                "",
                1,
                "luismartin@hotmail.com",
                "C/Algeciras 21,4A, Madrid",
                "",
                "https://www.youtube.com/",
                "Compañero curro"
            )
        )
        listaCarga.add(
            Contactos(
                "1",
                BitmapFactory.decodeResource(resources, R.drawable.foto06),
                "Maria",
                "Lopez Hoyos",
                "27/02/1992",
                "63957772",
                1,
                "",
                2,
                "marialopez@mixmail.com",
                "C/Aluche 48,8A, Madrid",
                "",
                "https://www.facebook.com/",
                "Amiga Luisa"
            )
        )
        listaCarga.add(
            Contactos(
                "2",
                BitmapFactory.decodeResource(resources, R.drawable.foto02),
                "Chan",
                "Zhen Ramón",
                "15/02/1962",
                "679965332",
                1,
                "",
                0,
                "zhenWuhan@chinamail.com",
                "C/Torrecillas 10,Bajo A, Tres Cantos",
                "",
                "https://www.linkedin.com/feed/",
                "Profesor TechCo"
            )
        )
        listaCarga.add(
            Contactos(
                "3",
                BitmapFactory.decodeResource(resources, R.drawable.foto07),
                "Carmen",
                "Rodriguez Saez",
                "15/02/1984",
                "75098972",
                1,
                "",
                3,
                "rodrigo84@hotmail.com",
                "C/Salamanca 2,5C, Ceuta",
                "",
                "https://www.youtube.com/",
                "Colega carrera"
            )
        )
        listaCarga.add(
            Contactos(
                "4",
                BitmapFactory.decodeResource(resources, R.drawable.foto03),
                "Ismail",
                "Morun Cachi",
                "22/02/1977",
                "688991122",
                1,
                "",
                4,
                "morunojae@gmail.com",
                "C/Principe 13,2A, Ceuta",
                "",
                "https://www.facebook.com/",
                "Barrio principe"
            )
        )
        listaCarga.add(
            Contactos(
                "5",
                BitmapFactory.decodeResource(resources, R.drawable.foto08),
                "Carla",
                "Sanz Saez",
                "13/02/1979",
                "95697972",
                0,
                "",
                1,
                "carlasanz_79@hotmail.com",
                "C/Sivano 41,5C, Madrid",
                "",
                "https://www.linkedin.com/feed/",
                "Compañera curro"
            )
        )
        listaCarga.add(
            Contactos(
                "6",
                BitmapFactory.decodeResource(resources, R.drawable.foto04),
                "Marcos",
                "Bonilla Cruz",
                "15/02/1971",
                "915897972",
                3,
                "",
                1,
                "mbonillacruz@techco.com",
                "C/Amador 11,2A, Alcala de Henares",
                "",
                "https://www.linkedin.com/feed/",
                "Compañero trabajo"
            )
        )
        listaCarga.add(
            Contactos(
                "7",
                BitmapFactory.decodeResource(resources, R.drawable.foto09),
                "Sandra",
                "Cruz Castillo",
                "01/02/1987",
                "665897972",
                1,
                "",
                0,
                "sandrasc_87@hotmail.com",
                "C/Portugalete 11,9D, Madrid",
                "",
                "https://www.youtube.com/",
                "Mindfulness"
            )
        )

        val insertDBscope = CoroutineScope(Dispatchers.IO)
        insertDBscope.launch {

            for (i in 0..7) {
                crud?.newContacto(listaCarga[i])
            }
        }
    }


}