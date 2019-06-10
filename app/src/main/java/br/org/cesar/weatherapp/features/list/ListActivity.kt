package br.org.cesar.weatherapp.features.list

import android.app.ListActivity
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import br.org.cesar.weatherapp.features.setting.SettingActivity
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import br.org.cesar.weatherapp.Constants
import br.org.cesar.weatherapp.R
import br.org.cesar.weatherapp.api.RetrofitManager
import br.org.cesar.weatherapp.database.RoomManager
import br.org.cesar.weatherapp.database.WeatherDatabase
import br.org.cesar.weatherapp.entity.City
import br.org.cesar.weatherapp.entity.FavoriteCity
import br.org.cesar.weatherapp.entity.FindResult
import br.org.cesar.weatherapp.utils.Util
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ListActivity : AppCompatActivity() {

    val adapter = WeatherAdapter{ saveFavorite(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        val lang = "pt-rBR"
        val locale = Locale(lang)
        val connfig = Configuration()
        connfig.setLocale(locale)

        baseContext.resources.updateConfiguration(connfig,  baseContext.resources.displayMetrics)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()

    }

    /**
     * Salva uma cidade favorita no banco de dados de forma SÍNCRONA
     */
    private fun saveFavorite(city: City) {
        saveFavoriteAsync(this, city)
    }

    /**
     * Salva uma cidade favorita no banco de dados de forma ASSÍNCRONA
     */
    private fun saveFavoriteAsync(context: Context, city: City) {
        doAsync {

            var favoriteCities: List<FavoriteCity>? = null

            RoomManager.instance(context).favoriteDao().apply {
                city.let {
                    val (id, name) = it
                    if (selectById(it.id) == null) {
                        insert(FavoriteCity(id, name))
                    }else{
                        delete(FavoriteCity(id, name))
                    }
                }
                favoriteCities = selectAll()
            }

            uiThread {
                favoriteCities?.let { it1 -> refreshList(it1) }
            }
        }
    }

    /**
     * Classe que herda de AsyncTask para salvar a cidade favorita de forma assíncrona
     */
    class FavoriteAsync(val context: Context) : AsyncTask<City, Void, List<FavoriteCity>>() {

        /**
         * Método executado em segundo plano
         */
        override fun doInBackground(vararg params: City?): List<FavoriteCity> {
            RoomManager.instance(context).favoriteDao().apply {
                params[0]?.let {
                    val (id,name) = it
                    insert(FavoriteCity(id, name))
                }
                return selectAll()
            }
        }

        /**
         * Método que será executado após o doInBackground.
         * Aqui, o processamento será realizado na Main Thread
         */
        override fun onPostExecute(result: List<FavoriteCity>?) {
            result?.apply {
                forEach {
                    Log.d("w", it.name) }
            }
        }

    }

    /**
     * Método paque faz a requisição a Weather API e atualiza o recycler view
     */
    private fun refreshList(list: List<FavoriteCity>?) {

        var favoriteCities = list
        if (favoriteCities == null) {
            RoomManager.instance(this).favoriteDao().apply {
                favoriteCities = selectAll()
            }
        }

        progressBar.visibility = View.VISIBLE

        val units = Util.getUnit(this)

        val rm = RetrofitManager()
        val call = rm.weatherService().find(
            edtSearch.text.toString(),
            "5fde54966e3e1c8a80e436245bdf9672",
            units)

        call.enqueue(object : Callback<FindResult> {

            override fun onFailure(call: Call<FindResult>, t: Throwable) {
                progressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<FindResult>, response: Response<FindResult>) {
                if (response.isSuccessful) {
                    response.body()?.let { findResult ->

                        findResult.list.forEach{
                            favoriteCities?.forEach { favoriteCity ->
                                if (it.id == favoriteCity.id) {
                                    it.favorite = true
                                }
                            }
                        }

                        adapter.updataData(findResult.list)
                    }
                }
                progressBar.visibility = View.GONE
            }

        })
    }

    /**
     * Inicializa os componentes da UI
     */
    private fun initUI() {
        btnSearch.setOnClickListener {
            if (isDeviceConnected()) {
                refreshList(null)
            } else {
                Toast.makeText(this, "Desconectado", Toast.LENGTH_SHORT).show()
            }
        }

        // RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    /**
     * Verifica se o device está conectado a internet
     */
    private fun isDeviceConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    /**
     * Método que infla o menu na Actionbar/Toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.weather_menu, menu)
        return true
    }

    /**
     * Método que será invocado quando o item do menu for selecionado
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.settings_action) {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        return true
    }
}
