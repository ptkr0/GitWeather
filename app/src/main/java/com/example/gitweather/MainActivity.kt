package com.example.gitweather

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.gitweather.POJO.Model
import com.example.gitweather.Utilities.APIUtil
import com.example.gitweather.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding= DataBindingUtil.setContentView(this,R.layout.activity_main)
        supportActionBar?.hide()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        activityMainBinding.rlMain.visibility=android.view.View.GONE // hide main layout until we get the weather

        getCurrentLocation()

        activityMainBinding.etSearch.setOnClickListener {
            val city = activityMainBinding.etSearch.text.toString()
            if(city.isNotEmpty())
            {
                getCityWeather(city)
            }
            else
            {
                Toast.makeText(this,getString(R.string.enter_city),Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentLocation() {
        if(checkPermission())
        {
            if(isLocationEnabled())
            {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task->
                    val location: Location?= task.result
                    if(location==null)
                    {
                        Toast.makeText(this,getString(R.string.null_city),Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        getCurrentWeather(location.latitude.toString(),location.longitude.toString())
                    }
                }
            }
            else
            {
                // If location is not enabled, open settings
                Toast.makeText(this,getString(R.string.loc_disabled),Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else
        {
            // If permission is not granted, request permission
            requestPermission()
        }
    }

    private fun getCurrentWeather(latitude: String, longitude: String) {

        activityMainBinding.progressBar.visibility=android.view.View.VISIBLE // show progress bar while we get the weather
        APIUtil.getIAPI().getCurrentWeather(latitude, longitude, API_KEY).enqueue(object: retrofit2.Callback<Model> {
            override fun onResponse(call: retrofit2.Call<Model>, response: retrofit2.Response<Model>) {
                if(response.isSuccessful)
                {
                    activityMainBinding.progressBar.visibility=android.view.View.GONE // hide progress bar
                    activityMainBinding.rlMain.visibility=android.view.View.VISIBLE // show main layout
                    setData(response.body())
                }
            }

            override fun onFailure(call: retrofit2.Call<Model>, t: Throwable) {
                Toast.makeText(applicationContext,getString(R.string.error),Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getCityWeather(city: String) {
        activityMainBinding.progressBar.visibility=android.view.View.VISIBLE // show progress bar while we get the weather
        APIUtil.getIAPI().getCityWeather(city, API_KEY).enqueue(object: retrofit2.Callback<Model> {
            override fun onResponse(call: retrofit2.Call<Model>, response: retrofit2.Response<Model>) {
                if(response.isSuccessful)
                {
                    activityMainBinding.progressBar.visibility=android.view.View.GONE // hide progress bar
                    activityMainBinding.rlMain.visibility=android.view.View.VISIBLE // show main layout
                    setData(response.body())
                }
            }

            override fun onFailure(call: retrofit2.Call<Model>, t: Throwable) {
                Toast.makeText(applicationContext,getString(R.string.error),Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setData(body: Model?) {

        setEmojiandDesc(body?.main?.temp!!)
        setDateAndTime(body.timezone)
        val city = body.name
        val temp = body.main.temp.roundToInt().toString()+"°C"
        val minTemp = body.main.temp_min.toString()+"°C"
        val maxTemp = body.main.temp_max.toString()+"°C"
        val humidity = body.main.humidity.toString()+"%"
        val wind = body.wind.speed.toString()+"m/s"
        val pressure = body.main.pressure.toString()+"hPa"

        activityMainBinding.tvWind.text=getString(R.string.wind)+" "+wind
        activityMainBinding.tvPressure.text=getString(R.string.pressure)+" "+pressure
        activityMainBinding.tvRain.text=getString(R.string.rain)+" "+humidity

        activityMainBinding.tvTemp.text=temp

        activityMainBinding.tvMinTemp.text=getString(R.string.temp_min)+"\n"+minTemp
        activityMainBinding.tvMaxTemp.text=getString(R.string.temp_max)+"\n"+maxTemp

        activityMainBinding.tvCity.text=city+" "+getString(R.string.city)
    }

    private fun setEmojiandDesc(temp: Double) {
        if(temp < -10.0) {
            activityMainBinding.tvEmoji.text=getString(R.string.colder)
            activityMainBinding.tvWeatherInfo.text=getString(R.string.colder_weather)
        }
        if(temp >= -10.0 && temp < 5.0) {
            activityMainBinding.tvEmoji.text=getString(R.string.cold)
            activityMainBinding.tvWeatherInfo.text=getString(R.string.cold_weather)
        }
        if(temp >= 5.0 && temp < 10.0) {
            activityMainBinding.tvEmoji.text=getString(R.string.cool)
            activityMainBinding.tvWeatherInfo.text=getString(R.string.cool_weather)
        }
        if(temp >= 10.0 && temp < 15.0) {
            activityMainBinding.tvEmoji.text = getString(R.string.cooler)
            activityMainBinding.tvWeatherInfo.text = getString(R.string.cooler_weather)
        }
        if(temp >= 15.0 && temp < 20.0) {
            activityMainBinding.tvEmoji.text=getString(R.string.warm)
            activityMainBinding.tvWeatherInfo.text=getString(R.string.warm_weather)
        }
        if(temp >= 20.0 && temp < 25.0) {
            activityMainBinding.tvEmoji.text=getString(R.string.warmer)
            activityMainBinding.tvWeatherInfo.text=getString(R.string.warmer_weather)
        }
        if(temp >= 25.0) {
            activityMainBinding.tvEmoji.text=getString(R.string.hot)
            activityMainBinding.tvWeatherInfo.text=getString(R.string.hot_weather)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDateAndTime(timezone: Int){
        val sdf = SimpleDateFormat("dd/MM/yy HH:mm")
        val currentDate = sdf.format(System.currentTimeMillis()+(timezone*1000)-3600000)
        activityMainBinding.tvDateAndTime.text=currentDate
    }

    // Requests permission to access location
    // arrayOf since we ask for both coarse and fine location
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
        private const val API_KEY = "OPEN_WEATHER_API_KEY"
    }

    // Check if location is granted
    // COARSE_LOCATION: approximate location
    // FINE_LOCATION: precise location
    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // Android function that is called when the user responds to the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== PERMISSION_REQUEST_ACCESS_LOCATION)
        {
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this,getString(R.string.permission_granted),Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else
            {
                Toast.makeText(this,getString(R.string.permission_denied),Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Check if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}