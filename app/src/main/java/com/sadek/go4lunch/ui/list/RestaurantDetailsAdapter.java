package com.sadek.go4lunch.ui.list;

import android.content.Context;
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
import com.sadek.go4lunch.model.Workmate;

import java.util.List;

public class RestaurantDetailsAdapter extends RecyclerView.Adapter<RestaurantDetailsAdapter.ViewHolder> {

    private final List<Workmate> mWorkmates;
    private final Context context;

    public RestaurantDetailsAdapter(List<Workmate> mWorkmates, Context context) {
        this.mWorkmates = mWorkmates;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_workmate_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Workmate workmate = mWorkmates.get(position);
        holder.populateWorkmates(workmate);
    }

    @Override
    public int getItemCount() {
        return mWorkmates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView photo;
        private TextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.workmate_item_photo);
            description = itemView.findViewById(R.id.workmate_item_description);
        }

        void populateWorkmates(Workmate workmate) {
            if(workmate.getUrlImage() != null) {
                Glide.with(context)
                        .load(workmate.getUrlImage())
                        .apply(RequestOptions.circleCropTransform())
                        .into(photo);
            }

            if(workmate.getChosenRestaurant() != null) {
                description.setText(String.format("%s is joining!"));
            }
        }
    }
}
