package com.versilistyson.welldone.presentation.ui.dashboard

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.versilistyson.welldone.R
import com.versilistyson.welldone.domain.framework.entity.Entity
import com.versilistyson.welldone.presentation.adapter.SensorStatusListAdapter
import com.versilistyson.welldone.presentation.ui.dashboard.detail.PumpDialogDetailFragment
import com.versilistyson.welldone.presentation.util.MAPVIEW_BUNDLE_KEY
import com.versilistyson.welldone.presentation.viewmodel.DashboardViewmodel
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, SensorStatusListAdapter.DashboardToDetailsDialog {

    private lateinit var viewmodel: DashboardViewmodel
    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var sensorStatusListAdapter: SensorStatusListAdapter
    private val allSensors: List<Entity.Sensor>
        get() = sensorStatusListAdapter.sensors
    private lateinit var averageLatLng: LatLng

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        initViewModel()
        mapView = view.findViewById(R.id.map_view)
        initGoogleMap(savedInstanceState)
        return view
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {
        if(savedInstanceState != null){
            mapView.onCreate(savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY))
        } else {
            mapView.onCreate(savedInstanceState)
        }

        viewmodel.sensorLiveData.observe(viewLifecycleOwner, Observer {

            sensorStatusListAdapter = SensorStatusListAdapter(it.allSensors, this)
            initRecyclerView()
            averageLatLng = it.avgLatLng
            mapView.getMapAsync(this)
        })

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(averageLatLng, 6.0f))

        for(s in allSensors){
            //add a marker to the map in the sensor
            mMap.addMarker(generateMarker(s))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapExpandButton.setOnClickListener {
            val action =
                DashboardFragmentDirections.actionDashboardFragmentToFullScreenMapFragment()
            findNavController().navigate(action)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }

    private fun initRecyclerView() {
        rv_pump_status.apply{
            adapter = sensorStatusListAdapter
            layoutManager = LinearLayoutManager(activity!!.applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker?.let{

            return true
        }
        return false
    }

    private fun generateMarker(sensor: Entity.Sensor): MarkerOptions {
        val marker = MarkerOptions()
            .position(sensor.location)

        when(sensor.sensorStatus){
            null -> marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pump_functioning))
            1 -> marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pump_no_data))
            2 -> marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pump_non_functioning))
        }

        return marker
    }

    private fun initViewModel(){
        viewmodel = activity.let {
            val appContext = activity?.applicationContext as Application
            ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(appContext)
                .create(DashboardViewmodel::class.java)
        }
    }

    override fun moveToDialog(sensor: Entity.Sensor) {
        val pumpDialogDetailFragment =
            PumpDialogDetailFragment()
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        val prev = activity!!.supportFragmentManager.findFragmentByTag("dialog")
        if(prev != null){
            fragmentTransaction.remove(prev)
        }
        fragmentTransaction.addToBackStack(null)
        val bundle = Bundle()
        bundle.putSerializable("sensor", sensor)
        pumpDialogDetailFragment.arguments = bundle
        pumpDialogDetailFragment.show(fragmentTransaction, "dialog")
    }

    override fun onStart() {
        super.onStart()
        map_view.onStart()
    }

    override fun onResume() {
        super.onResume()
        map_view.onResume()
    }

    override fun onStop() {
        super.onStop()
        map_view.onStop()
    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        map_view.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_view.onLowMemory()
    }
}