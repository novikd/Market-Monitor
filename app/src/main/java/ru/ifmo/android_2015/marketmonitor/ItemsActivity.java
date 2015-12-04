package ru.ifmo.android_2015.marketmonitor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import list.ItemsRecyclerAdapter;
import target.Item;

/**
 * Created by ruslanthakohov on 03/12/15.
 */
public class ItemsActivity extends AppCompatActivity {
    Item[] items;

    private RecyclerView mItemsRecyclerView;
    private RecyclerView.Adapter mItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_items);

        Item mock = new Item();
        mock.setName("item");
        items = new Item[5];
        for (int i = 0; i < 5; i++) {
            items[i] = mock;
        }

        mItemsRecyclerView = (RecyclerView) findViewById(R.id.itemsList);
        mItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mItemsAdapter = new ItemsRecyclerAdapter(this, items);
        mItemsRecyclerView.setAdapter(mItemsAdapter);
    }
}
