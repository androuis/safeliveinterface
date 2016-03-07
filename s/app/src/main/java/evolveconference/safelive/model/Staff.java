package evolveconference.safelive.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by abacalu on 2/18/2016.
 */
public class Staff implements Parcelable {
    @JsonProperty("staffid")
    public int id = 0;
    @JsonProperty("stafffirstname")
    public String firstName = "";
    @JsonProperty("stafflastname")
    public String lastName = "";
    @JsonProperty("staffphoto")
    public String photo = "";
    @JsonProperty("staffposition")
    public String position = "";
    @JsonProperty("nursinghomeid")
    public int nursinghomeid = 0;

    public Staff() {}

    protected Staff(Parcel in) {
        id = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        photo = in.readString();
        position = in.readString();
        nursinghomeid = in.readInt();
    }

    public static final Creator<Staff> CREATOR = new Creator<Staff>() {
        @Override
        public Staff createFromParcel(Parcel in) {
            return new Staff(in);
        }

        @Override
        public Staff[] newArray(int size) {
            return new Staff[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(photo);
        dest.writeString(position);
        dest.writeInt(nursinghomeid);
    }
}
