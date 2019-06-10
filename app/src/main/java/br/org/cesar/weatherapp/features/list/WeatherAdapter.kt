package br.org.cesar.weatherapp.features.list

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.org.cesar.weatherapp.R
import br.org.cesar.weatherapp.entity.City
import br.org.cesar.weatherapp.utils.Util
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row_city_layout.view.*
import kotlin.math.roundToInt

/**
 *
 * Classe que cria um Adapter para o nosso RecyclerView
 * @sample https://medium.com/android-dev-br/listas-com-recyclerview-d3f41e0d653c
 *
 */
class WeatherAdapter(private val saveFavoriteCallback: (City) -> Unit) : RecyclerView.Adapter<WeatherAdapter.MyViewHolder>() {

    private var list: List<City>? = null

    /**
     * Método responsável por inflar a view e retornar um ViewHolder
     */
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        position: Int): MyViewHolder {

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_city_layout, viewGroup, false)

        return MyViewHolder(view)
    }

    /**
     * Método que retorna a quantidade de itens da lista
     *
     * Aqui utilizamos o operador Elvis Operator ?:
     * https://www.concrete.com.br/2017/06/21/kotlin-no-tratamento-de-erros/
     */
    override fun getItemCount() = list?.size ?: 0

    /**
     * Método responsável por realizar o bind da View com o item
     *
     * @param vh Nosso viewholder criado para reciclar as views
     * @param position posição do item que será inflado no recyclerview
     */
    override fun onBindViewHolder(vh: MyViewHolder, position: Int) {
        list?.let {
            vh.bind(it[position], saveFavoriteCallback)
        }
    }

    /**
     * Classe responsável por fazer o bind da View com o objeto City
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        /**
         * Método que faz o bind
         *
         * @param city objeto a ser exibido
         * @param callback expressão lambda que será invokada quando a view for clicada/tocada
         */
        fun bind(city: City, callback: (City) -> Unit) {
            itemView.tvCityCountry.text = "${city.name}, ${city.id}"
            itemView.tvTemp.text = city.main.temp.roundToInt().toString()
            val weather = city.weathers[0]
            itemView.tvDescription.text = weather.description

            changeStar(city.favorite)

            val details = "wind ${city.wind?.speed}m/s | clouds ${city.clouds?.all}% | sea ${city.main?.seaLevel} m"
            itemView.tvDetails.text = details

            /**
             * Glide é uma lib opensource para facilitar o carregamento de imagens de forma eficiente
             * @sample https://github.com/bumptech/glide
              */
            Glide.with(itemView.context)
                .load("http://openweathermap.org/img/w/${weather.icon}.png")
                .placeholder(R.drawable.w_01d)
                .into(itemView.imgIcon)

            itemView.imgFavoriteStar.setOnClickListener {
                callback(city)
                changeStar(!city.favorite)
            }

            itemView.tvTempType.text = if (Util.getUnit(itemView.context) == "metric")  "C" else "F"
        }

        private fun changeStar(favorite: Boolean) {
            if (favorite) {
                itemView.imgFavoriteStar.setImageBitmap(
                    BitmapFactory.decodeResource(
                        itemView.context.resources,
                        R.drawable.star_gold
                    )
                )
            } else {
                itemView.imgFavoriteStar.setImageBitmap(
                    BitmapFactory.decodeResource(
                        itemView.context.resources,
                        R.drawable.star_empty
                    )
                )

            }
        }
    }


    /**
     * Método responsável por atualizar os itens do recyclerview
     */
    fun updataData(list: List<City>) {
        this.list = list
        notifyDataSetChanged()
    }


}