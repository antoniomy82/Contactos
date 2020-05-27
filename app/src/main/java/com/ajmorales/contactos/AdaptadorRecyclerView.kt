package com.example.recyclerview

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ajmorales.contactos.ContactoDetalle
import com.ajmorales.contactos.Contactos
import com.ajmorales.contactos.MainActivity
import com.ajmorales.contactos.R
import java.util.*

class AdaptadorRecyclerView(var context: Context, listaItems: ArrayList<Contactos>, tipoVista: String) : RecyclerView.Adapter<AdaptadorRecyclerView.ViewHolder>(), Filterable {

    var listaFull: ArrayList<Contactos>?=null
    var listaCopia: ArrayList<Contactos>?=null
    var tipoVista: String

    //Constructor por parámetros
    init {
        this.context=context
        this.listaCopia=listaItems
        listaFull = ArrayList<Contactos>(listaItems)
        this.tipoVista = tipoVista
    }
    
    //Aquí es dónde vamos a crear o inflar la vista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var miContentView: View? = null

        if (tipoVista == "lista") {
            miContentView = LayoutInflater.from(context).inflate(R.layout.recyclerview_item_lista, null) //Aquí cambiamos el tipo de vista item_grid o Item_lista
        }
        else if (tipoVista == "grid") {
            miContentView = LayoutInflater.from(context).inflate(R.layout.recyclerview_item_grid, null) //Aquí cambiamos el tipo de vista item_grid o Item_lista
            Log.d("VISTA", "GRID LAYOUT")
        }
        println("Create View Holder: $viewType")

        //Barra de inicio
        return ViewHolder(miContentView!!) //Devolvemos el ViewHolder que hemos creado debajo, con la View que acabamos de inflar
    }

    //Asignamos los datos a cada elemento de la lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contactos = listaCopia!!.get(position) // Cargamos los elementos
        val miViewHolder = holder as ViewHolder?
        miViewHolder!!.ivFoto.setImageBitmap(contactos.foto)

        //Comprobamos si la vista es grid o lista
        if (tipoVista == "lista") {
            miViewHolder.tvItemNombre.setText(contactos.nombre.toString() + " " + contactos.apellidos)
            miViewHolder.tvItemInicial.setText(contactos.nombre.get(0).toString()) //obtenemos la inicial

        } else {
            miViewHolder.tvItemNombre.setText(contactos.nombre)
            miViewHolder.tvItemInicial.setText(contactos.nombre.get(0).toString()) //obtenemos la inicial

            //Pongo las celdas pares de otro color
            if (position % 2 == 0) {
                miViewHolder.itemView.setBackgroundColor(Color.parseColor("#eeeeee")) //android:background="#FFFFFF"
            } else {
                miViewHolder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF")) //"#e6e2d3"
            }
        }
        println("Bind View Holder: $position")


        //Listener cuando clicamos en un item.
        miViewHolder.itemView.setOnClickListener {
            val intent = Intent(context, ContactoDetalle::class.java) //Activity inicio, activity destino
            intent.putExtra("miIndice", position) //Envío la posición dentro de lista
            context.startActivity(intent)
        }
        MainActivity.stopProgressBar()
    }

    override fun getItemCount(): Int {
        return listaCopia!!.size!!
    }


    //Definimos nuestro holder, en base a los campos que tenemos en nuestros "item"
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivFoto: ImageView
        var tvItemNombre: TextView
        var tvItemInicial : TextView//ListView

        init {
            ivFoto = itemView.findViewById<View>(R.id.ivFoto_contacto) as ImageView
            tvItemNombre = itemView.findViewById(R.id.tvItemNombre)
            tvItemInicial = itemView.findViewById(R.id.tvInicial) //ListView
        }
    }

    //Barra de búsqueda
    override fun getFilter(): Filter {
        return filtroBusqueda
    }

    //Resultados de busqueda
    private val filtroBusqueda: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<Contactos> =
                ArrayList<Contactos>()
            if (constraint == null || constraint.length == 0) {
                listaFull?.let { filteredList.addAll(it) }
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }

                Log.d("ListaItems", listaFull?.size.toString())

                for (item in listaFull!!) {
                    if (item.nombre.toLowerCase().contains(filterPattern)) {
                        Log.d("Item->", item.nombre)
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            MainActivity.setEsBusqueda(listaFull) //Activamos un booleano para saber que tipo de carga hay que hacer y hacemos una copia de la lista de contactos
            listaCopia?.clear()
            listaCopia?.addAll((results.values as ArrayList<Contactos>))
            notifyDataSetChanged()
        }
    }
}