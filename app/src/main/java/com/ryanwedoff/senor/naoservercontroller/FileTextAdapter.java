package com.ryanwedoff.senor.naoservercontroller;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class FileTextAdapter extends RecyclerView.Adapter<FileTextAdapter.ViewHolder> {
    private final ArrayList<CharSequence> mDataset;

    //Constructor
    public FileTextAdapter(ArrayList<CharSequence> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FileTextAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_input_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder((CardView) v);
        v.setId((int) (Math.random() * 1000));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FileTextAdapter.ViewHolder holder, int position) {
        View view = holder.mCardView.getRootView();
        TextView lineView = (TextView) view.findViewById(R.id.line_num);
        String num = Integer.toString(position) + "  ";
        lineView.setText(num);
        TextView textView = (TextView)view.findViewById(R.id.file_text_view);
        textView.setText(mDataset.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final CardView mCardView;

        public ViewHolder(CardView v) {
            super(v);
            mCardView = v;
        }
    }

}

