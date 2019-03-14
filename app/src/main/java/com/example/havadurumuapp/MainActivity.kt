package com.example.havadurumuapp

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var txtSehir: TextView? = null
    var location: SimpleLocation? = null
    var latitude: String? = null
    var longitude: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var spinerAdapter = ArrayAdapter.createFromResource(this, R.array.sehirler, android.R.layout.simple_list_item_1)
        spinerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnSehirler.adapter = spinerAdapter
        spnSehirler.setOnItemSelectedListener(this)
        location = SimpleLocation(this)

        location?.setListener(object : SimpleLocation.Listener {
            override fun onPositionChanged() {
                //enlem ve boylamı aldık
                //latitude =String.format("%.2f",location?.getLatitude())

                //longitude = String.format("%.2f",location?.getLongitude())

                //Log.e("sevgi",""+latitude+" "+longitude)

            }

        })



        verileriGetir("Antalya")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // txtSehir= view as TextView
        if (!location!!.hasLocationEnabled()) {
            // izin isteme
            Toast.makeText(this, "GPS açmak ister misin", Toast.LENGTH_SHORT).show()
            SimpleLocation.openSettings(this);
        } else
            // izin isteme
            // izin isteme
        {
            if (checkSelfPermission(this,ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(ACCESS_COARSE_LOCATION),60)

            }
            else{
                latitude = String.format("%.2f", location?.getLatitude())
                longitude = String.format("%.2f", location?.getLongitude())
                oAnkiSehriGetir(latitude,longitude)
            }
        }
        if (position == 0) {

            var oAnkiSehirAdi = oAnkiSehriGetir(latitude, longitude)
            txtSehir?.setText(oAnkiSehirAdi)
        } else {
            var secilenSehir = parent?.getItemAtPosition(position).toString()
            verileriGetir(secilenSehir)
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode==60)
        {
            if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                latitude = String.format("%.2f", location?.getLatitude())
                longitude = String.format("%.2f", location?.getLongitude())
            }
            else{
                Toast.makeText(this,"İzin vermediğin maalesef için sana yardımcı olamayız",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun oAnkiSehriGetir(latitude: String?, longitude: String?): String? {
        var sehirAdi: String? = null
        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=d07ace976526f4c9e0a64b0afa46b691&lang=tr&units=metric"
        val havaDurumuObjeRequest2 =
            JsonObjectRequest(Request.Method.GET, url, null, object : Response.Listener<JSONObject> {
                override fun onResponse(response: JSONObject?) {
                    //Toast.makeText(this@MainActivity,response.toString(),Toast.LENGTH_LONG).show()

                    var main = response?.getJSONObject("main")
                    var sicaklik = main?.getInt("temp")
                    txtSicaklik.text = sicaklik.toString()


                    sehirAdi = response?.getString("name")
                    txtSehir?.setText(sehirAdi)

                    var weather = response?.getJSONArray("weather")
                    var aciklama = weather?.getJSONObject(0)?.getString("description")
                    txtAciklama.text = aciklama
                    var icon = weather?.getJSONObject(0)?.getString("icon")
                    var resimDosyAdi =
                        resources.getIdentifier("icon_" + icon?.sonKarakteriSil(), "drawable", packageName)
                    imgIcon.setImageResource(resimDosyAdi)

                    txtTarih.text = tarihYazdir()


                }

            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {

                }

            })

        MySingleton.getInstance(this).addToRequestQueue(havaDurumuObjeRequest2)
        if (sehirAdi != null) {
            return sehirAdi
        } else return "N/A"


    }


    override fun onResume() {
        super.onResume()
        location?.beginUpdates()
    }

    override fun onPause() {
        super.onPause()
        location?.endUpdates()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


    fun verileriGetir(sehir: String) {
        val url =
            "https://api.openweathermap.org/data/2.5/weather?q=" + sehir + ",tr&appid=d07ace976526f4c9e0a64b0afa46b691&lang=tr&units=metric"
        val havaDurumuObjeRequest =
            JsonObjectRequest(Request.Method.GET, url, null, object : Response.Listener<JSONObject> {
                override fun onResponse(response: JSONObject?) {
                    //Toast.makeText(this@MainActivity,response.toString(),Toast.LENGTH_LONG).show()

                    var main = response?.getJSONObject("main")
                    var sicaklik = main?.getInt("temp")
                    txtSicaklik.text = sicaklik.toString()


                    var sehirAdi = response?.getString("name")
                    txtSehir?.text = sehirAdi
                    var weather = response?.getJSONArray("weather")
                    var aciklama = weather?.getJSONObject(0)?.getString("description")
                    txtAciklama.text = aciklama
                    var icon = weather?.getJSONObject(0)?.getString("icon")
                    var resimDosyAdi =
                        resources.getIdentifier("icon_" + icon?.sonKarakteriSil(), "drawable", packageName)
                    imgIcon.setImageResource(resimDosyAdi)

                    txtTarih.text = tarihYazdir()


                }

            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {

                }

            })

        MySingleton.getInstance(this).addToRequestQueue(havaDurumuObjeRequest)
    }

    fun tarihYazdir(): String {
        var takvim = Calendar.getInstance().time
        var formatli = SimpleDateFormat("EEEE,MMMM,yyyy", Locale("tr"))
        var tarih = formatli.format(takvim)

        return tarih

    }
}

private fun String?.sonKarakteriSil(): String? {
    return this?.substring(0, this?.length - 1)

}
