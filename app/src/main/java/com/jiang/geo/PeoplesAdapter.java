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

public class PeoplesAdapter extends RecyclerView.Adapter<PeoplesAdapter.PeopleViewHolder> {

    //
    private LayoutInflater inflate = null;
    public Context mcontext;
    //
    private List<People> mObjects = new ArrayList<>();

    public PeoplesAdapter(List<People> objects) {
        mObjects = objects;
    }

    public PeoplesAdapter(LayoutInflater inflate) {
        this.inflate = inflate;
    }

    public void setObjects(List<People> objects) {
        mObjects = objects;
        notifyDataSetChanged();
    }

    @Override
    public PeoplesAdapter.PeopleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //
        if (inflate == null) {
            inflate = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        //
        mcontext = parent.getContext();
        PeoplesAdapter.PeopleViewHolder holder = new PeoplesAdapter.PeopleViewHolder(inflate.inflate(R.layout.people_list, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(PeoplesAdapter.PeopleViewHolder holder, final int position) {
        final People model = mObjects.get(position);
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsActivity.mMarkerClickInfo = model;
                mcontext.startActivity(new Intent(mcontext, DetailActivity.class));
            }
        });

        if (MapsActivity.lastlocation != null) {
            holder.distance.setText(getDistance(MapsActivity.lastlocation.getLongitude(), MapsActivity.lastlocation.getLatitude(),
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

    public String getCurrentTime() {

        Calendar myDate = Calendar.getInstance();

        String hour = myDate.get(Calendar.HOUR_OF_DAY) + "clock";


        return hour;
    }

    @Override
    public int getItemCount() {
        //
        return mObjects.size();
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

}
