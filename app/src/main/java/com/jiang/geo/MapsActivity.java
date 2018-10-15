package com.jiang.geo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.ClassSnapshotParser;
import com.firebase.ui.database.FirebaseArray;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.jiang.geo.MoveFrameLayout.dp2px;
import static com.jiang.geo.Util.getDistance;
import static com.jiang.geo.Util.getDistanceValue;

/**
 * 首页，地图，marker标记展示
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener, ChangeEventListener {

    // todo，distrance range
    public static final float MAX_DISTANCE = 10.f;

    // todo，distance range
//    public static final float MAX_REMIND_DISTANCE = 5.f;
    public static final float MAX_REMIND_DISTANCE = Integer.MAX_VALUE;
    //
    private GoogleMap mMap;
    //
    private GoogleApiClient client;
    //
    private LocationRequest locationRequest;
    //
    public static Location lastlocation;
    //
    private Marker currentLocationmMarker;
    // request code
    public static final int REQUEST_LOCATION_CODE = 99;
    // latest lat lon
    double latitude, longitude;
    private TextView geo_autocomplete;

    //ADDBUTTON
    private CardView addbutton;
    //VIEWBUTTON
    private CardView viewbutton;

    private Integer THRESHOLD = 2;

    // mapview
    private View mMapView;
    // drawer
    private DrawerLayout mainDrawerLayout;
    private RelativeLayout rlContent;
    // switch
    private ImageView toggle;
    // button
    private ImageView search;
    // fragment
    private MoveFrameLayout fullScreenFragment;
    // left menu
    private NavigationView navigation;
    // control
    private ActionBarDrawerToggle toggleAct;
    // tab button
    private TextView tab1, tab2, tab3;
    // container
    private FrameLayout mTopWrapper;
    // cardview
    private CardView mCardView;
    // sshadow
    private View mShownMove;
    // three fragment
    private Fragment mFragment1, mFragment2, mFragment3, fragment;
    private CardView clear;
    private CardView location;
    private ImageView close;

    public static Object mMarkerClickInfo;

    ObservableSnapshotArray<People> mPeople;
    ObservableSnapshotArray<Constructions> mConstructions;

    MapFragment mMapFragment;
    FragmentAboutUs mFragmentAboutUs;
    FragmentSearchResult mFragmentSearchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // sey layout
        setContentView(R.layout.activity_maps);

        mainDrawerLayout = findViewById(R.id.main_drawer_layout);
        rlContent = findViewById(R.id.rl_content);
        toggle = findViewById(R.id.toggle);
        search = findViewById(R.id.search);
        fullScreenFragment = findViewById(R.id.full_screen_fragment);
        navigation = findViewById(R.id.navigation);
        mTopWrapper = findViewById(R.id.top_wrapper);
        clear = findViewById(R.id.clear);
        location = findViewById(R.id.location);
        mCardView = findViewById(R.id.card_view);
        mShownMove = findViewById(R.id.shown_move);
        close = findViewById(R.id.close);

        tab1 = findViewById(R.id.tab_1);
        tab2 = findViewById(R.id.tab_2);
        tab3 = findViewById(R.id.tab_3);

        geo_autocomplete = (TextView) findViewById(R.id.geo_autocomplete);

        DatabaseReference newsreference1 = FirebaseDatabase.getInstance().getReference().child("Constructions");
        DatabaseReference newsreference2 = FirebaseDatabase.getInstance().getReference().child("People/" + getCurrentWeekTime());
        Query query1 = newsreference1.orderByKey();
        Query query2 = newsreference2.orderByKey();
        mConstructions = new FirebaseArray<>(query1, new ClassSnapshotParser<>(Constructions.class));
        mPeople = new FirebaseArray<>(query2, new ClassSnapshotParser<>(People.class));

        mConstructions.addChangeEventListener(this);
        mPeople.addChangeEventListener(this);

        geo_autocomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearchWith();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearchWith();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastlocation != null && mMap != null) {
                    LatLng latLng = new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMarker != null) {
                    mMarker.remove();
                    mMarker = null;
                    var4 = null;
                }
                if (mTags.size() != 0) {
                    for (Tag t : mTags) {
                        if (t != null && t.mMarker != null) {
                            t.mMarker.remove();
                        }
                    }
                    mTags.clear();
                }
                isSearch = false;
                geo_autocomplete.setText("");
                close.setVisibility(View.INVISIBLE);
                selectTab(1);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear.performClick();
            }
        });

        geo_autocomplete.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // fragment
        fullScreenFragment.setOnMoveListener(new MoveFrameLayout.OnMoveListener() {
            @Override
            public void move(float translationY, float overSize) {
                //
                if (overSize > 0) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mMapView.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    layoutParams.height = (int) Math.ceil(translationY);
                    mMapView.requestLayout();
                    mMapView.invalidate();
                }
                //
                if (translationY == 0) {
                    clear.setVisibility(View.GONE);
                    location.setVisibility(View.GONE);
                    //
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mCardView.setElevation(dp2px(MapsActivity.this, 2.5f));
                    }
                    //
                    mShownMove.setVisibility(View.GONE);
                    mTopWrapper.setBackgroundColor(0xffffffff);
                } else {
                    clear.setVisibility(View.VISIBLE);
                    location.setVisibility(View.VISIBLE);
                    // cardview shadow
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mCardView.setElevation(dp2px(MapsActivity.this, 5.0f));
                    }
                    // 移动fragment up fragment
                    mShownMove.setTranslationY(translationY);
                    mShownMove.setVisibility(View.VISIBLE);
                    mTopWrapper.setBackgroundColor(0x00000000);
                }
            }
        });

        // listener
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);
        toggle.setOnClickListener(this);
        search.setOnClickListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
        toggleAct = new ActionBarDrawerToggle(this, mainDrawerLayout, R.mipmap.ic_launcher, R.string.close) {
            //
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            //
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        // listener
        mainDrawerLayout.addDrawerListener(toggleAct);

        // menu item点击监听
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_about:

                        /*new AlertDialog.Builder(MapsActivity.this)
                                .setTitle("About Us")
                                .setMessage("Our project is about the development of an Android go-to app, which provides the user with a visualization of Melbourne CBD’s current construction, informing potential noise disruptions caused by the construction work, so that pedestrians can avoid the routes exposed by construction noise pollution. Our target audience for this product is parents with children age 0 to 5 who travels on foot around Melbourne CBD and want to avoid or minimize construction work's noise pollution exposure for their children. The functionalities we aim to develop for this app development project include:\n" +
                                        "\n" +
                                        "A visualization of current construction work in Melbourne CBD and noise pollution estimation produced from it\n" +
                                        "A noise meter in decibels\n" +
                                        "Hourly foot traffic estimation in Melbourne CBD\n" +
                                        "Recommendation for routes that are more likely not being exposed by loud construction work nor foot traffic\n" +
                                        "Notification for loud construction work happening across the user's route\n" +
                                        "Public toilets facilitation visualization on Melbourne CBD map") // todo your about info
                                .setPositiveButton("OK", null)
                                .show();*/

                        location.setVisibility(View.GONE);
                        clear.setVisibility(View.GONE);
                        if (mFragmentAboutUs == null) {
                            mFragmentAboutUs = new FragmentAboutUs();
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.rl_content, mFragmentAboutUs)
                                    .commitAllowingStateLoss();
                        } else {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .show(mFragmentAboutUs)
                                    .commitAllowingStateLoss();
                        }

                        break;
                    case R.id.nav_toilet:
                        location.setVisibility(View.GONE);
                        clear.setVisibility(View.GONE);
                        if (mMapFragment == null) {
                            mMapFragment = new MapFragment();
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.rl_content, mMapFragment)
                                    .commitAllowingStateLoss();
                        } else {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .show(mMapFragment)
                                    .commitAllowingStateLoss();
                        }
                        if (lastlocation != null) {
                            mMapFragment.g(new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude()));
                        }

                        break;
