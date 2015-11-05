package list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.ifmo.android_2015.marketmonitor.R;
import target.Target;

/**
 * Created by novik on 05.11.15.
 */
public class TargetsRecyclerAdapter extends RecyclerView.Adapter<TargetsRecyclerAdapter.TargetViewHolder> implements View.OnClickListener {

    private final LayoutInflater layoutInflater;
    private TargetSelectedListener targetSelectedListener;

    public TargetsRecyclerAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public void setTargetSelectedListener(TargetSelectedListener listener) {
        targetSelectedListener = listener;
    }

    //TODO: finish realization of layout item_target
    @Override
    public TargetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_target, parent, false);
        view.setOnClickListener(this);
        return new TargetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TargetViewHolder holder, int position) {
        //I can't implement it, because list of targets is unreachable
    }

    //TODO: This method should return number of targets
    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void onClick(View v) {

    }

    static class TargetViewHolder extends RecyclerView.ViewHolder {
        final TextView targetNameView;

        public TargetViewHolder(View itemView) {
            super(itemView);

            targetNameView = (TextView) itemView.findViewById(R.id.target_name);
        }
    }
}
