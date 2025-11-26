package com.example.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.data.local.FavoriteCity
import com.example.wheatherapp_full_stack.R
import com.example.wheatherapp_full_stack.databinding.ItemFavoriteCityBinding

//Adapter sencillo para mostrar cada ciudad favorita en una fila
class FavoriteCitiesAdapter(
    //Lista de ciudades favoritas (la declaramos vacía)
    private var items: List<FavoriteCity> = emptyList(),
    //Callback (función que pasa como argumento a otra función)
    //que recibe el nombre de la ciudad al pulsar el icono de la papelera
    private val onDeleteClicked: (FavoriteCity) -> Unit,
    //Callback que recibe la ciudad al pulsar la fila completa
    private val onItemClicked: (FavoriteCity) -> Unit
    //Esta clase extiende de RecyclerView.Adapter quien
    //se encarga de mostrar cada ciudad favorita en la lista
) : RecyclerView.Adapter<FavoriteCitiesAdapter.FavCityViewHolder>() {

    //Inner class para poder buscar las vistas en el layout
    inner class FavCityViewHolder(val binding: ItemFavoriteCityBinding) :
        RecyclerView.ViewHolder(binding.root)

    //Este método solo se ejecuta cuando se va a añadir una nueva fila
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavCityViewHolder {
        //Construimos la fila
        val binding = ItemFavoriteCityBinding.inflate(
            LayoutInflater.from(parent.context),
            //Contenedor donde irá la fila
            parent,
            //Que no añada automáticamente la fila al contenedor
            false
        )
        //Te devuelve el contenedor de la fila
        return FavCityViewHolder(binding)
    }

    //Función para rellenar la fila con los datos correspondientes
    override fun onBindViewHolder(holder: FavCityViewHolder, position: Int) {
        //Buscamos la ciudad que va en la fila
        val city = items[position]

        //Mostramos nombre de ciudad
        holder.binding.textCityName.text = city.name

        //Mostramos lat/lon como subtítulo
        holder.binding.textCitySubtitle.text = "${city.lat}, ${city.lon}"

        //Mostramos el icono de ubicación en el layout
        holder.binding.imageUbiItem.setImageResource(R.drawable.ubicacion)

        //Mostamos el icono de la papelera en el layout
        holder.binding.imageDelete.setImageResource(R.drawable.papelera)

        //Al pulsar el icono de la papelera
        holder.binding.imageDelete.setOnClickListener {
            //Eliminamos la ciudad seleccionada
            onDeleteClicked(city)
        }

        //Al pulsar la fila completa
        holder.itemView.setOnClickListener {
            //Abrimos pantalla con los datos meteorológicos de la ciudad
            onItemClicked(city)
        }
    }

    //Función que dice cuantas filas se deben mostrar
    override fun getItemCount(): Int = items.size

    //Función que actualiza la lista cada vez que cambian los datos
    fun submitList(newItems: List<FavoriteCity>) {
        items = newItems
        notifyDataSetChanged()
    }
}