//                    case R.id.nav_news:
//                        if(mMapFragment != null){
//                            location.setVisibility(View.VISIBLE);
//                            clear.setVisibility(View.VISIBLE);
//                            getSupportFragmentManager()
//                                    .beginTransaction()
//                                    .hide(mMapFragment)
//                                    .commitAllowingStateLoss();
//                        }
//                        break;
                }
                mainDrawerLayout.closeDrawer(Gravity.LEFT);
                return true;
            }
        });

        // initialize
        mFragment1 = new FragmentOne();
        mFragment2 = new FragmentTwo();
        mFragment3 = new FragmentThree();
        mFragmentSearchResult = new FragmentSearchResult();
        // default
        fragment = mFragment1;

        // add fragment into activity上
        getFragmentManager().beginTransaction().add(R.id.full_screen_fragment, mFragmentSearchResult, "fragment").commitAllowingStateLoss();
        getFragmentManager().beginTransaction().add(R.id.full_screen_fragment, mFragment3, "fragment").commitAllowingStateLoss();
        getFragmentManager().beginTransaction().add(R.id.full_screen_fragment, mFragment2, "fragment").commitAllowingStateLoss();
        getFragmentManager().beginTransaction().add(R.id.full_screen_fragment, mFragment1, "fragment").commitAllowingStateLoss();

    }

    public boolean hide() {
        if (mMapFragment != null && !mMapFragment.isHidden()) {
            location.setVisibility(View.VISIBLE);
            clear.setVisibility(View.VISIBLE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(mMapFragment)
                    .commitAllowingStateLoss();
            return true;
        }
        if (mFragmentAboutUs != null && !mFragmentAboutUs.isHidden()) {
            location.setVisibility(View.VISIBLE);
            clear.setVisibility(View.VISIBLE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(mFragmentAboutUs)
                    .commitAllowingStateLoss();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(isSearch){
            clear.performClick();
            return;
        }
        if (!hide()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mConstructions.removeChangeEventListener(this);
        mPeople.removeChangeEventListener(this);
    }

    public String getCurrentWeekTime() {

        Calendar myDate = Calendar.getInstance();

        int day = myDate.get(Calendar.DAY_OF_WEEK);

        String dayTag = "";

        switch (day) {
            case 1:
                dayTag = "Sunday";
                break;
            case 2:
                dayTag = "Monday";
                break;
            case 3:
                dayTag = "Tuesday";
                break;
            case 4:
                dayTag = "Wednesday";
                break;
            case 5:
                dayTag = "Thursday";
                break;
            case 6:
                dayTag = "Friday";
                break;
            case 7:
                dayTag = "Saturday";
                break;
        }

        return dayTag;
    }

    private void startSearchWith() {
        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE).build();
        try {
            Intent var2 = (new PlaceAutocomplete.IntentBuilder(2)).setFilter(autocompleteFilter).build(MapsActivity.this);
            startActivityForResult(var2, 30421);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            //
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (client == null) {
                                    bulidGoogleApiClient(); // 创建api client
                                }
                                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                mMap.setMyLocationEnabled(true); //
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                                if (isConnected) {
                                    //
                                    locationRequest = new LocationRequest();
                                    locationRequest.setInterval(100);
                                    locationRequest.setFastestInterval(1000);
                                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                                    //
                                    LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, MapsActivity.this);
                                }
                            }
                        }, 3000L);
                    }
                } else {
                    // failed
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     * map ready
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // map object
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                boolean returnValue = false;
                if (marker != null) {
                    Object tag = marker.getTag();
                    if (tag instanceof Constructions || tag instanceof People) {
                        returnValue = true;
                        mMarkerClickInfo = tag;
                        startActivity(new Intent(MapsActivity.this, DetailActivity.class));
                    } else {
                        returnValue = false;
                        mMarkerClickInfo = null;
                    }
                }
                return returnValue;
            }
        });
        // start
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        } else {
            // check
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }
        }

    }

    // 生成google api client connect
    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect(); // connect
    }

    @Override
    public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
        if (var4 != null) {
            addMarkerForFirebaseInfo(var4.getLatLng());
        }
    }

    @Override
    public void onDataChanged() {

    }

    @Override
    public void onError(@NonNull DatabaseError databaseError) {

    }

    class Tag {
        private Object data;
        private int drawableId;
        private Marker mMarker;

        public Tag(Object data, int drawableId, Marker marker) {
            this.data = data;
            this.drawableId = drawableId;
            mMarker = marker;
        }
    }

    boolean first = true;
    List<Tag> mTags = new ArrayList<>();
    Place var4;

    @Override
    public void onActivityResult(int var1, int var2, Intent var3) {
        if (var1 == 30421) {
            if (var2 == -1) {
                var4 = PlaceAutocomplete.getPlace(this, var3);
                if (fullScreenFragment.status != MoveFrameLayout.STATUS_NON_FULL) {
                    fullScreenFragment.setStatus(MoveFrameLayout.STATUS_NON_FULL);
                }
                if (var4 != null) {
                    if (mMarker != null) {
                        mMarker.remove();
                        mMarker = null;
                    }
                    LatLng latLng = var4.getLatLng();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(var4.getName().toString());
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mMarker = mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    addMarkerForFirebaseInfo(latLng);
                }
            } else if (var2 == 2) {
                Status var5 = PlaceAutocomplete.getStatus(this, var3);
                Toast.makeText(getApplicationContext(), var5.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(var1, var2, var3);
    }

    private void addMarkerForFirebaseInfo(LatLng latLng) {
        for (Tag t : mTags) {
            if (t.mMarker != null) {
                t.mMarker.remove();
            }
        }
        mTags.clear();
        if (mConstructions.size() == 0 && mPeople.size() == 0) {
            return;
        }
        List<Constructions> ccs = new ArrayList<>();
        List<People> pps = new ArrayList<>();
        for (int i = 0; i < mConstructions.size(); i++) {
            Constructions constructions = mConstructions.get(i);
            if (constructions == null) {
                continue;
            }
            float dis = getDistanceValue(latLng.longitude, latLng.latitude,
                    Double.parseDouble(constructions.getLocation_1().get("lat").toString()), Double.parseDouble(constructions.getLocation_1().get("lon").toString()));
            String diss = getDistance(latLng.longitude, latLng.latitude,
                    Double.parseDouble(constructions.getLocation_1().get("lat").toString()), Double.parseDouble(constructions.getLocation_1().get("lon").toString()));
            constructions.setDistanceValue(dis);
            constructions.setDistanceTag(diss);
            if (dis <= MAX_DISTANCE) {
                ccs.add(constructions);
                MarkerOptions markerOptions1 = new MarkerOptions();
                double lat = Double.parseDouble(constructions.getLocation_1().get("lon").toString());
                double lng = Double.parseDouble(constructions.getLocation_1().get("lat").toString());
                LatLng ll = new LatLng(lat, lng);
                MarkerOptions markerOptions = new MarkerOptions();
                // 将marker添加到地图上
                markerOptions.position(ll);
                markerOptions.title(constructions.getStreet_address());
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapUtil.getBitmapFromRes(this, R.mipmap.construction)));
                // 将餐厅信息放到marker到tag中，以便点击事件中获取
                Marker m = mMap.addMarker(markerOptions);
                m.setTag(constructions);
                mTags.add(new Tag(constructions, 0, m));
            }
        }
        for (int i = 0; i < mPeople.size(); i++) {
            People people = mPeople.get(i);
            if (people == null) {
                continue;
            }
            float dis = getDistanceValue(latLng.longitude, latLng.latitude,
                    Double.parseDouble(people.getGeo().get("longitude").toString()), Double.parseDouble(people.getGeo().get("latitude").toString()));
            String diss = getDistance(latLng.longitude, latLng.latitude,
                    Double.parseDouble(people.getGeo().get("longitude").toString()), Double.parseDouble(people.getGeo().get("latitude").toString()));
            people.setDistanceValue(dis);
            people.setDistanceTag(diss);
            if (dis <= MAX_DISTANCE) {
                pps.add(people);
                MarkerOptions markerOptions1 = new MarkerOptions();
                double lat = Double.parseDouble(people.getGeo().get("latitude").toString());
                double lng = Double.parseDouble(people.getGeo().get("longitude").toString());
                LatLng ll = new LatLng(lat, lng);
                MarkerOptions markerOptions = new MarkerOptions();
                // add marker into the map
                markerOptions.position(ll);
                markerOptions.title(people.getLocation());
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapUtil.getBitmapFromRes(this, R.mipmap.people1)));
                // add mao into tag and then click event
                Marker m = mMap.addMarker(markerOptions);
                m.setTag(people);
                mTags.add(new Tag(people, 0, m));
            }
        }
        if(ccs.size() > 0 || pps.size() > 0){
            isSearch = true;
            geo_autocomplete.setText(var4.getName());
            close.setVisibility(View.VISIBLE);
            mFragmentSearchResult.showResult(ccs,pps);
            selectTab(1);
        }
    }

    Marker mMarker;
    boolean isSearch;

    /**
     * 定位发生变化回调
     *
     * @param location
     */
    @Override
