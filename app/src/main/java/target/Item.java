package target;

/**
 * Created by novik on 07.11.15.
 */
public class Item {
    private String name, url, price, id, banknote;
    private long targetId; //id of the target which this item
                          // is associated with

    public Item() {}

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getPrice() {
        return price;
    }

    public String getId() {
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

    public void setPrice(String price) {
        this.price = price;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBanknote(String banknote) {
        this.banknote = banknote;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }
}
