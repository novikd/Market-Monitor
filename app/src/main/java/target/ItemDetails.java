package target;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by novik on 13.12.15.
 */
public class ItemDetails {
    private static final String TAG = "target.ItemDetails";

    private Item item;
    private String type, status, country, location;
    private URL viewURL;
    private String cost;
    private String banknote;

    public ItemDetails() {}

    public ItemDetails(String url, String type, String status, String country, String location) {
        try {
            viewURL  = new URL(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Parsing url error occurred: " + e);
            e.printStackTrace();
        }
        this.type = type;
        this.status = status;
        this.country = country;
        this.location = location;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public URL getViewURL() {
        return viewURL;
    }

    public void setViewURL(URL viewURL) {
        this.viewURL = viewURL;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCost() {
        return cost;
    }

    public void setBanknote(String banknote) {
        this.banknote = banknote;
    }

    public String getBanknote() {
        return banknote;
    }
}
