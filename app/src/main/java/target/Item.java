package target;

/**
 * Created by novik on 07.11.15.
 */
public class Item {
    private String name, url, price, id;

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
}
