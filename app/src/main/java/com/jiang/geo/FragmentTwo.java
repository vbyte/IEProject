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
 * fragment2
 */
public class FragmentTwo extends Fragment {

    private View mView;
    private RecyclerView list1;
    private List<Object> mData = new ArrayList<>();
    private TextView empty_tip;
    private CollectionAdapter mFirebaseRecyclerAdapter;
    private SwitchCompat mSwitchCompat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.layout_fragment_two, container, false); // 获取view
        list1 = mView.findViewById(R.id.list1);
        empty_tip = mView.findViewById(R.id.empty_tip);
        mSwitchCompat = mView.findViewById(R.id.switchs);
        mSwitchCompat.setChecked((Boolean) SPUtils.get(getActivity(),"status",false));
        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SPUtils.put(getActivity(),"status",b);
            }
        });
        String data = (String) SPUtils.get(getContext(), "data", "");
        if (TextUtils.isEmpty(data)) {
            hideListAndShowTip();
        } else {
            empty_tip.setVisibility(View.GONE);
            list1.setVisibility(View.VISIBLE);
            JSONArray array = JSONArray.parseArray(data);
            if (array != null && array.size() > 0) {
                list1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                list1.setAdapter(mFirebaseRecyclerAdapter = new CollectionAdapter());
                for (int i = 0;i<array.size();i++) {
                       if (array.get(i).toString().indexOf("volume") != -1) {
                        JSONObject object = (JSONObject) array.get(i);
                        People p = JSON.parseObject(object.toJSONString(), People.class);
                        if(p != null){
                            mData.add(p);
                        }
                    } else {
                        JSONObject object = (JSONObject) array.get(i);
                        Constructions c = JSON.parseObject(object.toJSONString(), Constructions.class);
                        if(c != null){
                            mData.add(c);
                        }
                    }
                }
                if(mData != null && mData.size() > 0){
                    mFirebaseRecyclerAdapter.setObjects(mData);
                }else {
                    hideListAndShowTip();
                }
            } else {
                hideListAndShowTip();
            }
        }
        return mView;
    }

    private void hideListAndShowTip() {
        list1.setVisibility(View.GONE);
        empty_tip.setVisibility(View.VISIBLE);
    }

    public void refreshData(){
        mData.clear();
        String data = (String) SPUtils.get(getContext(), "data", "");
        if (TextUtils.isEmpty(data)) {
            hideListAndShowTip();
        } else {
            empty_tip.setVisibility(View.GONE);
            list1.setVisibility(View.VISIBLE);
            JSONArray array = JSONArray.parseArray(data);
            if (array != null && array.size() > 0) {
                list1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                list1.setAdapter(mFirebaseRecyclerAdapter = new CollectionAdapter());
                for (int i = 0;i<array.size();i++) {
                    if (array.get(i).toString().indexOf("volume") != -1) {
                        JSONObject object = (JSONObject) array.get(i);
                        People p = JSON.parseObject(object.toJSONString(), People.class);
                        if(p != null){
                            mData.add(p);
                        }
                    } else {
                        JSONObject object = (JSONObject) array.get(i);
                        Constructions c = JSON.parseObject(object.toJSONString(), Constructions.class);
                        if(c != null){
                            mData.add(c);
                        }
                    }
                }
                if(mData != null && mData.size() > 0){
                    mFirebaseRecyclerAdapter.setObjects(mData);
                }else {
                    hideListAndShowTip();
                }
            } else {
                hideListAndShowTip();
            }
        }
    }

    public boolean ischeck() {
        return mSwitchCompat.isChecked();
    }
}
