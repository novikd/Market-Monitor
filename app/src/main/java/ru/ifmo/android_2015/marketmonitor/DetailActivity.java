package ru.ifmo.android_2015.marketmonitor;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import target.Category;
import target.Item;

/**
 * Created by novik on 13.12.15.
 */
public class DetailActivity extends Activity {
    public static final String EXTRA_ITEM = "item";

    private Item item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        item = getIntent().getParcelableExtra(EXTRA_ITEM);
    }
}
