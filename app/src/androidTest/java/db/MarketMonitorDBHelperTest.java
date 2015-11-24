package db;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import target.Item;
import target.Target;

/**
 * Created by ruslanthakohov on 10/11/15.
 */
public class MarketMonitorDBHelperTest extends AndroidTestCase {

    //test data set
    private Target[] targets = {
            new Target("iPhone 5s"),
            new Target("iPad Mini 3"),
            new Target("Macbook Pro")
    };

    private List<Target> targetsList;

    private MarketMonitorDBHelper helper;

    public void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        helper = new MarketMonitorDBHelper(context);
        targetsList = new ArrayList<>(Arrays.asList(targets));
    }

    public void tearDown() throws Exception {
        helper.close();
        super.tearDown();
    }

    public void testGetAllTargets() {
        //add targets to the db
        for (Target target: targetsList) {
            helper.addTarget(target);
        }

        //get targets from the db
        List<Target> fetchedTargets = helper.getAllTargets();

        assertEquals("Number of fetched targets should be equal to"
                + " number of added", targetsList.size(), fetchedTargets.size());

        assertEquals("Fetched targets" +
                " should be same as added", true, targetsList.containsAll(fetchedTargets));

    }

    public void testDeleteTarget() {
        for (Target target: targetsList) {
            helper.addTarget(target);
        }

        //delete targets one by one
        while (!targetsList.isEmpty()) {
            helper.deleteTarget(targetsList.get(0));
            targetsList.remove(0);
            List<Target> remainingTargets = helper.getAllTargets();
            assertEquals("Sizes after deletion should be equal", targetsList, remainingTargets);
            assertEquals("Data sets after deletion" +
                    "should contain same elements", true, targetsList.containsAll(remainingTargets));
        }
    }

    public void testGetItemsForTarget() {
        for (Target target: targetsList) {
            helper.addTarget(target);
        }

        //add items for targets
        List<List<Item>> itemsForTarget = new ArrayList<>();
        for (int i = 0; i < targetsList.size(); i++) {
            List<Item> items = new ArrayList<>();

            for (int j = 0; j < ITEMS_FOR_TARGET; j++) {
                Item item = makeItem(targetsList.get(i).getName() + "_item" + String .valueOf(j));
                item.setId(String.valueOf(i) + String.valueOf(j));
                helper.addItemsForTarget(targetsList.get(i), item);
                items.add(item);
            }

            itemsForTarget.add(items);
        }

        //fetch items for each target
        for (int i = 0; i < targetsList.size(); i++) {
            List<Item> fetchedItems = helper.getItemsForTarget(targetsList.get(i));
            assertEquals(itemsForTarget.get(i).size(), fetchedItems.size());
            assertEquals(true, fetchedItems.containsAll(itemsForTarget.get(i)));
        }

    }

    public void testDeleteItemsForTarget() {
        for (Target target: targetsList) {
            helper.addTarget(target);
        }

        for (int j = 0; j < ITEMS_FOR_TARGET; j++) {
            Item item = makeItem(targetsList.get(0).getName() + "_item" + String .valueOf(j));
            item.setId(String.valueOf(0) + String.valueOf(j));
            helper.addItemsForTarget(targetsList.get(0), item);
        }

        helper.deleteItemsForTarget(targetsList.get(0));

        assertEquals(0, helper.getItemsForTarget(targetsList.get(0)).size());

    }

    private static Item makeItem(String itemName) {
        Item item = new Item();
        item.setName(itemName);
        return item;
    }

    private static final int ITEMS_FOR_TARGET = 10;

    private static final String TAG = "DBHelperTest";
}