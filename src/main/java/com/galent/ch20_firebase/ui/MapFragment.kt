package com.galent.ch20_firebase.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.galent.ch20_firebase.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.BufferedReader
import java.io.InputStreamReader
import android.util.Log


class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val defaultLocation = LatLng(37.5665, 126.9780)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))

        // 위치 권한 있을 경우 내 위치 활성화
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        }

        // 여기부터 마커 찍는 CSV 읽기 코드 붙이기!
        val inputStream = requireContext().assets.open("facilities.csv")
        val reader = BufferedReader(InputStreamReader(inputStream, "EUC-KR"))


        reader.readLine() // 헤더 생략
        var count = 0

        reader.forEachLine { line ->
            val tokens = line.split(",")
            if (tokens.size >= 13) {
                val name = tokens[5].trim()
                val lat = tokens[12].toDoubleOrNull()
                val lng = tokens[11].toDoubleOrNull()

                if (lat != null && lng != null) {
                    val latLng = LatLng(lat, lng)
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(name)
                    )

                    if (count == 0) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
                    }

                    count++
                }
            } else {
                Log.d("25android", "줄 무시됨 (열 부족): ${tokens.size}개")
            }
        }
        Log.d("25android", "마커 총 ${count}개 추가됨")

    }

}
