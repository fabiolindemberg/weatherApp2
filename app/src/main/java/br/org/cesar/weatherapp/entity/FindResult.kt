package br.org.cesar.weatherapp.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class FindResult(var list: List<City>)

data class City(
    var id: Int,
    var name: String,
    @SerializedName("weather") var weathers: List<Weather>,
    var main: Main,
    var wind: Wind,
    var sys: Sys,
    var rain: Rain,
    var clouds: Clouds)

data class Weather(var main: String,
                   var icon: String,
                   var description: String)

data class Sys(var country: String)

data class Wind(var speed: Double, var deg: Double)

data class Rain(@ColumnInfo(name = "3h") var vol: Double)

data class Clouds (var all: Double)

data class Main(var temp: Double,
                var pressure: Double,
                @ColumnInfo(name = "sea_level")
                var seaLevel: Double)

@Entity(tableName = "tb_favorite_city")
data class FavoriteCity(
    @PrimaryKey
    var id: Int,
    @ColumnInfo(name = "city_name")
    var name: String)