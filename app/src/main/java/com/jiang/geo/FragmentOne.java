package com.jiang.geo;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

/**
 * fragment1
 */
public class FragmentOne extends Fragment {

    private View mView;
    private RecyclerView recycler1;
    private RecyclerView recycler2;
    private List<News> mnews;
    //    private List<Constructions> mconstruction;
//    private HashMap<String,String> ll;
//    private List<ConstructionsAdapter>madapter;
    private DatabaseReference mdatabse;
    //    private HashMap<String,Float> lol;
    Location location1;
    //ADDBUTTON
    private CardView addbutton;
    //VIEWBUTTON
    private CardView viewbutton;

    private RecyclerView nview;
    private FirebaseRecyclerAdapter<News, FragmentOne.NewsViewHolder> mnewsAdapter;
    private Context mcontex;


    private NewsAdapter adapter1;

    // 模拟数据
    private List<Object> data1, data2;

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.layout_fragment_one, container, false);
        mdatabse = FirebaseDatabase.getInstance().getReference().child("News");
        mdatabse.keepSynced(true);
        mcontex = container.getContext();
        nview = mView.findViewById(R.id.recycler_1);
        addbutton = mView.findViewById(R.id.Addbookbutton);
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenListActivity();
            }
        });
        viewbutton = mView.findViewById(R.id.viewbook);
        viewbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PeopleListActivity.class);
                startActivity(intent);
            }
        });
        DatabaseReference newsreference = FirebaseDatabase.getInstance().getReference().child("News");
        Query newquery = newsreference.orderByKey();
        nview.hasFixedSize();
        LinearLayoutManager mLinear = new LinearLayoutManager(mcontex,
                LinearLayoutManager.HORIZONTAL, false);
        nview.setLayoutManager(mLinear);
        FirebaseRecyclerOptions newsOptions = new FirebaseRecyclerOptions.Builder<News>().setQuery(newquery, News.class).build();
        mnewsAdapter = new FirebaseRecyclerAdapter<News, NewsViewHolder>(newsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull NewsViewHolder holder, final int position, @NonNull final News model) {
                holder.setTitle(model.getTitle());
                holder.setImage(getActivity().getBaseContext(), model.getImage());
                holder.setDescriion(model.getDescription());


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String url = model.getUrl();
                        Intent detail = new Intent(getActivity(), NewsInformation.class);
                        detail.putExtra("NewsID", url);


                        startActivity(detail);

                    }
                });

            }

            @NonNull
            @Override
            public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.news_row, parent, false);

                return new NewsViewHolder(view);
            }
        };
        nview.setAdapter(mnewsAdapter);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mnewsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mnewsAdapter.stopListening();


    }


    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public NewsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


        }

        public void setTitle(String year) {
            TextView post_desc = (TextView) mView.findViewById(R.id.title);
            post_desc.setText(year);
        }

        public void setImage(Context ctx, String image) {
            ImageView shoe_image = (ImageView) mView.findViewById(R.id.newsimage);
            Glide.with(ctx).load(image).into(shoe_image);
        }

        public void setDescriion(String message) {
            TextView post_desc = (TextView) mView.findViewById(R.id.description);
            post_desc.setText(message);
        }


    }

    public void OpenListActivity() {
        Intent intent = new Intent(getActivity(), ConstructionListActivity.class);
        startActivity(intent);

    }


}
