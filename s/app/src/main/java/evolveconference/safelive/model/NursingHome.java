package evolveconference.safelive.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by abacalu on 2/18/2016.
 */
public class NursingHome implements Parcelable {
    @JsonProperty("nursinghomeid")
    public int nursinghomeid = 0;
    @JsonProperty("nursinghomeaddress")
    public String nursinghomeaddress = "";
    @JsonProperty("nursinghomename")
    public String nursinghomename = "";
    @JsonProperty("nursinghomeresponsible")
    public int nursinghomeresponsible = 0;

    public NursingHome() {}

    protected NursingHome(Parcel in) {
        nursinghomeid = in.readInt();
        nursinghomeaddress = in.readString();
        nursinghomename = in.readString();
        nursinghomeresponsible = in.readInt();
    }

    public static final Creator<NursingHome> CREATOR = new Creator<NursingHome>() {
        @Override
        public NursingHome createFromParcel(Parcel in) {
            return new NursingHome(in);
        }

        @Override
        public NursingHome[] newArray(int size) {
            return new NursingHome[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(nursinghomeid);
        dest.writeString(nursinghomeaddress);
        dest.writeString(nursinghomename);
        dest.writeInt(nursinghomeresponsible);
    }
}
