package com.jiang.geo;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Aboutusfragment extends Fragment {

    View mView;
    Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.about_us, container, false);
         button = mView.findViewById(R.id.back);
         button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

             }
         });
        return mView;
    }


}
