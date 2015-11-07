package ru.ifmo.android_2015.marketmonitor;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;

import db.MarketMonitorDBHelper;
import list.CategoriesRecyclerAdapter;
import list.RecyclerDividerDecorator;
import request.GetCategoriesTask;
import request.GetCategoriesTaskClient;
import target.Category;
import target.Target;

public class AddTargetActivity extends AppCompatActivity
                                implements GetCategoriesTaskClient {

    private RecyclerView categoriesList;
    private TextView textTarget;
    private Button buttonAddTarget;
    private ProgressBar progressBar;

    private List<Category> categories; //model

    private GetCategoriesTask task = null;

    private CategoriesRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_target);

        //initialize handlers for UI elements
        categoriesList = (RecyclerView) findViewById(R.id.categoriesList);
        textTarget = (TextView) findViewById(R.id.textTarget);
        buttonAddTarget = (Button) findViewById(R.id.buttonAdd);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //setup the RecyclerView
        categoriesList.setLayoutManager(new LinearLayoutManager(this));
        categoriesList.addItemDecoration(new RecyclerDividerDecorator(Color.DKGRAY));
        adapter = new CategoriesRecyclerAdapter(this, categories);
        categoriesList.setAdapter(adapter);

        if (savedInstanceState != null) {
            task = (GetCategoriesTask) getLastCustomNonConfigurationInstance();
        }

        if (task == null) {
            fetchCategories();
        } else {
            task.attachClient(this);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return task;
    }

    /**
     * Executes AsyncTask to fetch the list of Category objects
     */
    private void fetchCategories() {
        task = new GetCategoriesTask(this);
        Log.d(TAG, "Began fetching categories");
        task.execute();
    }

    /**
     * Implementation of GetCategoriesTaskClient interface
     * @param categories    ArrayList of Category objects
     */
    public void categoriesAreReady(List<Category> categories) {
        Log.d(TAG, "Categories fetched, size = " + categories.size());

        this.categories = categories;

        adapter.updateDataSet(categories);
        adapter.notifyDataSetChanged();

        //fetching completed, hide progress bar
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Implementation of GetCategoriesTaskClient interface
     */
    public void downloadFailed() {
        progressBar.setVisibility(View.INVISIBLE);
        //TODO: extract text to string resources
        Toast.makeText(this, "Failed to load the categories", Toast.LENGTH_SHORT).show();
    }

    public void onAddClick(View view) {
        if (textTarget.getText().toString().isEmpty()) {
            //TODO: extarct text to string recourses
            Toast.makeText(this, "You need to specify target!", Toast.LENGTH_SHORT).show();
        } else {
            //TODO: give the new target back to the caller

            new AddTargetTask(this).execute(new Target(textTarget.getText().toString()));
        }
    }

    static class AddTargetTask extends AsyncTask<Target, Void, Void> {
        AddTargetActivity activity;

        public AddTargetTask(AddTargetActivity activity) {
            this.activity = activity;
        }

        @Override
        public Void doInBackground(Target ... params) {
            MarketMonitorDBHelper helper = new MarketMonitorDBHelper(activity);
            helper.addTarget(params[0]);

            Log.d(TAG, "New target saved");

            return null;
        }
    }

    private static final String TAG = "AddTargetActivity";
}
