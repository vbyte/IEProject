package com.jiang.geo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentAboutUs extends Fragment {

    View view;
    ImageView back;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about_us, container, false);
        back = view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() instanceof MapsActivity) {
                    MapsActivity activity = (MapsActivity) getActivity();
                    activity.hide();
                }
            }
        });
        return view;
    }
}
