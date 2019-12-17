package com.versilistyson.welldone.ui.dashboard.fragment

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.versilistyson.welldone.R
import com.versilistyson.welldone.data.remote.dto.PumpResponse
import com.versilistyson.welldone.data.remote.dto.SensorRecentResponse
import com.versilistyson.welldone.ui.dashboard.DashboardViewmodel
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

/*
    Dashboard map for WellDone operator where they can view the pumps on a map.
    Landscape and portrait mode. In landscape mode, map is bigger and pump status
    displays in a little box on the map when you click on a marker on the map. In
    portrait mode it displays in a little recycler view underneath, shows previous marker
    clicks as well.
 */

class DashboardFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewmodel: DashboardViewmodel
    private lateinit var mMap: GoogleMap
    private lateinit var mMapView: MapView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMapView = map_view

        if(map_view != null){
            mMapView.onCreate(null)
            mMapView.onResume()
            mMapView.getMapAsync(this)
        }

        viewmodel = activity.let {
            val appContext = activity?.applicationContext as Application
            ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(appContext)
                .create(DashboardViewmodel::class.java)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        MapsInitializer.initialize(context)
        mMap = googleMap

        viewmodel.sensorLiveData.observe(viewLifecycleOwner, Observer{
            if(it.isSuccessful){
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                    val averageLatLng = addMarkers(it.body()!!)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(averageLatLng))
                }
            }
        })
    }

    fun addMarkers(pumps: List<SensorRecentResponse>): LatLng{

        var totalLat = 0.0
        var totalLng = 0.0

        for(l in pumps){
            val point = LatLng(l.latitude, l.longitude)
            totalLat += point.latitude
            totalLng += point.longitude

            val marker = MarkerOptions()
                    .position(point)

            when(l.status){
                null -> marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.no_data_marker))
                1 -> marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.non_working_marker))
                2 -> marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.working_marker))
            }

            mMap.addMarker(marker)
        }
        return LatLng(totalLat/pumps.size, totalLng/pumps.size)
    }
}
