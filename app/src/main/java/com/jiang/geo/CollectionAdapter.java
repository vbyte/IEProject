package com.jiang.geo;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.jiang.geo.Util.getDistance;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.MyViewHolder> {

    // layout analysis
    private LayoutInflater inflate = null;
    public Context mcontext;
    // list
    private List<Object> mObjects = new ArrayList<>();

    public CollectionAdapter() {
    }

    public CollectionAdapter(List<Object> objects) {
        mObjects = objects;
    }

    public CollectionAdapter(LayoutInflater inflate) {
        this.inflate = inflate;
    }

    public void setObjects(List<Object> objects) {
        mObjects = objects;
        notifyDataSetChanged();
    }

    @Override
    public CollectionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get layout
        if (inflate == null) {
            inflate = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        // create holder
        mcontext = parent.getContext();
        CollectionAdapter.MyViewHolder holder = new CollectionAdapter.MyViewHolder(inflate.inflate(R.layout.rows_collection, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final CollectionAdapter.MyViewHolder holder, final int position) {
        if (mObjects.get(position) instanceof Constructions) {
            final Constructions constructions = (Constructions) mObjects.get(position);
            holder.people.setVisibility(View.GONE);
            holder.construction.setVisibility(View.VISIBLE);
            if (constructions == null) return;
            Object o = constructions.getNoise().get(getCurrentTime());
            holder.noise.setText(o == null ? "none" : o.toString());
            Glide.with(holder.cardview1.getContext())
                    .load(constructions.getImage())
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.cimage);
            holder.address.setText(constructions.getStreet_address());
            holder.type.setText(constructions.getType());
            if (MapsActivity.lastlocation != null) {
                holder.distance.setText(getDistance(MapsActivity.lastlocation.getLongitude(), MapsActivity.lastlocation.getLatitude(),
                        Double.parseDouble(constructions.getLocation_1().get("lat").toString()), Double.parseDouble(constructions.getLocation_1().get("lon").toString())));
            } else {
                holder.distance.setText("--.-- Km");
            }
            holder.cardview1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MapsActivity.mMarkerClickInfo = constructions;
                    holder.address.getContext().
                            startActivity(new Intent(holder.address.getContext(), DetailActivity.class));
                }
            });
        } else {
            final People p = (People) mObjects.get(position);
            holder.construction.setVisibility(View.GONE);
            holder.people.setVisibility(View.VISIBLE);
            if (MapsActivity.lastlocation != null) {
                holder.distanceP.setText(getDistance(MapsActivity.lastlocation.getLongitude(), MapsActivity.lastlocation.getLatitude(),
                        Double.parseDouble(p.getGeo().get("longitude").toString()), Double.parseDouble(p.getGeo().get("latitude").toString())));
            } else {
                holder.distanceP.setText("--.-- Km");
            }
            holder.addressP.setText(p.getLocation());
            Integer integer = p.getVolume().get(getCurrentHour());
            if (integer != null) {
                holder.volume.setText(integer.intValue() + "");
            } else {
                holder.volume.setText("0");
            }
            holder.c12.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MapsActivity.mMarkerClickInfo = p;
                    holder.address.getContext().
                            startActivity(new Intent(holder.address.getContext(), DetailActivity.class));
                }
            });
        }
    }

    public String getCurrentTime() {

        Calendar myDate = Calendar.getInstance();

        String hour = myDate.get(Calendar.HOUR_OF_DAY) + "clock";


        return hour;
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

    @Override
    public int getItemCount() {
        // return size
        return mObjects.size();
    }

    // holder setting
    class MyViewHolder extends RecyclerView.ViewHolder {

        CardView c12;
        CardView cardview1;
        TextView noise;
        TextView label;
        ImageView cimage;
        TextView address;
        TextView label2;
        TextView type;
        TextView label3;
        TextView distance;
        ConstraintLayout people;
        ConstraintLayout construction;
        TextView addressP;
        TextView volume;
        TextView distanceP;
        ImageView cimageP;
        FrameLayout reset;

        public MyViewHolder(View view) {
            super(view);
            c12 = (CardView) view.findViewById(R.id.c12);
            cardview1 = (CardView) view.findViewById(R.id.cardview1);
            noise = (TextView) view.findViewById(R.id.noise);
            label = (TextView) view.findViewById(R.id.label);
            cimage = (ImageView) view.findViewById(R.id.cimage);
            address = (TextView) view.findViewById(R.id.address);
            label2 = (TextView) view.findViewById(R.id.label2);
            type = (TextView) view.findViewById(R.id.type);
            label3 = (TextView) view.findViewById(R.id.label3);
            distance = (TextView) view.findViewById(R.id.distance);
            people = (ConstraintLayout) view.findViewById(R.id.people);
            construction = (ConstraintLayout) view.findViewById(R.id.construction);
            addressP = (TextView) view.findViewById(R.id.address_p);
            volume = (TextView) view.findViewById(R.id.volume);
            distanceP = (TextView) view.findViewById(R.id.distance_p);
            cimageP = (ImageView) view.findViewById(R.id.cimage_p);
            reset = (FrameLayout) view.findViewById(R.id.sss);
        }
    }

}
