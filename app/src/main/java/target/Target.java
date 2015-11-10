package target;

/**
 * Created by novik on 05.11.15.
 */
public class Target {
    private long id; //unique identifier of the target
                    //this is set for targets in the
                    //db
    public String name;

    public Target(String itemName) {
        name = itemName;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
