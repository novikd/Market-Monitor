package list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.ifmo.android_2015.marketmonitor.R;
import target.Category;
import target.Target;

/**
 * Created by ruslanthakohov on 07/11/15.
 */

//TODO: add code for onCLickListener

public class CategoriesRecyclerAdapter extends RecyclerView.Adapter<CategoriesRecyclerAdapter.CategoryViewHolder> {
    private final LayoutInflater layoutInflater;
    private List<Category> categories; //model

    public CategoriesRecyclerAdapter(Context context, List<Category> categories) {
        layoutInflater = LayoutInflater.from(context);
        this.categories = categories;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.categoryNameTextView.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        if (categories != null) {
            return categories.size();
        } else {
            return 0;
        }
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        final TextView categoryNameTextView;

        public CategoryViewHolder(View itemView){
            super(itemView);

            categoryNameTextView = (TextView) itemView.findViewById(R.id.category_name);
        }
    }
}
