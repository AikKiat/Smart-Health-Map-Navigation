package com.example.smarthealth.MedicalCentreFinder.MapPageNavigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;

import java.util.HashMap;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private final List<HashMap<String, String>> mData;

    public List<HashMap<String,String>> getmData()
    {
        return mData;
    }
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<HashMap<String, String>> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Just one particular place
        HashMap<String, String> aParticularPlaceDetail = (HashMap<String, String>) mData.get(position);
        String formattedAddress = aParticularPlaceDetail.get("Address");
        String latitude = aParticularPlaceDetail.get("Latitude");
        String longitude = aParticularPlaceDetail.get("Longitude");
        String placeName = aParticularPlaceDetail.get("Place Name");

        //Assign the Text value UI fields in the xml elements of recycler_row.xml, with these variable values, derived from our search results!!!!
        holder.placeNameTextView.setText(placeName);
        holder.formattedAddressTextView.setText(formattedAddress);
        holder.latlngTextView.setText("Latutude: "+ latitude + "Longitude: " + longitude);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData != null? mData.size():0;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //creating variables to reference the UI .xml Text view elements in recyclerview_row.xml!
        TextView placeNameTextView;
        TextView formattedAddressTextView;

        TextView latlngTextView;

        ViewHolder(View itemView) {
            super(itemView);
            placeNameTextView = itemView.findViewById(R.id.placeName);
            formattedAddressTextView = itemView.findViewById(R.id.formattedAddress);
            latlngTextView = itemView.findViewById(R.id.latlng);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    HashMap<String, String> getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}