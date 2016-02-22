package com.chronix.databaseparsing;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by chronix on 2/22/16.
 */
public class RecyclerListAdapter extends RecyclerView.Adapter <RecyclerListAdapter.RecyclerViewHolder> {

    public LayoutInflater inflater;
    List <RecyclerList> data = Collections.emptyList();

    public RecyclerListAdapter(Context context, List<RecyclerList> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_layout, parent, false);
        RecyclerViewHolder mRecyclerViewHolder = new RecyclerViewHolder(view);
        return mRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.mRecyclerListTextView.setText(data.get(position).brandModel);
        holder.mRecyclerListImageView.setImageResource(data.get(position).imageID);
        holder.mRecyclerListYearView.setText(data.get(position).year);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public ImageView mRecyclerListImageView;
        public TextView mRecyclerListTextView;
        public TextView mRecyclerListYearView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mRecyclerListTextView = (TextView) itemView.findViewById(R.id.recycler_list_text_brand_model);
            mRecyclerListImageView = (ImageView) itemView.findViewById(R.id.drawer_list_image);
            mRecyclerListYearView = (TextView) itemView.findViewById(R.id.recycler_list_text_year);
        }
    }
}
