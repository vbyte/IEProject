package com.jiang.geo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.common.BaseCachingSnapshotParser;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.CachingSnapshotParser;
import com.firebase.ui.database.ClassSnapshotParser;
import com.firebase.ui.database.FirebaseArray;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import static android.support.constraint.Constraints.TAG;
import static com.jiang.geo.Util.getDistance;
import static com.jiang.geo.Util.getDistanceValue;

public class ConstructionListActivity extends AppCompatActivity implements Observer {

    // todo，can change the range of the distance, currently is 500m
    public static final float MAX_DISTANCE = 0.5f; // km

    Location location1;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private DatabaseReference mdatabse;
    private List<Constructions> constructions;
    private List<ConstructionsAdapter> mtest;
    private HashMap<String, Float> lol;
    private HashMap<String, String> ll;
    ConstructionsAdapter constructionAdapter;
    private Toolbar toolbar;
    private FirebaseRecyclerAdapter mFirebaseRecyclerAdapter;

    public ObservableSnapshotArray<Constructions> getData() {
        if (mFirebaseRecyclerAdapter != null) {
            return mFirebaseRecyclerAdapter.getSnapshots();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_construction_list);
        location1 = MapsActivity.lastlocation;
        toolbar = findViewById(R.id.tb_toolbar);
        mdatabse = FirebaseDatabase.getInstance().getReference().child("Constructions");
        recyclerView = findViewById(R.id.list1);//set recycleview
        recyclerView.setLayoutManager(mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        constructions = new ArrayList<>(); //as we can not initiate the new list, more flexiable
        mtest = new ArrayList<>();
        lol = new HashMap<>();
        ll = new HashMap<>();
        Query query = FirebaseDatabase.getInstance().getReference("Constructions");
        // query.addValueEventListener(valueEventListener);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nearby Constructions");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        DatabaseReference newsreference = FirebaseDatabase.getInstance().getReference().child("Constructions");
        newsreference.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren() && location1 != null) {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot next = iterator.next();
                        Constructions model = next.getValue(Constructions.class);
                        float dis = getDistanceValue(location1.getLongitude(), location1.getLatitude(),
                                Double.parseDouble(model.getLocation_1().get("lat").toString()), Double.parseDouble(model.getLocation_1().get("lon").toString()));
                        if (dis > MAX_DISTANCE) {
                            // next.getRef().removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Query query1 = newsreference.orderByChild("street_address");
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Constructions>().setQuery(query1, Constructions.class).build();
        mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Constructions, ConstructionViewHolder>(options) {

            Map<DataSnapshot, Boolean> record = new HashMap<>();

            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
                Object o = record.get(snapshot);
                if (o == null || !(Boolean) o) {
                    record.put(snapshot, true);
                    super.onChildChanged(type, snapshot, newIndex, oldIndex);
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull ConstructionViewHolder holder, final int position, @NonNull final Constructions model) {

                holder.cardview1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MapsActivity.mMarkerClickInfo = model;
                        startActivity(new Intent(ConstructionListActivity.this, DetailActivity.class));
                    }
                });

                if (model == null) return;
                Object o = model.getNoise().get(getCurrentTime());
                holder.noise.setText(o == null ? "none" : o.toString());
                Glide.with(ConstructionListActivity.this)
                        .load(model.getImage())
                        .apply(new RequestOptions().centerCrop())
                        .into(holder.cimage);
                holder.address.setText(model.getStreet_address());
                holder.type.setText(model.getType());
                if (location1 != null) {
                    holder.distance.setText(getDistance(location1.getLongitude(), location1.getLatitude(),
                            Double.parseDouble(model.getLocation_1().get("lat").toString()), Double.parseDouble(model.getLocation_1().get("lon").toString())));
                } else {
                    holder.distance.setText("--.-- Km");
                }
            }

            @NonNull
            @Override
            public ConstructionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.construction_list, parent, false);
                return new ConstructionViewHolder(view);
            }
        };
        recyclerView.setAdapter(mFirebaseRecyclerAdapter);
        LocationNotifyManager.INSTANCE.addObserver(this);
    }

    private String sortType;
    private String sortDirection;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.collection) {

        } else {
            final String items[] = {"Name", "Distance", "Random"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sorting Way");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sortType = items[which];
                    if (which == 2) {
                        sort();
                        dialog.dismiss();
                    } else {
                        final String items[] = {"Positive Sequence", "Reverse Order"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(ConstructionListActivity.this);
                        builder.setTitle("Direction Way");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sortDirection = items[which];
                                sort();
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                        dialog.dismiss();
                    }
                }
            });
            builder.create().show();
        }
        return true;
    }

    @SuppressLint("RestrictedApi")
    private void sort() {
        final BaseCachingSnapshotParser<DataSnapshot, Constructions> parser = new CachingSnapshotParser<>(new ClassSnapshotParser<>(Constructions.class));
        if (mFirebaseRecyclerAdapter == null) {
            return;
        }
        FirebaseArray<Constructions> s = (FirebaseArray<Constructions>) mFirebaseRecyclerAdapter.getSnapshots();
        ObservableSnapshotArray snapshots1 = mFirebaseRecyclerAdapter.getSnapshots();
        List<Constructions> cs = new ArrayList<>();
        if (s == null || snapshots1 == null) {
            return;
        }
        for (int i = 0; i < snapshots1.size(); i++) {
            DataSnapshot snapshot = (DataSnapshot) snapshots1.getSnapshot(i);
            Constructions c = parser.parseSnapshot(snapshot);
            if (!cs.contains(c)) {
                cs.add(c);
            }
        }
        switch (sortType) {
            case "Name":
                Collections.sort(cs, new Comparator<Constructions>() {
                    @Override
                    public int compare(Constructions o, Constructions t1) {
                        updateInfo(o);
                        updateInfo(t1);
                        if (sortDirection.equalsIgnoreCase("Reverse order")) {
                            return t1.getStreet_address().compareTo(o.getStreet_address());
                        } else {
                            return o.getStreet_address().compareTo(t1.getStreet_address());
                        }
                    }
                });
                break;
            case "Distance":
                if (location1 == null) {
                    Toast.makeText(this, "The current location information is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Collections.sort(cs, new Comparator<Constructions>() {
                    @Override
                    public int compare(Constructions o, Constructions t1) {
                        updateInfo(o);
                        updateInfo(t1);
                        Float d1 = o.getDistanceValue();
                        Float d2 = t1.getDistanceValue();
                        if (sortDirection.equalsIgnoreCase("Reverse order")) {
                            return d2.compareTo(d1);
                        } else {
                            return d1.compareTo(d2);
                        }
                    }
                });
                break;
            case "Random":
                Collections.shuffle(cs);
                break;
        }
        recyclerView.setAdapter(new ConsAdapter(cs));
    }

    private void updateInfo(Constructions constructions) {
        float dis = getDistanceValue(location1.getLongitude(), location1.getLatitude(),
                Double.parseDouble(constructions.getLocation_1().get("lat").toString()), Double.parseDouble(constructions.getLocation_1().get("lon").toString()));
        String diss = getDistance(location1.getLongitude(), location1.getLatitude(),
                Double.parseDouble(constructions.getLocation_1().get("lat").toString()), Double.parseDouble(constructions.getLocation_1().get("lon").toString()));
        constructions.setDistanceValue(dis);
        constructions.setDistanceTag(diss);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRecyclerAdapter.stopListening();


    }

    @Override
    public void update(Observable observable, Object o) {
        this.location1 = MapsActivity.lastlocation;
        mFirebaseRecyclerAdapter.notifyDataSetChanged();
    }

    class ConstructionViewHolder extends RecyclerView.ViewHolder {

        private CardView cardview1;
        private TextView noise;
        private TextView label;
        private ImageView cimage;
        private TextView address;
        private TextView label2;
        private TextView type;
        private TextView distance;

        public ConstructionViewHolder(View itemView) {
            super(itemView);
            cardview1 = itemView.findViewById(R.id.cardview1);
            noise = itemView.findViewById(R.id.noise);
            label = itemView.
                    findViewById(R.id.label);
            cimage = itemView.findViewById(R.id.cimage);
            address = itemView.findViewById(R.id.address);
            label2 = itemView.findViewById(R.id.label2);
            type = itemView.findViewById(R.id.type);
            distance = itemView.findViewById(R.id.distance);

        }
    }


    ValueEventListener valueEventListener = new ValueEventListener() {


        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            constructions.clear();


            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Constructions mconstructions = snapshot.getValue(Constructions.class);
                    String address = mconstructions.getStreet_address();

                    HashMap<String, Object> noise = mconstructions.getNoise();
                    String noiselevel = noise.get(getCurrentTime()).toString();
                    String lat1 = mconstructions.getLocation_1().get("lat").toString();
                    String lon2 = mconstructions.getLocation_1().get("lon").toString();
                    double endlat = Double.valueOf(lat1);
                    double endlon = Double.valueOf(lon2);
                    Location location = new Location("pointA");
                    location.setLatitude(endlon);
                    location.setLongitude(endlat);
                    float currentdistance = 0.f;
                    if (location1 != null) {
                        currentdistance = location1.distanceTo(location);
                    }
                    constructions.add(mconstructions);
                    lol.put(address, currentdistance);
                    ll.put(address, noiselevel);


                }
                Log.d(TAG, "onDataChange: " + constructions.size());

                Log.d(TAG, "onDataChange: " + lol.size());
                Log.d(TAG, "onDataChange: " + ll.size());
                List<Map.Entry<String, Float>> sss = new LinkedList<Map.Entry<String, Float>>(lol.entrySet());
                List<Map.Entry<String, String>> kkk = new LinkedList<Map.Entry<String, String>>(ll.entrySet());
                Collections.sort(sss, new Comparator<Map.Entry<String, Float>>() {
                    @Override
                    public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                        return o1.getValue().compareTo(o2.getValue());
                    }
                });
                String noiselevel = null;
                for (Map.Entry<String, Float> item : sss) {

                    String address = item.getKey();
                    Float distance = item.getValue();
                    for (Map.Entry<String, String> gg : kkk) {

                        if (address.equalsIgnoreCase(gg.getKey())) {
                            noiselevel = gg.getValue();
                        }

                    }
                    constructionAdapter = new ConstructionsAdapter(address, distance, noiselevel);
                    mtest.add(constructionAdapter);
                }

//


                recyclerView.setAdapter(mFirebaseRecyclerAdapter);
                recyclerView.setLayoutManager(mLinearLayoutManager);
                mFirebaseRecyclerAdapter.notifyDataSetChanged();


            }


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    public String getCurrentTime() {

        Calendar myDate = Calendar.getInstance();

        String hour = myDate.get(Calendar.HOUR_OF_DAY) + "clock";


        return hour;
    }
}
