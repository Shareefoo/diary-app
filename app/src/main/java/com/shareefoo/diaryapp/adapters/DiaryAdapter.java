package com.shareefoo.diaryapp.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.ListDiarysQuery;
import com.shareefoo.diaryapp.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shareefoo
 */

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    // Store a member variable for the diaries
    private List<ListDiarysQuery.Item> mData = new ArrayList<>();
    private LayoutInflater mInflater;

    private OnDiaryClickListener mListener;

    // Pass in context into the constructor
    public DiaryAdapter(Context context, OnDiaryClickListener listener) {
        mInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    // Inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public DiaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View matchView = mInflater.inflate(R.layout.item_diary, parent, false);

        // Returns a new holder instance
        return new ViewHolder(matchView);
    }

    // Populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull DiaryAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        ListDiarysQuery.Item diary = mData.get(position);

        // Set item views based on views and data model
        viewHolder.tvDiaryTitle.setText(diary.title());
        viewHolder.tvDiaryTitle.setPaintFlags(viewHolder.tvDiaryTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        viewHolder.tvDiaryDescription.setText(diary.desc());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDiaryClick(diary);
            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // resets the list with a new set of data
    public void setItems(List<ListDiarysQuery.Item> items) {
        mData = items;
    }

    public interface OnDiaryClickListener {
        void onDiaryClick(ListDiarysQuery.Item item);
    }

    // Create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_diary_title)
        TextView tvDiaryTitle;

        @BindView(R.id.tv_diary_description)
        TextView tvDiaryDescription;

        ViewHolder(@NonNull View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
