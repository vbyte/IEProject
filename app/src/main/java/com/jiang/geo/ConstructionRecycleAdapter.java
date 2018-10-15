package com.jiang.geo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class ConstructionRecycleAdapter extends RecyclerView.Adapter<ConstructionRecycleAdapter.ViewHolder> {

    public static final int SPAN_COUNT_ONE = 1;
    public static final int SPAN_COUNT_THREE = 2;

    private static final int VIEW_TYPE_SMALL = 1;
    private static final int VIEW_TYPE_BIG = 2;
    private GridLayoutManager mLayoutManager;
    public List<Constructions> constructionsList;

    public Context mcontext;
    private LocationManager locationManager;
    public List<ConstructionsAdapter> mconstructionsList;

    public ConstructionRecycleAdapter(List<ConstructionsAdapter> cList, GridLayoutManager layoutManager) {

        this.mconstructionsList = cList;
        mLayoutManager = layoutManager;

    }


    @Override
    public ConstructionRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        mcontext = parent.getContext();
        if (viewType == VIEW_TYPE_BIG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.construction_list, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.construction_grid, parent, false);
        }
        return new ViewHolder(view, viewType);

    }

    @Override
    public int getItemViewType(int position) {
        int spanCount = mLayoutManager.getSpanCount();
        if (spanCount == SPAN_COUNT_ONE) {
            return VIEW_TYPE_BIG;
        } else {
            return VIEW_TYPE_SMALL;
        }
    }


    @Override
    public void onBindViewHolder(final ConstructionRecycleAdapter.ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);


        String address = mconstructionsList.get(position).getAddress();
        Float distance = mconstructionsList.get(position).getDistance();
        String noiselevel = mconstructionsList.get(position).getNoiselevel();
//        final String address = constructionsList.get(position).getStreet_address();
//        HashMap<String, Object> noise = constructionsList.get(position).getNoise();
//        String type = constructionsList.get(position).getType();
//        final String noiselevel = noise.get(getCurrentTime()).toString();
//        HashMap<String, Object> location = constructionsList.get(position).getLocation_1();
//        final String lat = location.get("lat").toString();
//        final String lon = location.get("lon").toString();
//        double latitude = Double.valueOf(lon);
//        double longitude = Double.valueOf(lat);
//        Location location1 = new Location("pointA");
//        location1.setLatitude(latitude);
//        location1.setLongitude(longitude);
        holder.noise.setText(noiselevel);
        holder.address.setText(address);
//        holder.ctype.setText();
//        holder.setAddress(address);
//        holder.setType(distance.toString());
//        holder.setNoise(noiselevel);
//        holder.setBackground(noiselevel);
//        if(holder.getItemViewType() == VIEW_TYPE_BIG){
//
//            holder.setNoise(noiselevel);
//            holder.setAddress(address);
//            holder.setBackground(noiselevel);
//            holder.setType(type);
//
//        }else
//        {
//            holder.setNoise1(noiselevel);
//            holder.setType1(type);
//            holder.setAddress1(address);
//            holder.setBackground1(noiselevel);
//
//        }
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mcontext, MapsActivity.class);
//                intent.putExtra("lat", lat);
//                intent.putExtra("lon", lon);
//                intent.putExtra("address",address);
//                intent.putExtra("noise",noiselevel);
//                mcontext.startActivity(intent);
//
//            }
//        });


    }


    @Override
    public int getItemCount() {

        if (mconstructionsList != null) {

            return mconstructionsList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView address;
        private TextView noise;
        private TextView ctype;
        private ImageView imageView;


        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            mView = itemView;
            address = mView.findViewById(R.id.address);
            noise = mView.findViewById(R.id.noise);
            ctype = mView.findViewById(R.id.type);
            imageView = mView.findViewById(R.id.cimage);

        }


    }


    public String getCurrentTime() {

        Calendar myDate = Calendar.getInstance();

        String hour = myDate.get(Calendar.HOUR_OF_DAY) + "clock";


        return hour;
    }


}
