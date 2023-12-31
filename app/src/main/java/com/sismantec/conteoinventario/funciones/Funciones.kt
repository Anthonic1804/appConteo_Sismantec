package com.sismantec.conteoinventario.funciones

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.sismantec.conteoinventario.R
import com.sismantec.conteoinventario.database.DataBaseConteo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Funciones{

    //DATACLASE PARA OBTENER LAS PREFERENCIAS ALMACENADAS
    data class ValoresPrefs(
        val ip: String?,
        val puerto: String?,
        val empleado: String?,
        val idEmpleado: Int?,
        val esAdmin: Int?,

        //VARIABLES PARA PRODUCTOS Y CONTEOS
        val from: String?,
        val idConteo: Int?,
        val tipoConteo: String?,
        val ubicacion: String?,
        val estado: String?,
        val fechaInicio: String?,
        val fechaEnvio: String?,

        //VARIABLES DE PRODUCTOS
        val idProducto: Int?,
        val codigoProducto: String?,
        val descripcionProducto: String?,
        val unidades: String?,
        val fracciones: String?

    )

    //FUNCION PARA VERIFICAR LA CONEXION A INTERNET
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET])
    fun isInternetReachable(context: Context): Boolean {
        if (isNetworkAvailable(context)) {
            try {
                val httpConnection: HttpURLConnection = URL("https://clients3.google.com/generate_204")
                    .openConnection() as HttpURLConnection
                httpConnection.setRequestProperty("User-Agent", "Android")
                httpConnection.setRequestProperty("Connection", "close")
                httpConnection.connectTimeout = 1500
                httpConnection.connect()

                return httpConnection.responseCode == 204
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
    //FIN FUNCION PARA VERIFICAR LA CONEXION A INTERNET

    //OBTENIENDO LA CONEXION DE RETROFIT
    fun getRetrofit(url:String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //FUNCION PARA OBTENER EL SERVIDOR
    fun getServidor(ip: String?, puerto: String?): String {
        return "http://${ip}:${puerto}/"
    }

    //FUNCION PARA OBTENER LA INSTANCIA DE LA BASE DE DATOS
    fun getDataBase(context: Context): DataBaseConteo {
        return DataBaseConteo(context)
    }

    //FUNCION PARA OBTERNER LAS PREFERENCIAS ALMACENADAS
    fun getPreferences(context: Context): ValoresPrefs{

        val prefs = context.getSharedPreferences("serverData", Context.MODE_PRIVATE)
        val ip = prefs.getString("ip", null)
        val puerto = prefs.getString("puerto", null)
        val empleado = prefs.getString("empleado", null)
        val idEmpleado = prefs.getInt("idEmpleado", 0)
        val esAdmin = prefs.getInt("esAdmin", 0)

        //VARIABLES PARA LOS CONTEO Y PRODUCTOS
        val from = prefs.getString("from", null)
        val idConteo = prefs.getInt("idConteo", 0)
        val tipoConteo = prefs.getString("tipoConteo", null)
        val ubicacion = prefs.getString("ubicacion", null)
        val estado = prefs.getString("estado", null)
        val fechaInicio = prefs.getString("fechaInicio", null)
        val fechaEnvio = prefs.getString("fechaEnvio", null)

        //VARIABLES PARA PRODUCTOS
        val idProducto = prefs.getInt("idProducto", 0)
        val codigoProducto = prefs.getString("codigoProducto", null)
        val descripcionProducto = prefs.getString("descripcionProducto", null)
        val unidades = prefs.getString("unidades", null)
        val fracciones = prefs.getString("fracciones", null)


        return ValoresPrefs(ip, puerto, empleado, idEmpleado, esAdmin,
        from, idConteo, tipoConteo, ubicacion, estado, fechaInicio, fechaEnvio,
        idProducto, codigoProducto, descripcionProducto, unidades, fracciones)

    }

    //FUNCION PARA OBTENER FECHA
    fun getDateTime(): String {
        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()
        )
        val date = Date()
        return dateFormat.format(date)
    }

    //FUNCION TOAST ACEPTADO
    fun toastMensaje(context: Context, msj: String, tipo: Int){
        val inflater = LayoutInflater.from(context)
        var layout: View? = null
        layout = when(tipo){
            0 -> {
                inflater.inflate(R.layout.toast_error, null)
            }else ->{
                inflater.inflate(R.layout.toast_correcto, null)
            }
        }

        val txtMensaje = layout.findViewById<TextView>(R.id.tvMensaje)
        txtMensaje.text = msj

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view= layout
        toast.setGravity(Gravity.BOTTOM, 0,100)
        toast.show()
    }
}
