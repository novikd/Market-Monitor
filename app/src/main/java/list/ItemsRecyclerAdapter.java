package list;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import ru.ifmo.android_2015.marketmonitor.R;
import target.Item;
import target.Target;

/**
 * Created by novik on 07.11.15.
 */
public class ItemsRecyclerAdapter extends RecyclerView.Adapter<ItemsRecyclerAdapter.ItemViewHolder>
        implements View.OnClickListener {
    private List<Item> mItems;

    private final LayoutInflater inflater;
    private SelectedListener<Item> listener;

    public ItemsRecyclerAdapter(Context context, List<Item> items) {
        inflater = LayoutInflater.from(context);
        setData(items);
    }

    public void setData(List<Item> items) {
        mItems = items;
    }

    public void setSelectListener(SelectedListener<Item> listener) {
        this.listener = listener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_good, parent, false);
        v.setOnClickListener(this);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item item = mItems.get(position);
        holder.itemName.setText(item.getName());
        String price = item.getPrice() + " " + item.getBanknote();
        holder.itemCost.setText(price);

        holder.itemImage.setImageBitmap(null);
        ImageLoader.getInstance().displayImage(item.getImageUrl(), holder.itemImage);
        holder.itemLayout.setTag(R.id.tag_item, item); //Если это не сделать, то NPE
        Log.d(TAG, item.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onClick(View v) {
        Item item = (Item) v.getTag(R.id.tag_item);
        Log.i("AdapterOnClick", "Item clicked: " + item.getName());
        if (item != null && listener != null) {
            listener.onSelected(item);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout itemLayout;
        final TextView itemName;
        final TextView itemCost;
        final ImageView itemImage;

        public ItemViewHolder(View itemView) {
            super(itemView);

            itemLayout = (LinearLayout) itemView.findViewById(R.id.item_layout);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemCost = (TextView) itemView.findViewById(R.id.item_descr);
            itemImage = (ImageView) itemView.findViewById(R.id.item_image);
        }
    }

    private static final String TAG = ItemsRecyclerAdapter.class.getSimpleName();
}
