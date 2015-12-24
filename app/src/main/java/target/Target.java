package target;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Created by novik on 05.11.15.
 */
public class Target implements Parcelable {
    private long id; //unique identifier of the target
                     //this is set for targets in the
                     //db
    private boolean isLoaded;

    public String name;

    public Target(String itemName) {
        name = itemName;
    }

    protected Target(Parcel in) {
        id = in.readLong();
        name = in.readString();
        isLoaded = in.readByte() == 1;
    }

    public static final Creator<Target> CREATOR = new Creator<Target>() {
        @Override
        public Target createFromParcel(Parcel in) {
            return new Target(in);
        }

        @Override
        public Target[] newArray(int size) {
            return new Target[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    @Override
    public boolean equals(Object target) {
        if (target.getClass() == Target.class) {
            return ((Target) target).getId() == id;
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
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeByte(isLoaded ? (byte) 1 : (byte) 0);
    }
}
