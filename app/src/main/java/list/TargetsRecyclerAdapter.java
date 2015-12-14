package list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.ifmo.android_2015.marketmonitor.R;
import target.Target;

/**
 * Created by novik on 05.11.15.
 */
public class TargetsRecyclerAdapter extends RecyclerView.Adapter<TargetsRecyclerAdapter.TargetViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {

    private final LayoutInflater layoutInflater;
    private TargetClickHandler targetListener;
    private List<Target> targets;

    public TargetsRecyclerAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public void setSelectListener(TargetClickHandler listener) {
        targetListener = listener;
    }

    //TODO: finish realization of layout item_target (it seems that it is ready)
    @Override
    public TargetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_target, parent, false);
        view.setOnClickListener(this);
        return new TargetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TargetViewHolder holder, int position) {
        Target target = targets.get(position);

        holder.targetNameView.setText(target.name);
        holder.targetNameView.setTag(R.id.tag_target, target);
    }

    @Override
    public int getItemCount() {
        if (targets != null) {
            return targets.size();
        } else {
            return 0;
        }
    }

    @Override
    public void onClick(View v) {
        Target target = (Target) v.getTag(R.id.tag_target);
        if (target != null && targetListener != null) {
            targetListener.onSelected(target);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Target target = (Target) v.getTag(R.id.tag_target);
        //TODO: To show a button for deleting the Target
        if (target != null && targetListener != null) {
            targetListener.onLongClick(target);
            int position = v.getVerticalScrollbarPosition();

        }
        return false;
    }

    public void targetsAreReady(List<Target> targets) {
        this.targets = targets;
    }

    public void appendTarget(Target target) {
        if (targets == null) {
            targets = new ArrayList<>();
        }
        targets.add(target);
        notifyItemInserted(targets.size() - 1);
    }

    public void deleteTarget(int position) {
        if (position >= targets.size()) {
            Log.e("TargetAdapter", "Index out of range!");
        }
        for (int i = position; i < targets.size() - 1; ++i) {

        }
    }

    static class TargetViewHolder extends RecyclerView.ViewHolder {
        final TextView targetNameView;

        public TargetViewHolder(View itemView) {
            super(itemView);

            targetNameView = (TextView) itemView.findViewById(R.id.target_name);
        }
    }
}
