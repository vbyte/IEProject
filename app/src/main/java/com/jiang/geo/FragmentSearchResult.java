package com.jiang.geo;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * fragment result
 */
public class FragmentSearchResult extends Fragment {

    private View mView;
    private RecyclerView list1;
    public static List<Object> mData = new ArrayList<>();
    private CollectionAdapter mFirebaseRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.layout_fragment_result, container, false); // 获取view
        list1 = mView.findViewById(R.id.list1);
        list1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        list1.setAdapter(mFirebaseRecyclerAdapter = new CollectionAdapter());
        return mView;
    }

    private void hideListAndShowTip() {
        list1.setVisibility(View.GONE);
    }

    public void showResult(List<Constructions> cons, List<People> peos) {
        mData.clear();
        mData.addAll(cons);
        mData.addAll(peos);
        mFirebaseRecyclerAdapter.setObjects(mData);
    }

}
