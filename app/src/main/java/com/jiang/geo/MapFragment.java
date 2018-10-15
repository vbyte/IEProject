package com.jiang.geo;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.ClassSnapshotParser;
import com.firebase.ui.database.FirebaseArray;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.jiang.geo.Util.getDistance;
import static com.jiang.geo.Util.getDistanceValue;

public class MapFragment extends Fragment implements ChangeEventListener {

    private View mapview;
    private View back;
    GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LatLng current;
    int PROXIMITY_RADIUS = 10000;
    double lat, lon;
    ObservableSnapshotArray<Toilets> mToiletsObservableSnapshotArray;

    public static final int MAX_DISTANCE = 100;

    @Nullable
    @Override
    //user google geocode and nearby api and current location which is fuseadload
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mapview = inflater.inflate(R.layout.map_fragment, container, false);
        back = mapview.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof MapsActivity) {
                    MapsActivity activity = (MapsActivity) getActivity();
                    activity.hide();
                }
            }
        });
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                requestPremession();
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

                if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            current = new LatLng(lat, lon);
                            map.addMarker(new MarkerOptions().position(current).title("Current location"));
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(current)
                                    .zoom(17).bearing(0).tilt(45).build();
                            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            DatabaseReference newsreference1 = FirebaseDatabase.getInstance().getReference().child("Toilets");
                            Query query1 = newsreference1.orderByKey();
                            mToiletsObservableSnapshotArray = new FirebaseArray<>(query1, new ClassSnapshotParser<>(Toilets.class));
                            mToiletsObservableSnapshotArray.addChangeEventListener(MapFragment.this);
                            findNearByStore(current);

                        }

                    }


                });
            }
        });

        return mapview;


    }

    public void g(final LatLng current) {
        if (map != null) {
            map.clear();
            lat = MapsActivity.lastlocation.getLatitude();
            lon = MapsActivity.lastlocation.getLongitude();
            map.addMarker(new MarkerOptions().position(current).title("Current location"));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(current)
                    .zoom(17).bearing(0).tilt(45).build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            findNearByStore(current);
        }
    }

    private void requestPremession() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, 1);
    }


    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlaceUrl.append("&keyword=" + nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=" + "AIzaSyBNhBnbi1u1q2qHJHAxMFTWxm9rw2BpU40");
        return googlePlaceUrl.toString();

    }


    private void findNearByStore(LatLng latLng) {

        if (map == null) {
            return;
        }

        /*Object dataTransfer[] = new Object[2];
        GetNearByPlaces getNearbyPlacesData = new GetNearByPlaces();

        String store = "toilet";
        String url = getUrl(lat, lon, store);
        dataTransfer[0] = map;
        dataTransfer[1] = url;
        getNearbyPlacesData.execute(dataTransfer);*/

        for (int i = 0; i < mToiletsObservableSnapshotArray.size(); i++) {
            Toilets toilets = mToiletsObservableSnapshotArray.get(i);
            if (toilets == null) {
                continue;
            }
            float dis = getDistanceValue(latLng.longitude, latLng.latitude, toilets.lon, toilets.lat);
            String diss = getDistance(latLng.longitude, latLng.latitude, toilets.lon, toilets.lat);
            toilets.setDistanceValue(dis);
            toilets.setDistanceTag(diss);
            if (dis <= MAX_DISTANCE) {
                MarkerOptions markerOptions1 = new MarkerOptions();
                LatLng ll = new LatLng(toilets.lat, toilets.lon);
                MarkerOptions markerOptions = new MarkerOptions();
                // 将marker添加到地图上
                markerOptions.position(ll);
                markerOptions.title(toilets.getName());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                map.addMarker(markerOptions);
            }
        }

    }

    @Override
    public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {

    }

    @Override
    public void onDataChanged() {
        if (MapsActivity.lastlocation != null) {
            findNearByStore(new LatLng(MapsActivity.lastlocation.getLatitude(), MapsActivity.lastlocation.getLongitude()));
        }
    }

    @Override
    public void onError(@NonNull DatabaseError databaseError) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mToiletsObservableSnapshotArray.addChangeEventListener(this);
    }

    public static class Toilets {

        public double lat;
        public double lon;
        public String name;

        public String distanceTag;
        public float distanceValue;

        public Toilets() {
        }

        public Toilets(double lat, double lon, String name) {
            this.lat = lat;
            this.lon = lon;
            this.name = name;
        }

        public Toilets(double lat, double lon, String name, String distanceTag, float distanceValue) {
            this.lat = lat;
            this.lon = lon;
            this.name = name;
            this.distanceTag = distanceTag;
            this.distanceValue = distanceValue;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDistanceTag() {
            return distanceTag;
        }

        public void setDistanceTag(String distanceTag) {
            this.distanceTag = distanceTag;
        }

        public float getDistanceValue() {
            return distanceValue;
        }

        public void setDistanceValue(float distanceValue) {
            this.distanceValue = distanceValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Toilets toilets = (Toilets) o;

            if (Double.compare(toilets.lat, lat) != 0) return false;
            if (Double.compare(toilets.lon, lon) != 0) return false;
            return name != null ? name.equals(toilets.name) : toilets.name == null;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(lat);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(lon);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }

}
