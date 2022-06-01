package com.sadek.go4lunch.ui.workmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sadek.go4lunch.R;
import com.sadek.go4lunch.model.Workmate;

import java.util.List;

public class WorkmateAdapter extends RecyclerView.Adapter<WorkmateAdapter.ViewHolder> {

    private List<Workmate> mWorkmates;
    private String result;


    public WorkmateAdapter(List<Workmate> workmates, String result) {
        this.mWorkmates = workmates;
        this.result = result;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_workmate_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Workmate workmate = mWorkmates.get(position);
        if(workmate.getChosenRestaurant() == null) {
            holder.getDescription().setText(String.format("%s has not decided yet",workmate.getUsername()));
        }else {
            if(result.equals("eating")) {
                holder.getDescription().setText(String.format("%s is eating (%s)",
                        workmate.getUsername(),workmate.getChosenRestaurant()));
            }else {
                holder.getDescription().setText(String.format("%s is joining!",
                        workmate.getUsername(),workmate.getChosenRestaurant()));
            }

        }
        if(workmate.getUrlImage() != null) {
            Glide.with(holder.getPhoto().getContext())
                    .load(workmate.getUrlImage())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.getPhoto());
        }
    }

    @Override
    public int getItemCount() {
        return mWorkmates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView photo;
        private final TextView description;

        public ImageView getPhoto() {
            return photo;
        }
        public TextView getDescription() {
            return description;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.workmate_item_photo);
            description = (TextView) itemView.findViewById(R.id.workmate_item_description);
        }
    }
}
