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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.common.BaseCachingSnapshotParser;
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

public class PeopleListActivity extends AppCompatActivity implements Observer {

    // todo，check the distance
    public static final float MAX_DISTANCE = 0.5f; // km

    Location location1;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private DatabaseReference mdatabse;
    private List<Constructions> constructions;
    private List<ConstructionsAdapter> mtest;
    ConstructionsAdapter constructionAdapter;
    private Toolbar toolbar;
    private FirebaseRecyclerAdapter mFirebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_construction_list);
        location1 = MapsActivity.lastlocation;
        toolbar = findViewById(R.id.tb_toolbar);
        mdatabse = FirebaseDatabase.getInstance().getReference().child("People");
        recyclerView = findViewById(R.id.list1);//set recycleview
        recyclerView.setLayoutManager(mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        constructions = new ArrayList<>(); //as we can not initiate the new list, more flexiable
        mtest = new ArrayList<>();
        Query query = FirebaseDatabase.getInstance().getReference("People");
        // query.addValueEventListener(valueEventListener);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nearby People");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        DatabaseReference newsreference = FirebaseDatabase.getInstance().getReference().child("People/" + getCurrentWeekTime());
        newsreference.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext() && location1 != null) {
                        DataSnapshot next = iterator.next();
                        People model = next.getValue(People.class);
                        float dis = getDistanceValue(location1.getLongitude(), location1.getLatitude(),
                                Double.parseDouble(model.getGeo().get("longitude").toString()), Double.parseDouble(model.getGeo().get("latitude").toString()));
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
        Query query1 = newsreference.orderByKey();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<People>().setQuery(query1, People.class).build();
        mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<People, PeopleViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PeopleViewHolder holder, final int position, @NonNull final People model) {

                holder.cardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MapsActivity.mMarkerClickInfo = model;
                        startActivity(new Intent(PeopleListActivity.this, DetailActivity.class));
                    }
                });

                if (location1 != null) {
                    holder.distance.setText(getDistance(location1.getLongitude(), location1.getLatitude(),
                            Double.parseDouble(model.getGeo().get("longitude").toString()), Double.parseDouble(model.getGeo().get("latitude").toString())));
                } else {
                    holder.distance.setText("--.-- Km");
                }
                holder.address.setText(model.getLocation());
                Integer integer = model.getVolume().get(getCurrentHour());
                if (integer != null) {
                    holder.volume.setText(integer.intValue() + "");
                } else {
                    holder.volume.setText("0");
                }

            }

            @NonNull
            @Override
            public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.people_list, parent, false);
                return new PeopleViewHolder(view);
            }
        };

        recyclerView.setAdapter(mFirebaseRecyclerAdapter);
        LocationNotifyManager.INSTANCE.addObserver(this);
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

    class PeopleViewHolder extends RecyclerView.ViewHolder {

        private TextView volume;
        private ImageView cimage;
        private TextView address;
        private TextView distance;
        private CardView cardview;

        public PeopleViewHolder(View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            volume = itemView.findViewById(R.id.volume);
            distance = itemView.findViewById(R.id.distance);
            cimage = itemView.findViewById(R.id.cimage);
            cardview = itemView.findViewById(R.id.cardview1);

        }
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

    private String sortType;
    private String sortDirection;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.collection){

        }else {
            final String items[] = {"Name", "Distance", "Volume", "Random"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sorting Way");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sortType = items[which];
                    if (which == 3) {
                        sort();
                        dialog.dismiss();
                    } else {
                        final String items[] = {"Positive Sequence", "Reverse Order"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(PeopleListActivity.this);
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
        final BaseCachingSnapshotParser<DataSnapshot, People> parser = new CachingSnapshotParser<>(new ClassSnapshotParser<>(People.class));
        if (mFirebaseRecyclerAdapter == null) {
            return;
        }
        FirebaseArray<People> s = (FirebaseArray<People>) mFirebaseRecyclerAdapter.getSnapshots();
        ObservableSnapshotArray snapshots1 = mFirebaseRecyclerAdapter.getSnapshots();
        List<People> cs = new ArrayList<>();
        if (s == null || snapshots1 == null) {
            return;
        }
        for (int i = 0; i < snapshots1.size(); i++) {
            DataSnapshot snapshot = (DataSnapshot) snapshots1.getSnapshot(i);
            People c = parser.parseSnapshot(snapshot);
            if (!cs.contains(c)) {
                cs.add(c);
            }
        }
        switch (sortType) {
            case "Name":
                Collections.sort(cs, new Comparator<People>() {
                    @Override
                    public int compare(People o, People t1) {
                        updateInfo(o);
                        updateInfo(t1);
                        if (sortDirection.equalsIgnoreCase("Reverse order")) {
                            return t1.getLocation().compareTo(o.getLocation());
                        } else {
                            return o.getLocation().compareTo(t1.getLocation());
                        }
                    }
                });
                break;
            case "Distance":
                if (location1 == null) {
                    Toast.makeText(this, "The current location information is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Collections.sort(cs, new Comparator<People>() {
                    @Override
                    public int compare(People o, People t1) {
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
            case "Volume":
                Collections.sort(cs, new Comparator<People>() {
                    @Override
                    public int compare(People o, People t1) {
                        updateInfo(o);
                        updateInfo(t1);
                        Integer integer1 = o.getVolume().get(getCurrentHour());
                        Integer integer2 = t1.getVolume().get(getCurrentHour());
                        if (sortDirection.equalsIgnoreCase("Reverse order")) {
                            return integer2.compareTo(integer1);
                        } else {
                            return integer1.compareTo(integer2);
                        }
                    }
                });
                break;
            case "Random":
                Collections.shuffle(cs);
                break;
        }
        recyclerView.setAdapter(new PeoplesAdapter(cs));
    }

    private void updateInfo(People people) {
        float dis = getDistanceValue(location1.getLongitude(), location1.getLatitude(),
                Double.parseDouble(people.getGeo().get("longitude").toString()), Double.parseDouble(people.getGeo().get("latitude").toString()));
        String diss = getDistance(location1.getLongitude(), location1.getLatitude(),
                Double.parseDouble(people.getGeo().get("longitude").toString()), Double.parseDouble(people.getGeo().get("latitude").toString()));
        people.setDistanceValue(dis);
        people.setDistanceTag(diss);
    }

}
