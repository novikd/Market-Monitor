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
public class ItemsRecyclerAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private final LayoutInflater inflater;
    private SelectedListener<Item> listener;

    public ItemsRecyclerAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setSelectListener(SelectedListener<Item> listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_good, parent, false);
        v.setOnClickListener(this);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
