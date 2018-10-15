package com.jiang.geo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import static com.jiang.geo.MoveFrameLayout.dp2px;

/**
 * 横向列表数据适配器
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    // 布局解析器
    private LayoutInflater inflate = null;
    // 数据集合
    private List<ConstructionsAdapter> mObjects = new ArrayList<>();

    public HomeAdapter(List<ConstructionsAdapter> objects) {
        mObjects = objects;
    }

    public HomeAdapter(LayoutInflater inflate) {
        this.inflate = inflate;
    }

    public void setObjects(List<ConstructionsAdapter> objects) {
        mObjects = objects;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 获取布局解析器
        if (inflate == null) {
            inflate = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        // 创建布局对应的holder
        MyViewHolder holder = new MyViewHolder(inflate.inflate(R.layout.item_home, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // 数据绑定，即展示图片，此处直接展示固定的测试图片
        holder.setIsRecyclable(false);


        String address = mObjects.get(position).getAddress();
        Float distance = mObjects.get(position).getDistance();

        String noiselevel = mObjects.get(position).getNoiselevel();
        holder.setAddress(address);
        holder.setType(convertTo2(distance / 1000));

        holder.setBackground(noiselevel);

    }

    @Override
    public int getItemCount() {
        // 数据数量
        return mObjects.size();
    }

    // 布局holder
    class MyViewHolder extends RecyclerView.ViewHolder {


        // 图片控件
        ImageView mImageView;
        private View mView;

        private TextView address;
        private TextView noise;
        private TextView ctype;
        RelativeLayout linearLayout;

        public MyViewHolder(View view) {
            super(view);
            mView = itemView;
        }

        public void setAddress(String message) {

            address = mView.findViewById(R.id.address1);
            address.setText(message);

        }

        public void setType(String message) {

            ctype = mView.findViewById(R.id.distance1);
            ctype.setText(message);


        }

        public void setBackground(String level) {
            mImageView = mView.findViewById(R.id.img);

            if (level.equalsIgnoreCase("Low")) {
                mImageView.setBackgroundColor(Color.rgb(102, 224, 255));

            }
            if (level.equalsIgnoreCase("Medium")) {
                mImageView.setBackgroundColor(Color.rgb(117, 102, 255));

            }
            if (level.equalsIgnoreCase("High")) {
                mImageView.setBackgroundColor(Color.rgb(255, 102, 193));

            }
        }


    }

    public String convertTo2(Float distance) {


        String distance1 = String.format("%.2f", distance);
        return distance1 + "Km";

    }
}