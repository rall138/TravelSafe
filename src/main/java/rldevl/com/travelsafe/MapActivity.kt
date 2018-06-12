package rldevl.com.travelsafe

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.rldevel.helpers.CustomMarker
import com.rldevel.helpers.PolyLineHelper
import com.rldevel.helpers.RouteDefinition

import rldevel.com.parsers.DirectionSearch
import rldevel.com.parsers.JSONDirectionParser

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var manejadorArrastreMarcadores: ManejadorArrastreMarcadores? = null
    private var definicionActiva = false
    private var currentLatittudeLongitud: LatLng? = null
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var googleMap: GoogleMap? = null
    private var lastActiveMarker: MarkerOptions? = null

    /* Variables para definicion de ruta */

    // true si se esta definiendo la ruta en este mismo momento.
    private val marcadorSeleccionado: Marker? = null

    /* Variables para definicion de ruta */

    private var btnAddLocation: ImageButton? = null
    private var btnAddGasStation: ImageButton? = null
    private var btnAddRestauran: ImageButton? = null

    private val txtSearchDirection: EditText? = null
    private val btnSearchDirection: Button? = null
    private var action_selected: ICON_TYPE? = null

    private var isFirstTime: Boolean = false
    private var routeDefinition: RouteDefinition? = null
    private var routeDefinitionAuxiliar: RouteDefinition? = null

    //endregion

    //region FUNCIONES Y SUB RUTINAS

    private val isFirstMarker = routeDefinition!!.colleccionMarcadores.isEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Asociación de CallBack con el fragmento que se debe actualizar
        val mapFragment = R.id.mapfragment as MapFragment
        mapFragment.getMapAsync(this)

        btnAddLocation = R.id.btnAddLocation as ImageButton
        btnAddGasStation = R.id.btnAddGasStation as ImageButton
        btnAddRestauran = R.id.btnAddRestaurant as ImageButton

        this.definicionActiva = true
        this.eventRegistration()

        //this.listenerOnStartCancelEdition();
        //this.escuchaBotonPosicionActual();
        //this.escuchaBotonMarcadorCentral();
        //this.listenerOnBtnSearch();

    }

    enum class ICON_TYPE {LOCATION, GASSTATION, RESTAURANT}
    private fun eventRegistration() {
        this.btnAddLocation!!.setOnClickListener { action_selected = ICON_TYPE.LOCATION }
        this.btnAddGasStation!!.setOnClickListener { action_selected = ICON_TYPE.GASSTATION }
        this.btnAddRestauran!!.setOnClickListener { action_selected = ICON_TYPE.RESTAURANT }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {

            override fun onLocationChanged(location: Location) {
                //dibujadoDistanciaPrimerPuntoAposicionActual();
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                GeneralHelper.generateMessage(TAG, "Cambio de status")
            }

            override fun onProviderEnabled(provider: String) {

            }

            override fun onProviderDisabled(provider: String) {

            }

        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    NotificationManager.IMPORTANCE_DEFAULT)
        }
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)

        if (this.currentLatittudeLongitud == null) {
            val location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            this.currentLatittudeLongitud = LatLng(location.latitude, location.latitude)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.mapEventListeners()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    NotificationManager.IMPORTANCE_DEFAULT)
        }

        this.googleMap!!.isMyLocationEnabled = true
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        if (isFirstTime) {

            currentLatittudeLongitud = LatLng(locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).latitude,
                    locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).longitude)

            this.googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatittudeLongitud, 14.0f))
            isFirstTime = false

        }

        routeDefinition = RouteDefinition(this.googleMap)
        routeDefinition!!.polyLineHelper = PolyLineHelper()
        manejadorArrastreMarcadores = ManejadorArrastreMarcadores(routeDefinition)

    }

    //region For the one point selecction over a pre defined routemap

    enum class SELECTION_ACTION {
        GROUP_MEETING
    }

    fun AlertOverClientSelection(): SELECTION_ACTION {
        return SELECTION_ACTION.GROUP_MEETING
    }

    //endregion For the one point selecction over a pre defined routemap


    private fun componerDefinicionRutaPrimerPunto() {

        if (routeDefinitionAuxiliar != null)
            deleteRouteDefiniton(routeDefinitionAuxiliar!!)

        routeDefinitionAuxiliar = RouteDefinition(googleMap)
        routeDefinitionAuxiliar!!.polyLineHelper = PolyLineHelper()

        this.addMarker(this.currentLatittudeLongitud, false, true).isVisible = false
        this.addMarker(routeDefinition!!.colleccionMarcadores.firstItem.marcador.position, false, true)

    }

    private fun dibujadoDistanciaPrimerPuntoAposicionActual() {

        val parser = JSONDirectionParser(routeDefinitionAuxiliar)
        val polyLineHelper = parser.obtenerRespuestaDeServicioGoogle(true)

    }

    private fun dibujadoDeRuta() {

        val parser = JSONDirectionParser(routeDefinition)
        val polyLineHelper = parser.obtenerRespuestaDeServicioGoogle()
    }

    //region Escuchas Eventos

    private fun mapEventListeners() {

        this.googleMap!!.setOnMapClickListener { latLng ->
            if (definicionActiva && !isMarkerExistent(latLng)) {
                addMarker(latLng, false, false)
                //componerDefinicionRutaPrimerPunto();
            }
        }

        this.googleMap!!.setOnCameraMoveListener {
            marcadorSeleccionado?.setPosition(googleMap!!.cameraPosition.target)
        }

        this.googleMap!!.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {

            }

            override fun onMarkerDrag(marker: Marker) {

            }

            override fun onMarkerDragEnd(marker: Marker) {

                var itemEncontrado = false
                var index = 0
                while (index < routeDefinition!!.colleccionMarcadores.size && !itemEncontrado) {

                    if (marker.id.compareTo((routeDefinition!!.colleccionMarcadores[index] as CustomMarker).marcador.id, ignoreCase = true) == 0) {
                        itemEncontrado = true
                    } else {
                        index++
                    }
                }

                if (itemEncontrado) {

                    routeDefinition!!.polyLineHelper
                            .removerPolilineaEntreMarcadorPivot(routeDefinition!!.colleccionMarcadores[index] as CustomMarker)

                    val parser = JSONDirectionParser(routeDefinition, routeDefinition!!.colleccionMarcadores[index] as CustomMarker)
                    val polyLineHelper = parser.obtenerRespuestaDeServicioGoogle()


                }

            }
        })
    }


    private fun escuchaBotonPosicionActual() {

        /*
        this.btnMarkerOnPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if(!isMarkerExistent(currentLatittudeLongitud)) {

                addMarker(currentLatittudeLongitud, false, false);

                componerDefinicionRutaPrimerPunto();

                definicionActiva = true;

            }
            }
        });
*/
    }

    private fun escuchaBotonMarcadorCentral() {

        /*
        this.btnCenterMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                definicionActiva = true;
                Log.d(TAG, "Marcador seleccionado: "+(marcadorSeleccionado != null));

                if (marcadorSeleccionado == null) {

                    // Marcador sobre el centro de la pantalla sin liberar
                    marcadorSeleccionado = addMarker(googleMap.getCameraPosition().target, true, false);

                }else{

                    // Marcardor seleccionado para dejar en el lugar
                    addMarker(marcadorSeleccionado.getPosition(), false, false);
                    marcadorSeleccionado = null;

                    if (isFirstMarker())
                        componerDefinicionRutaPrimerPunto();
                }

            }
        });
*/
    }

    /*
    private void listenerOnStartCancelEdition(){

        this.btnStartRoutDefinition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (definicionActiva) {

                    btnStartRoutDefinition.setImageResource(R.drawable.route);
                    btnStartRoutDefinition.setBackgroundColor(Color.GREEN);

                    btnCenterMarker.setEnabled(false);
                    btnMarkerOnPosition.setEnabled(false);
                    definicionActiva = false;
                    deleteRouteDefiniton(routeDefinition);

                }else{

                    btnStartRoutDefinition.setImageResource(R.drawable.cancel);
                    btnStartRoutDefinition.setBackgroundColor(Color.RED);

                    ((LinearLayout)findViewById(R.id.markerOptionsLayout)).setVisibility(View.VISIBLE);
                    ((LinearLayout)findViewById(R.id.routeDefinitionLayout)).setVisibility(View.GONE);

                    btnCenterMarker.setEnabled(true);
                    btnMarkerOnPosition.setEnabled(true);
                    definicionActiva = true;
                    routeDefinition =  new RouteDefinition(googleMap);
                    routeDefinition.setPolyLineHelper(new PolyLineHelper());
                    manejadorArrastreMarcadores = new ManejadorArrastreMarcadores(routeDefinition);
                }
            }
        });
    }
    */


    override fun onMarkerClick(marker: Marker): Boolean {


        return false
    }


    private fun enableMarkerSelectedOptions() {

        /* TODO
        if (!isMarkerSelectedOptionsEnabled){

        }*/

    }

    private fun listenerOnBtnSearch() {

        this.btnSearchDirection!!.setOnClickListener { searchConcidences() }
    }

    private fun isMarkerExistent(latLng: LatLng): Boolean {
        var itemFound = false
        var index = 0

        while (!itemFound && index < routeDefinition!!.colleccionMarcadores.size) {
            val customMarker = routeDefinition!!.colleccionMarcadores[index] as CustomMarker
            if (customMarker.marcador.position === latLng) {
                itemFound = true
            } else {
                index++
            }
        }

        return itemFound
    }

    private fun addMarker(latLng: LatLng?, centralMarkerForDrop: Boolean, definicionDeDistanciaInicial: Boolean): Marker {

        lastActiveMarker = MarkerOptions().position(latLng!!)
        lastActiveMarker!!.draggable(true)

        val marker = googleMap!!.addMarker(lastActiveMarker)
        val customMarker = CustomMarker(marker, if (isFirstMarker) 0 else routeDefinition!!.colleccionMarcadores.lastItem!!.enumerador)

        when (action_selected) {
            MapActivity.ICON_TYPE.LOCATION -> customMarker.marcador.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.add_place_18dp))
            MapActivity.ICON_TYPE.GASSTATION -> customMarker.marcador.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.local_gas_station_18dp))
            MapActivity.ICON_TYPE.RESTAURANT -> customMarker.marcador.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_18dp))
        }

        val isfirstItem = if (this.routeDefinition!!.colleccionMarcadores.isEmpty()) true else false
        this.routeDefinition!!.colleccionMarcadores.add(customMarker)
        if (!isfirstItem)
            dibujadoDeRuta()

        /*
        if (!definicionDeDistanciaInicial)
            googleMap.setOnMarkerClickListener(this);

        if (!centralMarkerForDrop) {

            if (!definicionDeDistanciaInicial) {

                marker.setTitle("P"+ routeDefinition.getColleccionMarcadores().size());
                marker.setTag("P"+ routeDefinition.getColleccionMarcadores().size());
                routeDefinition.getColleccionMarcadores().add(customMarker);

                if(routeDefinition.getColleccionMarcadores().size() > 1)
                    dibujadoDeRuta();

            }else{

                routeDefinitionAuxiliar.getColleccionMarcadores().add(customMarker);

                if(routeDefinitionAuxiliar.getColleccionMarcadores().size() > 1)
                    dibujadoDistanciaPrimerPuntoAposicionActual();
            }
        }
*/

        return marker

    }

    private fun deleteRouteDefiniton(routeDefinition: RouteDefinition) {

        // Remueve todos los marcadores
        routeDefinition.colleccionMarcadores.clear()

        // Remueve todas las líneas del mapa
        routeDefinition.polyLineHelper.removeAll()

    }

    private fun searchConcidences() {

        val search = DirectionSearch()
        val addresses = search.searchDirectionByText(this.txtSearchDirection!!.text.toString(), baseContext)

        if (addresses != null && !addresses.isEmpty()) {
            txtSearchDirection.setText(String.format("%s", addresses[0].getAddressLine(0)))
        }

    }

    private fun getColorDependingOnPositionNumber(number: Int): Int {
        when (number) {
            1 -> return Color.RED
            2 -> return Color.BLUE
            3 -> return Color.CYAN
            4 -> return Color.MAGENTA
            5 -> return Color.GREEN
            6 -> return Color.DKGRAY
            else -> return Color.BLACK
        }

    }

    companion object {
        val TAG = "MPACT"
    }

    //endregion

}
