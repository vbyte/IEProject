package com.jiang.geo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    // 布局解析器
    private LayoutInflater inflate = null;
    public Context mcontext;
    //
    private List<News> mObjects = new ArrayList<>();

    public NewsAdapter(List<News> objects) {
        mObjects = objects;
    }

    public NewsAdapter(LayoutInflater inflate) {
        this.inflate = inflate;
    }

    public void setObjects(List<News> objects) {
        mObjects = objects;
        notifyDataSetChanged();
    }

    @Override
    public NewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //
        if (inflate == null) {
            inflate = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        //
        mcontext = parent.getContext();
        NewsAdapter.MyViewHolder holder = new NewsAdapter.MyViewHolder(inflate.inflate(R.layout.news_row, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(NewsAdapter.MyViewHolder holder, final int position) {
        //
        holder.setIsRecyclable(false);
        holder.description.setText(mObjects.get(position).getDescription());
        holder.newstitle.setText(mObjects.get(position).getTitle());
        Glide.with(mcontext).load(mObjects.get(position).getImage()).into(holder.imageView);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = mObjects.get(position).getUrl();
                Intent detail = new Intent(mcontext, NewsInformation.class);
                detail.putExtra("NewsID", url);
                mcontext.startActivity(detail);

            }
        });


    }

    @Override
    public int getItemCount() {
        //
        return mObjects.size();
    }

    //
    class MyViewHolder extends RecyclerView.ViewHolder {


        //
        ImageView imageView;
        private View mView;


        private TextView newstitle;
        private TextView description;


        public MyViewHolder(View view) {
            super(view);
            mView = itemView;
            imageView = (ImageView) mView.findViewById(R.id.newsimage);
            newstitle = (TextView) mView.findViewById(R.id.title);
            description = (TextView) mView.findViewById(R.id.description);
        }

    }


}
