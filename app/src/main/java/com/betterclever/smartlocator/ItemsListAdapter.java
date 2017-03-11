package com.betterclever.smartlocator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.betterclever.smartlocator.Utils.Item;

import java.io.File;
import java.util.List;

/**
 * Created by better_clever on 11/9/16.
 */
public class ItemsListAdapter extends RecyclerView.Adapter<ItemsListAdapter.ViewHolder> implements View.OnClickListener {

    private List<Item> data;
    private List<String> tags;

    public ItemsListAdapter(List<Item> data){
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_cards, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = data.get(position);

        holder.itemName.setText(item.getItemName());
        holder.lastLocation.setText(item.getLocation());
        File imgFile = new File(item.getImgPath());
        final String imagePath = item.getImgPath();
        final ImageView imageView = holder.articleImage;
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        tags = item.getTags();

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addItemsList(List<Item> list){
        data.addAll(list);
        notifyDataSetChanged();
    }

    public void clearAll (){
        data.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, lastLocation;
        public ImageView articleImage;


        public ViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            lastLocation = (TextView) itemView.findViewById(R.id.last_location);
            articleImage = (ImageView) itemView.findViewById(R.id.item_image);
        }
    }


}
