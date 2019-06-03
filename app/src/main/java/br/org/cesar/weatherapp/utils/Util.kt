package br.org.cesar.weatherapp.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import br.org.cesar.weatherapp.Constants

object Util{

    private fun getPrefs(context: Context) : SharedPreferences {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getUnit(context: Context):String {
        val isTempC = (getPrefs(context).getBoolean(Constants.PREF_TEMP_C, true))
        return if (isTempC) "metric" else "imperial"
    }
}
