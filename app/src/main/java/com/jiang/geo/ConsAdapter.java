package com.jiang.geo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.jiang.geo.Util.getDistance;

public class ConsAdapter extends RecyclerView.Adapter<ConsAdapter.ConstructionViewHolder> {

    // layout analysis
    private LayoutInflater inflate = null;
    public Context mcontext;
    // list
    private List<Constructions> mObjects = new ArrayList<>();

    public ConsAdapter(List<Constructions> objects) {
        mObjects = objects;
    }

    public ConsAdapter(LayoutInflater inflate) {
        this.inflate = inflate;
    }

    public void setObjects(List<Constructions> objects) {
        mObjects = objects;
        notifyDataSetChanged();
    }

    @Override
    public ConsAdapter.ConstructionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // layout
        if (inflate == null) {
            inflate = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        // relevant holder
        mcontext = parent.getContext();
        ConsAdapter.ConstructionViewHolder holder = new ConsAdapter.ConstructionViewHolder(inflate.inflate(R.layout.construction_list, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ConsAdapter.ConstructionViewHolder holder, final int position) {
        final Constructions model = mObjects.get(position);
        holder.cardview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsActivity.mMarkerClickInfo = model;
                mcontext.startActivity(new Intent(mcontext, DetailActivity.class));
            }
        });

        if (model == null) return;
        Object o = model.getNoise().get(getCurrentTime());
        holder.noise.setText(o == null ? "none" : o.toString());
        Glide.with(mcontext)
                .load(model.getImage())
                .apply(new RequestOptions().centerCrop())
                .into(holder.cimage);
        holder.address.setText(model.getStreet_address());
        holder.type.setText(model.getType());
        if (MapsActivity.lastlocation != null) {
            holder.distance.setText(getDistance(MapsActivity.lastlocation.getLongitude(), MapsActivity.lastlocation.getLatitude(),
                    Double.parseDouble(model.getLocation_1().get("lat").toString()), Double.parseDouble(model.getLocation_1().get("lon").toString())));
        } else {
            holder.distance.setText("--.-- Km");
        }


    }

    public String getCurrentTime() {

        Calendar myDate = Calendar.getInstance();

        String hour = myDate.get(Calendar.HOUR_OF_DAY) + "clock";


        return hour;
    }

    @Override
    public int getItemCount() {
        // size
        return mObjects.size();
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


}
