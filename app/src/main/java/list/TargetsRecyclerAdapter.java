package list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.List;

import db.FetchTargetsTaskClient;
import ru.ifmo.android_2015.marketmonitor.R;
import target.Target;

/**
 * Created by novik on 05.11.15.
 */
public class TargetsRecyclerAdapter extends RecyclerView.Adapter<TargetsRecyclerAdapter.TargetViewHolder>
        implements View.OnClickListener, FetchTargetsTaskClient {

    private final LayoutInflater layoutInflater;
    private SelectedListener<Target> targetSelectedListener;
    private List<Target> targets;

    public TargetsRecyclerAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public void setSelectListener(SelectedListener<Target> listener) {
        targetSelectedListener = listener;
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
        if (target != null && targetSelectedListener != null) {
            targetSelectedListener.onSelected(target);
        }
    }

    @Override
    public void targetsAreReady(List<Target> targets) {
        this.targets = targets;
    }

    static class TargetViewHolder extends RecyclerView.ViewHolder {
        final TextView targetNameView;

        public TargetViewHolder(View itemView) {
            super(itemView);

            targetNameView = (TextView) itemView.findViewById(R.id.target_name);
        }
    }
}
