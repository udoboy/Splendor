package com.example.splendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splendor.R;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
    List<String> colorList;
    Context context;
    ColorClickedListener colorClickedListener;

    public ColorAdapter(List<String> colorList, Context context, ColorClickedListener colorClickedListener) {
        this.colorList = colorList;
        this.context = context;
        this.colorClickedListener = colorClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.solid_color_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
      holder.colorLayout.setBackgroundColor(Color.parseColor(colorList.get(position)));
      holder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              colorClickedListener.onColorClicked(colorList.get(position));
          }
      });
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout colorLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorLayout = itemView.findViewById(R.id.colorListParent);
        }
    }
}
