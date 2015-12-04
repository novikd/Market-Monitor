package list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.ifmo.android_2015.marketmonitor.R;
import target.Item;
import target.Target;

/**
 * Created by novik on 07.11.15.
 */
public class ItemsRecyclerAdapter extends RecyclerView.Adapter<ItemsRecyclerAdapter.ItemViewHolder>
        implements View.OnClickListener {
    private Item[] mItems;

    private final LayoutInflater inflater;
    private SelectedListener<Item> listener;

    public ItemsRecyclerAdapter(Context context, Item[] items) {
        inflater = LayoutInflater.from(context);
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
        holder.itemName.setText(mItems[position].getName());
        holder.itemCost.setText(mItems[position].getPrice());
    }

    @Override
    public int getItemCount() {
        return mItems.length;
    }

    @Override
    public void onClick(View v) {
        Item item = (Item) v.getTag(R.id.tag_item);
        if (item != null && listener != null) {
            listener.onSelected(item);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final TextView itemName;
        final TextView itemCost;

        public ItemViewHolder(View itemView) {
            super(itemView);

            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemCost = (TextView) itemView.findViewById(R.id.item_cost);
        }
    }
}
