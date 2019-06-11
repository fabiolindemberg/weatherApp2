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

class WeatherAdapter(private val saveFavoriteCallback: (City) -> Unit) : RecyclerView.Adapter<WeatherAdapter.MyViewHolder>() {

    private var list: List<City>? = null

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        position: Int): MyViewHolder {

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_city_layout, viewGroup, false)

        return MyViewHolder(view)
    }

    override fun getItemCount() = list?.size ?: 0

    override fun onBindViewHolder(vh: MyViewHolder, position: Int) {
        list?.let {
            vh.bind(it[position], saveFavoriteCallback)
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(city: City, callback: (City) -> Unit) {
            itemView.tvCityCountry.text = "${city.name} - ${city.sys.country}"
            itemView.tvTemp.text = city.main.temp.roundToInt().toString()
            val weather = city.weathers[0]
            itemView.tvDescription.text = weather.description

            changeStar(city.favorite)

            if (Util.getLang(itemView.context) == "en") {
                itemView.tvDetails.text = "wind ${city.wind?.speed}m/s | clouds ${city.clouds?.all}% | sea ${city.main?.seaLevel} m"
            }else{
                itemView.tvDetails.text = "vento ${city.wind?.speed}m/s | nuvens ${city.clouds?.all}% | maré ${city.main?.seaLevel} m"

            }

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

    fun updataData(list: List<City>) {
        this.list = list
        notifyDataSetChanged()
    }


}