//    public void onLocationChanged(Location location) {
//        boolean reset = (location != null && (lastlocation == null)
//                || lastlocation.getLatitude() != location.getLatitude()
//                || lastlocation.getLongitude() != location.getLongitude());
//        // 存储记录最新位置信息
//        latitude = location.getLatitude();
//        longitude = location.getLongitude();
//        lastlocation = location;
//        Log.d("lat = ", "" + latitude);
//        // 视角移动到新的位置
//        if (first) {
//            mMap.clear();
//            // 移除之前的位置marker
//            if (currentLocationmMarker != null) {
//                currentLocationmMarker.remove();
//            }
//            FragmentTwo fragmentTwo = (FragmentTwo) mFragment2;
//            fragmentTwo.refreshData();
//            // 生成新的位置marker并添加
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(latLng);
//            markerOptions.title("Current Location");
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//            currentLocationmMarker = mMap.addMarker(markerOptions);
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));
//        }
//        LocationNotifyManager.INSTANCE.notifyObservers();
//        checkRemind();
//    }
    public void onLocationChanged(Location location) {
        boolean reset = (location != null && (lastlocation == null)
                || lastlocation.getLatitude() != location.getLatitude()
                || lastlocation.getLongitude() != location.getLongitude());
        // store the newest location
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        Log.d("lat = ", "" + latitude);
        // 视角移动到新的位置
        if (first) {
            mMap.clear();
            // 移除之前的位置marker
            if (currentLocationmMarker != null) {
                currentLocationmMarker.remove();
            }
            FragmentTwo fragmentTwo = (FragmentTwo) mFragment2;
            fragmentTwo.refreshData();
            // 生成新的位置marker并添加
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            currentLocationmMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            if (first || reset) {
                first = false;
                // delay 300ms
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /*new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mMap.animateCamera(CameraUpdateFactory.zoomBy(11));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude())));
                                    }
                                }, 300L);
                            }
                        }, 300L);*/
                        // animare
                        mMap.animateCamera(CameraUpdateFactory.zoomBy(11));
                    }
                }, 300);
            }
        }
        LocationNotifyManager.INSTANCE.notifyObservers();
        checkRemind();
    }

    public static final long remind_max_time = 1; // todo maxtime for notification
    public static final long remind_max_interl = 10000L; // todo 10seconds
    private long remindCount = 0;
    private long lastReminTime = 0;

    private void checkRemind() {
        if (lastlocation == null) {
            return;
        }
        if (mConstructions.size() == 0 && mPeople.size() == 0) {
            return;
        }
        List<Constructions> cons = getCons();
        List<People> people1 = getPeople();
        StringBuilder stringBuilder = new StringBuilder();
        LatLng latLng = new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude());
        for (int i = 0; i < mConstructions.size(); i++) {
            Constructions constructions = mConstructions.get(i);
            if (constructions == null) {
                continue;
            }
            float dis = getDistanceValue(latLng.longitude, latLng.latitude,
                    Double.parseDouble(constructions.getLocation_1().get("lat").toString()), Double.parseDouble(constructions.getLocation_1().get("lon").toString()));
            String diss = getDistance(latLng.longitude, latLng.latitude,
                    Double.parseDouble(constructions.getLocation_1().get("lat").toString()), Double.parseDouble(constructions.getLocation_1().get("lon").toString()));
            constructions.setDistanceValue(dis);
            constructions.setDistanceTag(diss);
            if (dis <= MAX_REMIND_DISTANCE) {
                for (Constructions c : cons) {
                    if (c.getStreet_address().equalsIgnoreCase(constructions.getStreet_address())) {
                        stringBuilder.append(constructions.getStreet_address() + ",");
                    }
                }
            }
        }
        for (int i = 0; i < mPeople.size(); i++) {
            People people = mPeople.get(i);
            if (people == null) {
                continue;
            }
            float dis = getDistanceValue(latLng.longitude, latLng.latitude,
                    Double.parseDouble(people.getGeo().get("longitude").toString()), Double.parseDouble(people.getGeo().get("latitude").toString()));
            String diss = getDistance(latLng.longitude, latLng.latitude,
                    Double.parseDouble(people.getGeo().get("longitude").toString()), Double.parseDouble(people.getGeo().get("latitude").toString()));
            people.setDistanceValue(dis);
            people.setDistanceTag(diss);
            if (dis <= MAX_REMIND_DISTANCE) {
                for (People c : people1) {
                    if (c.getLocation().equalsIgnoreCase(people.getLocation())) {
                        stringBuilder.append(people.getLocation() + ",");
                    }
                }
            }
        }
        boolean check = false;
        FragmentTwo fragmentTwo = (FragmentTwo) mFragment2;
        check = fragmentTwo.ischeck();
        if (!TextUtils.isEmpty(stringBuilder.toString()) && check
                && remindCount < remind_max_time && hasWindowFocus()
                && System.currentTimeMillis() - lastReminTime > remind_max_interl) {
            remindCount++;
            lastReminTime = System.currentTimeMillis();
            new AlertDialog.Builder(this)
                    .setTitle("Attention")
                    .setMessage("You are also close to location " + stringBuilder.substring(0, stringBuilder.length() - 1) + ", please note.")
                    .setPositiveButton("OK", null)
                    .show();
        }
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
                    com.alibaba.fastjson.JSONObject o = (com.alibaba.fastjson.JSONObject) array.get(i);
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
                    com.alibaba.fastjson.JSONObject o = (com.alibaba.fastjson.JSONObject) array.get(i);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        FragmentTwo f = (FragmentTwo) mFragment2;
        f.refreshData();
    }

    boolean isConnected;

    /**
     * google api client
     *
     * @param bundle
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        isConnected = true;
        // [arameter
        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // request
        LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
    }

    // check premission
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;

        } else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    /**
     * google api client failed
     *
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    /**
     * click event
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle: // menu switch
                boolean drawerOpen = mainDrawerLayout.isDrawerOpen(Gravity.LEFT);
                if (drawerOpen) {
                    mainDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mainDrawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.tab_1: // tab1
                selectTab(1);
                break;
            case R.id.tab_2: // tab2
                selectTab(2);
                break;
            case R.id.tab_3: // tab3
                selectTab(3);
                break;
            case R.id.search: // search
                break;
        }
    }

    // choose fragment
    private void selectTab(int i) {
        // default
        Fragment newFragment = null;
        tab1.setTextColor(0xff999999);
        tab2.setTextColor(0xff999999);
        tab3.setTextColor(0xff999999);
        Drawable d1 = getResources().getDrawable(R.mipmap.explore);
        Drawable d2 = getResources().getDrawable(R.mipmap.notification);
        Drawable d3 = getResources().getDrawable(R.mipmap.noise);
        d1.setBounds(0, 0, d1.getIntrinsicWidth(), d1.getIntrinsicHeight());
        d2.setBounds(0, 0, d2.getIntrinsicWidth(), d2.getIntrinsicHeight());
        d3.setBounds(0, 0, d3.getIntrinsicWidth(), d3.getIntrinsicHeight());
        tab1.setCompoundDrawables(null, d1, null, null);
        tab2.setCompoundDrawables(null, d2, null, null);
        DrawableCompat.setTint(d1, 0xff999999);
        DrawableCompat.setTint(d2, 0xff999999);
        DrawableCompat.setTint(d3, 0xff999999);
        tab3.setCompoundDrawables(null, d3, null, null);
        switch (i) {
            case 1: // first fragment
                if(isSearch){
                    newFragment = mFragmentSearchResult;
                }else {
                    newFragment = mFragment1;
                }
                DrawableCompat.setTint(d1, 0xff333333);
                tab1.setTextColor(0xff333333);
                break;
            case 2: // second fragment
                newFragment = mFragment2;
                DrawableCompat.setTint(d2, 0xff333333);
                tab2.setTextColor(0xff333333);
                break;
            case 3: // three fragment
                newFragment = mFragment3;
                DrawableCompat.setTint(d3, 0xff333333);
                tab3.setTextColor(0xff333333);
                break;
        }
        // change fragment
        if (newFragment != fragment) {
            fragment = newFragment;
            getFragmentManager().beginTransaction().hide(mFragmentSearchResult).commitAllowingStateLoss();
            getFragmentManager().beginTransaction().hide(mFragment1).commitAllowingStateLoss();
            getFragmentManager().beginTransaction().hide(mFragment2).commitAllowingStateLoss();
            getFragmentManager().beginTransaction().hide(mFragment3).commitAllowingStateLoss();
            getFragmentManager().beginTransaction().show(fragment).commitAllowingStateLoss();
            fragment = newFragment;
        }
        switch (i) {
            case 1:
                // set not full screen
                fullScreenFragment.setStatus(MoveFrameLayout.STATUS_NON_FULL);
                break;
            case 2: // second
                fullScreenFragment.setStatus(MoveFrameLayout.STATUS_NON_FULL);
                break;
            case 3: // third
                fullScreenFragment.setStatus(MoveFrameLayout.STATUS_IS_NO_MOVE);
                break;
        }

    }
}
