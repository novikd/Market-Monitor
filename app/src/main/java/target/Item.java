package target;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by novik on 07.11.15.
 */
public class Item implements Parcelable {
    private String name, url, banknote, imageUrl;
    float price;
    long id;
    private long targetId; //id of the target which this item
                           //is associated with

    public Item() {}

    protected Item(Parcel in) {
        name = in.readString();
        url = in.readString();
        imageUrl = in.readString();
        price = in.readFloat();
        banknote = in.readString();
        id = in.readLong();
        targetId = in.readLong();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public float getPrice() {
        return price;
    }

    public long getId() {
        return id;
    }

    public String getBanknote() {
        return banknote;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setId(long id) {
        Log.d("ItemIdInitialized", "Item's id: " + String.valueOf(id));
        this.id = id;
    }

    public void setBanknote(String banknote) {
        this.banknote = banknote;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public boolean equals(Object item) {
        if (item.getClass() == Item.class) {
            return ((Item) item).getId() == id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (int) (id % (long) Integer.MAX_VALUE);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(imageUrl);
        dest.writeFloat(price);
        dest.writeString(banknote);
        dest.writeLong(id);
        dest.writeLong(targetId);
    }
}
