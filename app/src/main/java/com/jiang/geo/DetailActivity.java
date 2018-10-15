package com.jiang.geo;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.jiang.geo.Util.getDistance;

/**
 * Created by jinfeng on 2018/9/24.
 */
public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LatLng latLng;
    private GoogleMap mMap;
    private String name;
    private int icon;
    private Marker mMarker;

    private Toolbar mToolbar;
    private ConstraintLayout construction;
    private CardView cardview1;
    private TextView noise;
    private TextView label;
    private ImageView cimage;
    private TextView address;
    private TextView label2;
    private TextView type;
    private TextView label3;
    private TextView distance;
    private ConstraintLayout people;
    private TextView addressP;
    private TextView volume;
    private TextView distanceP;
    private ImageView cimageP;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mToolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        construction = (ConstraintLayout) findViewById(R.id.construction);
        cardview1 = (CardView) findViewById(R.id.cardview1);
        noise = (TextView) findViewById(R.id.noise);
        label = (TextView) findViewById(R.id.label);
        cimage = (ImageView) findViewById(R.id.cimage);
        address = (TextView) findViewById(R.id.address);
        label2 = (TextView) findViewById(R.id.label2);
        type = (TextView) findViewById(R.id.type);
        label3 = (TextView) findViewById(R.id.label3);
        distance = (TextView) findViewById(R.id.distance);
        people = (ConstraintLayout) findViewById(R.id.people);
        addressP = (TextView) findViewById(R.id.address_p);
        volume = (TextView) findViewById(R.id.volume);
        distanceP = (TextView) findViewById(R.id.distance_p);
        cimageP = (ImageView) findViewById(R.id.cimage_p);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Location location = MapsActivity.lastlocation;
        if (MapsActivity.mMarkerClickInfo instanceof Constructions) {
            Constructions constructions = (Constructions) MapsActivity.mMarkerClickInfo;
            double lat = Double.parseDouble(constructions.getLocation_1().get("lon").toString());
            double lng = Double.parseDouble(constructions.getLocation_1().get("lat").toString());
            name = constructions.getStreet_address();
            latLng = new LatLng(lat, lng);
            icon = R.mipmap.construction;
            people.setVisibility(View.GONE);
            construction.setVisibility(View.VISIBLE);
            if (constructions == null) return;
            Object o = constructions.getNoise().get(getCurrentTime());
            noise.setText(o == null ? "none" : o.toString());
            Glide.with(this)
                    .load(constructions.getImage())
                    .apply(new RequestOptions().centerCrop())
                    .into(cimage);
            address.setText(constructions.getStreet_address());
            type.setText(constructions.getType());
            if (location != null) {
                distance.setText(getDistance(location.getLongitude(), location.getLatitude(),
                        Double.parseDouble(constructions.getLocation_1().get("lat").toString()), Double.parseDouble(constructions.getLocation_1().get("lon").toString())));
            } else {
                distance.setText("--.-- Km");
            }
        } else {
            People p = (People) MapsActivity.mMarkerClickInfo;
            double lat = Double.parseDouble(p.getGeo().get("latitude").toString());
            double lng = Double.parseDouble(p.getGeo().get("longitude").toString());
            name = p.getLocation();
            latLng = new LatLng(lat, lng);
            icon = R.mipmap.people1;
            construction.setVisibility(View.GONE);
            people.setVisibility(View.VISIBLE);
            if (location != null) {
                distanceP.setText(getDistance(location.getLongitude(), location.getLatitude(),
                        Double.parseDouble(p.getGeo().get("longitude").toString()), Double.parseDouble(p.getGeo().get("latitude").toString())));
            } else {
                distanceP.setText("--.-- Km");
            }
            addressP.setText(p.getLocation());
            Integer integer = p.getVolume().get(getCurrentHour());
            if (integer != null) {
                volume.setText(integer.intValue() + "");
            } else {
                volume.setText("0");
            }
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(!TextUtils.isEmpty(name) ? name : "Detail Info");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        // map 对象
//        mMap = googleMap;
//        mMap.getUiSettings().setMyLocationButtonEnabled(false);
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title(name);
//        // markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapUtil.getBitmapFromRes(this, icon)));
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
//        mMarker = mMap.addMarker(markerOptions);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomBy(13));
//        // 延时300ms后进行视图缩放，避免导致视角移动失效的问题
//
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // map 对象
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(name);
        // markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapUtil.getBitmapFromRes(this, icon)));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        mMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        // 延时300ms后进行视图缩放，避免导致视角移动失效的问题
//        getWindow().getDecorView().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // 视图缩放到层级为10
//                mMap.animateCamera(CameraUpdateFactory.zoomBy(13));
//            }
//        }, 1000);
    }

    private MenuItem item;

    public List<Object> getAll() {
        List<Object> mData = new ArrayList<>();
        String data = (String) SPUtils.get(this, "data", "");
        if (TextUtils.isEmpty(data)) return mData;
        JSONArray array = JSONArray.parseArray(data);
        if (array != null && array.size() > 0) {
            for (int i = 0; i < array.size(); i++) {
                if (array.get(i).toString().indexOf("volume") != -1) {
                    JSONObject object = (JSONObject) array.get(i);
                    People p = JSON.parseObject(object.toJSONString(), People.class);
                    if (p != null) {
                        mData.add(p);
                    }
                } else {
                    JSONObject object = (JSONObject) array.get(i);
                    Constructions c = JSON.parseObject(object.toJSONString(), Constructions.class);
                    if (c != null) {
                        mData.add(c);
                    }
                }
            }
        }
        return mData;
    }

    public List<Constructions> getCons() {
        List<String> strings = new ArrayList<>();
        List<Constructions> constructions = new ArrayList<>();
        String data = (String) SPUtils.get(this, "data", "");
        if (TextUtils.isEmpty(data)) {
            return constructions;
        } else {
            JSONArray array = JSONArray.parseArray(data);
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    JSONObject o = (JSONObject) array.get(i);
                    Constructions c = JSON.parseObject(o.toJSONString(), Constructions.class);
                    if (!TextUtils.isEmpty(c.getStreet_address())) {
                        constructions.add(c);
                    }
                }
                return constructions;
            } else {
                return constructions;
            }
        }
    }

    public List<People> getPeople() {
        List<String> strings = new ArrayList<>();
        List<People> constructions = new ArrayList<>();
        String data = (String) SPUtils.get(this, "data", "");
        if (TextUtils.isEmpty(data)) {
            return constructions;
        } else {
            JSONArray array = JSONArray.parseArray(data);
            if (array != null && array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    JSONObject o = (JSONObject) array.get(i);
                    People c = JSON.parseObject(o.toJSONString(), People.class);
                    if (c.getGeo() != null) {
                        constructions.add(c);
                    }
                }
                return constructions;
            } else {
                return constructions;
            }
        }
    }

    boolean colleted = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        colleted = false;
        getMenuInflater().inflate(R.menu.collection, menu);
        item = menu.findItem(R.id.collection);
        item.setIcon(R.mipmap.collection_empty);
        if (MapsActivity.mMarkerClickInfo instanceof Constructions) {
            Constructions p = (Constructions) MapsActivity.mMarkerClickInfo;
            List<Constructions> cons = getCons();
            for (Constructions c : cons) {
                if (c.getStreet_address().equalsIgnoreCase(p.getStreet_address())) {
                    colleted = true;
                    item.setIcon(R.mipmap.collection_yes);
                }
            }
        }
        if (MapsActivity.mMarkerClickInfo instanceof People) {
            People p = (People) MapsActivity.mMarkerClickInfo;
            List<People> cons = getPeople();
            for (People c : cons) {
                if (c.getLocation().equals(p.getLocation())) {
                    colleted = true;
                    item.setIcon(R.mipmap.collection_yes);
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.collection) {
            if (MapsActivity.mMarkerClickInfo instanceof Constructions) {
                Constructions p = (Constructions) MapsActivity.mMarkerClickInfo;
                List<Object> cons = getAll();
                Constructions des = null;
                int i = 0;
                for (Object a : cons) {
                    if (a instanceof Constructions) {
                        Constructions c = (Constructions) a;
                        if (c.getStreet_address().equalsIgnoreCase(p.getStreet_address())) {
                            des = c;
                            break;
                        }
                    }
                    i++;
                }
                String data = (String) SPUtils.get(this, "data", "");
                JSONArray array = JSONArray.parseArray(data);
                if (array == null) {
                    array = new JSONArray();
                }
                if (colleted && des != null) {
                    array.remove(i);
                } else {
                    array.add(JSONObject.toJSON(MapsActivity.mMarkerClickInfo));
                }
                SPUtils.put(this, "data", array.toJSONString());
            }
            if (MapsActivity.mMarkerClickInfo instanceof People) {
                People p = (People) MapsActivity.mMarkerClickInfo;
                List<Object> cons = getAll();
                People des = null;
                int i = 0;
                for (Object a : cons) {
                    if(a instanceof People){
                        People c = (People) a;
                        if (c.getGeo().equals(p.getGeo())) {
                            des = c;
                            break;
                        }
                    }
                    i++;
                }
                String data = (String) SPUtils.get(this, "data", "");
                JSONArray array = JSONArray.parseArray(data);
                if (array == null) {
                    array = new JSONArray();
                }
                if (colleted && des != null) {
                    array.remove(i);
                } else {
                    array.add(JSONObject.toJSON(MapsActivity.mMarkerClickInfo));
                }
                SPUtils.put(this, "data", array.toJSONString());
            }
        }
        colleted = !colleted;
        if (colleted) {
            item.setIcon(R.mipmap.collection_yes);
        } else {
            item.setIcon(R.mipmap.collection_empty);
        }
        return true;
    }

    public String getCurrentHour() {
        Calendar myDate = Calendar.getInstance();
        int day1 = myDate.get(Calendar.HOUR);
        int day2 = myDate.get(Calendar.HOUR_OF_DAY);
        if (day2 == 0) {
            return "Midnight";
        }
        if (day2 == 12) {
            return "Noon";
        }
        String tag = day2 > 12 ? "pm" : "am";
        return day1 + tag;
    }

    public String getCurrentTime() {

        Calendar myDate = Calendar.getInstance();

        String hour = myDate.get(Calendar.HOUR_OF_DAY) + "clock";


        return hour;
    }

}
