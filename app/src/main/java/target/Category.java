package target;

/**
 * Created by ruslanthakohov on 07/11/15.
 */
public class Category {
    private final String name;
    private final String id;

    public Category(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
