package com.example.projetdevandroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment {

    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker userMarker;
    private View loadingView;
    private ActivityResultLauncher<String> permissionLauncher;
    private TextView latitude;
    private TextView longitude;
    private TextView adresse;
    private boolean isMapReady = false;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        longitude = view.findViewById(R.id.longitude);
        latitude = view.findViewById(R.id.latitude);
        adresse=view.findViewById(R.id.adresse);
        loadingView = view.findViewById(R.id.loading_map);
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }

        Configuration.getInstance().load(
                requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext())
        );

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        File cache = new File(requireContext().getCacheDir(), "osmdroid");
        Configuration.getInstance().setOsmdroidTileCache(cache);

        mapView = view.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setUseDataConnection(true);
        mapView.setTilesScaledToDpi(true);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        setupPermissionLauncher();
        checkPermissionOrStart();

        return view;
    }

    private void setupPermissionLauncher() {
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startGps();
                    }
                }
        );
    }

    private void checkPermissionOrStart() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {

            startGps();
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void startGps() {

        LocationRequest request = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                3000
        ).setMinUpdateDistanceMeters(20).build();

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                requireActivity().getMainLooper()
        );
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {

            Location location = locationResult.getLastLocation();
            if (location != null) {
                updateMap(location);

                if (!isMapReady) {
                    isMapReady = true;
                    if (loadingView != null) {
                        loadingView.setVisibility(View.GONE);
                    }
                }
            }
        }
    };

    private void updateMap(Location location) {

        GeoPoint point = new GeoPoint(
                location.getLatitude(),
                location.getLongitude()
        );

        if (userMarker == null) {
            userMarker = new Marker(mapView);
            userMarker.setTitle("ici");
            mapView.getOverlays().add(userMarker);
        }
        double lattitude_double=location.getLatitude();
        double longitude_double=location.getLongitude();
        latitude.setText("Lat: " + lattitude_double);
        longitude.setText("Long"+longitude_double);
        adresse.setText(getStreetName(lattitude_double,longitude_double));
        userMarker.setPosition(point);

        mapView.getController().setZoom(18.0);
        mapView.getController().setCenter(point);

        mapView.invalidate();
    }
    private String getStreetName(double lat, double lon) {

        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

        try {
            List<Address> addresses =
                    geocoder.getFromLocation(lat, lon, 1);

            if (addresses != null && !addresses.isEmpty()) {
                android.location.Address address = addresses.get(0);

                String number = address.getSubThoroughfare();
                String street = address.getThoroughfare();

                StringBuilder result = new StringBuilder();

                if (number != null) result.append(number).append(" ");
                if (street != null) result.append(street);

                return result.length() > 0 ? result.toString() : "Adresse inconnue";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Adresse inconnue";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        checkPermissionOrStart();
    }

    @Override
    public void onPause() {
        super.onPause();

        mapView.onPause();

        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}