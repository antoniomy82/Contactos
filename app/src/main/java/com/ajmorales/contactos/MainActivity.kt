package com.ajmorales.contactos

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview.AdaptadorRecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var adapter: AdaptadorRecyclerView? = null
    private var manager: RecyclerView.LayoutManager? = null
    private var switchOn = false //Lo usamos para saber como se encuentra el Switch
    private var tipoVista : String? = null//Usamos como flag para saber si es grid o list

    companion object {
        private var listaContactos: ArrayList<Contactos>? = null
        private var listaAuxiliar: ArrayList<Contactos>? = null
        private var esBusqueda = false //Flag para saber si hemos realizado una búsqueda
        var progressBar: ProgressBar? = null

        fun getContacto(indice: Int): Contactos {
            return listaContactos!![indice]
        }

        fun getImagen(indice: Int): Bitmap {
            return listaContactos!![indice].foto!!
        }

        fun setContacto(foto: Bitmap?, nombre: String?, apellidos: String?, nacimiento: String?, telefono1: String?, spinner_tlf1: Int, telefono2: String?, spinner_tlf2: Int, email: String?, direccion: String?, web: String?, social: String?, notas: String?) {
            listaContactos!!.add(Contactos(foto!!, nombre!!, apellidos!!, nacimiento!!, telefono1!!, spinner_tlf1, telefono2!!, spinner_tlf2, email!!, direccion!!, web!!, social!!, notas!!))
            listaContactos = listaAuxiliar //Lista para borrados y actualizaciones, que no recarguemos la inicial
        }

        fun delContacto(posicion: Int) {
            listaAuxiliar = listaContactos
            listaAuxiliar!!.removeAt(posicion)
            listaContactos = listaAuxiliar //Lista para borrados y actualizaciones, que no recarguemos la inicial
        }

        fun updateContacto(posicion: Int, nueva: Contactos) {
            listaContactos!![posicion] = nueva
            listaAuxiliar = listaContactos //Lista para borrados y actualizaciones, que no recarguemos la inicial
        }

        fun setEsBusqueda(listaFull: ArrayList<Contactos>?) {
            esBusqueda = true
            listaAuxiliar = listaFull
        }

        fun stopProgressBar() {
            progressBar!!.visibility = View.GONE
        }

        fun getListaAuxiliar(): ArrayList<Contactos>? {
            return listaAuxiliar
        }

        //Ordenamos la lista de contactos por nombre
        fun ordenarListaContactos(lista: ArrayList<Contactos>?) {
            lista!!.sortWith(Comparator { c1, c2 -> c1.nombre.compareTo(c2.nombre) })
            listaContactos = lista
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById<View>(R.id.circularProgress) as ProgressBar

        var toolbar: Toolbar? = null
        toolbar = findViewById(R.id.toolbar_main)

        toolbar.title = "  Contactos"
        toolbar.setLogo(R.drawable.ico_personal)
        setSupportActionBar(toolbar)
        val fbNuevo = findViewById<FloatingActionButton>(R.id.fbnuevo)

        setUpRecyclerView() //Cargamos datos

        //Floating button nuevo
        fbNuevo.setOnClickListener {
            listaContactos = listaAuxiliar //Lista para borrados y actualizaciones, que no recarguemos la inicial
            val intent = Intent(applicationContext, ContactoNuevo::class.java) //Activity inicio, activity destino
            startActivity(intent)
        }

        progressBar!!.visibility = View.VISIBLE
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

        //Utilizamos 2 ArrayList para no machacar los CRUD realizados
        if (listaContactos == null) { //Caso inicial
            listaContactos = ArrayList<Contactos>() //Inicializo el ArrayList
            listaAuxiliar = ArrayList<Contactos>()
            cargarListaContactos()
            listaAuxiliar = listaContactos
        } else if (esBusqueda) { //Caso busquedas
            listaContactos = getListaAuxiliar()
            esBusqueda = false
        }
        ordenarListaContactos(listaContactos)
        recyclerView = findViewById(R.id.rvListItems) //Aquí definimos dónde tenemos la vista del recyclerView XML
        recyclerView!!.setHasFixedSize(true) //con tamaño fijo, para que tarde menos el renderizado.
        recyclerView!!.layoutManager = manager
        adapter = AdaptadorRecyclerView(this, listaContactos!!, tipoVista!!) //lista linearLayout o grid para grid Layout
        recyclerView!!.adapter = adapter
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
                adapter!!.filter.filter(newText)
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

    //Actualizo recicleView despues de un añadido o un borrado.
    override fun onResume() {
        super.onResume()
        //progressBar.setVisibility(View.VISIBLE);
        adapter!!.notifyDataSetChanged()
    }

    private fun cargarListaContactos() {
        val listaCarga: ArrayList<Contactos> = ArrayList<Contactos>()
        listaCarga.add(
            Contactos(
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
        listaCarga.add(
            Contactos(
                BitmapFactory.decodeResource(resources, R.drawable.foto05),
                "Antonio J",
                "Morales Yáñez",
                "11/01/1982",
                "65997972",
                1,
                "",
                4,
                "elcolegainformatico_82@gmail.com",
                "Ctra Canillas 21,4A, Madrid",
                "",
                "https://www.youtube.com/channel/UC2XTU132H9tHCnM_A3opCzQ",
                "Yo mismo"
            )
        )

        listaContactos = listaCarga
    }
}