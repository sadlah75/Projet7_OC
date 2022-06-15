package com.sadek.go4lunch.ui.list;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sadek.go4lunch.R;
import com.sadek.go4lunch.model.Restaurant;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder>  {

    // Interface
    public interface OnClickItemRecyclerViewListener {
        void OnClickItemRecyclerView(int position);
    }


    private final List<Restaurant> mRestaurants;
    private final OnClickItemRecyclerViewListener mListener;

    public RestaurantAdapter(List<Restaurant> restaurants, OnClickItemRecyclerViewListener listener) {
        mRestaurants = restaurants;
        mListener = listener;
    }

    @NonNull
    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_restaurant_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.ViewHolder holder, int position) {

        Restaurant restaurant = mRestaurants.get(position);

        holder.getName().setText(restaurant.getName());
        holder.getAddress().setText(restaurant.getAddress());
        holder.getNumberOfWorkmates().setText("(" +restaurant.getNumberOfWorkmates()+")");


        if(restaurant.getOpeningTime() != null && restaurant.getOpeningTime().equals("Close")) {
            holder.getOpeningTime().setTextColor(Color.RED);
        }
        holder.getOpeningTime().setText(restaurant.getOpeningTime());

        if(restaurant.getImageUrl() != null) {
            Glide.with(holder.getImageUrl().getContext())
                    .load(restaurant.getImageUrl())
                    .apply(RequestOptions.centerCropTransform())
                    .into(holder.getImageUrl());
        }
        holder.getDistance().setText(restaurant.getDistance()+"m");

        if(restaurant.getRating() < 4.0)
            holder.getStar3().setVisibility(View.GONE);
        if(restaurant.getRating() < 3.0)
            holder.getStar2().setVisibility(View.GONE);
        if(restaurant.getRating() < 2.0)
            holder.getStar1().setVisibility(View.GONE);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnClickItemRecyclerView(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mRestaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name,address,openingTime,distance,numberOfWorkmates;
        private final ImageView star1,star2,star3,imageUrl;

        public TextView getName() {return name;}
        public TextView getAddress() {return address;}
        public TextView getOpeningTime() {return openingTime;}
        public TextView getDistance() {return distance;}
        public TextView getNumberOfWorkmates() {return numberOfWorkmates;}
        public ImageView getImageUrl() {return imageUrl;}
        public ImageView getStar1() {return star1;}
        public ImageView getStar2() {return star2;}
        public ImageView getStar3() {return star3;}


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.restaurant_name);
            address = (TextView) itemView.findViewById(R.id.restaurant_address);
            openingTime = (TextView) itemView.findViewById(R.id.restaurant_opening_time);
            distance = (TextView) itemView.findViewById(R.id.restaurant_distance);
            numberOfWorkmates = (TextView) itemView.findViewById(R.id.restaurant_number_of_workmates);

            star1 = (ImageView) itemView.findViewById(R.id.restaurant_star_1);
            star2 = (ImageView) itemView.findViewById(R.id.restaurant_star_2);
            star3 = (ImageView) itemView.findViewById(R.id.restaurant_star_3);
            imageUrl = (ImageView) itemView.findViewById(R.id.restaurant_imageview);
        }
    }
}
