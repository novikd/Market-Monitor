package ru.ifmo.android_2015.marketmonitor;

import android.graphics.Color;
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

import list.CategoriesRecyclerAdapter;
import list.RecyclerDividerDecorator;
import request.GetCategoriesTask;
import request.GetCategoriesTaskClient;
import target.Category;

public class AddTargetActivity extends AppCompatActivity
                                implements GetCategoriesTaskClient {

    private RecyclerView categoriesList;
    private TextView textTarget;
    private Button buttonAddTarget;
    private ProgressBar progressBar;

    private List<Category> categories; //model

    private GetCategoriesTask task = null;

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

        //TODO: handle configuration changes

        fetchCategories();
    }

    /**
     * Executes AsyncTask to fetch the list of Category objects
     */
    private void fetchCategories() {
        task = new GetCategoriesTask(this);
        task.execute();
    }

    /**
     * Implementation of GetCategoriesTaskClient interface
     * @param categories    ArrayList of Category objects
     */
    public void categoriesAreReady(List<Category> categories) {
        Log.d(TAG, "Categories fetched, size = " + categories.size());

        this.categories = categories;

        //init the adapter for the RecyclerView
        CategoriesRecyclerAdapter adapter = new CategoriesRecyclerAdapter(this, categories);
        categoriesList.setAdapter(adapter);

        //fetching completed, hide progress bar
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void onAddClick(View view) {
        if (textTarget.getText().toString().isEmpty()) {
            //TODO: extarct text to string recourses
            Toast.makeText(this, "You need to specify target!", Toast.LENGTH_SHORT).show();
        } else {
            //TODO: give the new target back to the caller
            //TODO: save the new target to the database
        }
    }

    private static final String TAG = "AddTargetActivity";
}
