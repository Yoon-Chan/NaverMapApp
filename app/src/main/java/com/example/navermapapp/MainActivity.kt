package com.example.navermapapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import com.example.navermapapp.databinding.ActivityMainBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.Tm128
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() , OnMapReadyCallback{
    private lateinit var binding : ActivityMainBinding
    private lateinit var naverMap : NaverMap
    private var isMapInit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)



        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return if(p0?.isNotEmpty() == true){
                    SearchRepository.getGoodRestaurant(p0).enqueue(object : Callback<SearchResult>{
                        override fun onFailure(call: Call<SearchResult>, t: Throwable) {

                        }

                        override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
                            val searchItemList = response.body()?.items.orEmpty()

                            if(searchItemList.isEmpty()){
                                Toast.makeText(this@MainActivity, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                                return
                            }else if(isMapInit.not()){
                                Toast.makeText(this@MainActivity, "오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
                                return
                            }
//                            Log.e("onResponse", response.body().toString())

                            //검색된 부분들 마커 처리하기
                            val markers = searchItemList.map {
                                Marker(Tm128(it.mapx.toDouble(), it.mapy.toDouble()).toLatLng()).apply {
                                    captionText = it.title
                                    map = naverMap
                                }
                            }

                            //첫 번째 검색어 부분 카메라 이동
                            val cameraUpdate = CameraUpdate.scrollTo(markers.first().position)
                                .animate(CameraAnimation.Easing)
                            naverMap.moveCamera(cameraUpdate)


                        }
                    })
                    false
                }else{
                    true
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onMapReady(p0: NaverMap) {
        naverMap = p0
        isMapInit = true

        val cameraUpdate = CameraUpdate.scrollTo(LatLng(35.5666102, 126.9783881))
            .animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)
    }
}