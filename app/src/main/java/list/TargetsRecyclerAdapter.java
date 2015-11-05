package list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.ifmo.android_2015.marketmonitor.R;

/**
 * Created by novik on 05.11.15.
 */
public class TargetsRecyclerAdapter extends RecyclerView.Adapter<TargetsRecyclerAdapter.TargetViewHolder> {

    private final LayoutInflater layoutInflater;

    public TargetsRecyclerAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    //TODO: make a layout item_target and add target_name there
    @Override
    public TargetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_target, parent, false);
        return new TargetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TargetViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class TargetViewHolder extends RecyclerView.ViewHolder {
        final TextView targetNameView;

        public TargetViewHolder(View itemView) {
            super(itemView);
            targetNameView = (TextView) itemView.findViewById(R.id.target_name);
        }
    }
}
