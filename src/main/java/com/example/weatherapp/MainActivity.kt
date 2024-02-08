package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Query
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("delhi")
        SearchCity()

    }

    private fun fetchWeatherData(cityname:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityname, "f66ffbf4f16b1147cc78cdca18ba7e29", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val minTemp = responseBody.main.temp_min
                    val maxTemp = responseBody.main.temp_max



                    binding.temp.text="$temperature °C"
                    binding.weather.text=condition
                    binding.maxTemp.text ="Max Temp: $maxTemp"
                    binding.minTemp.text = "Min Temp: $minTemp"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunRise.text = "${time(sunRise)}"
                    binding.sunSet.text = "${time(sunSet)}"
                    binding.sea.text = "$seaLevel"
                    binding.condition.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                        binding.date.text= date()
                        binding.cityname.text="$cityname"

                     //Log.d("TAG","onResponse: $temperature")

                    changeImgCond(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Log.e("TAG", "Error occurred during API call: ${t.message}")
            }
        })
    }
    private fun changeImgCond(condition:String){
        when(condition){
            "Clear Sky", "Sunny", "Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Drizzle","Moderate Rain", "Showers", "Heavy Rain"  ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
            binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }
    private fun date(): String{
        val sdf = SimpleDateFormat("dd MMMM YYYY")
        return sdf.format((Date()))
    }
    private fun time(timestamp: Long):String{
        val sdf = SimpleDateFormat("HH:MM", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
    fun dayName(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
   private fun SearchCity(){
       val searchView = binding.searchView
       searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
           //here is some changes as it takes references from above
           android.widget.SearchView.OnQueryTextListener {
           override fun onQueryTextSubmit(query: String?):Boolean{
               if(query != null) {
                   fetchWeatherData(query)
               }
                   return true

           }
           override fun onQueryTextChange(newText: String?):Boolean{
               return true
           }
       })

    }

}