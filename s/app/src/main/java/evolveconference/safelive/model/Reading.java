package evolveconference.safelive.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by abacalu on 2/21/2016.
 */
public class Reading {
    @JsonProperty("readingid")
    public int id = 0;
    @JsonProperty("readingtimestamp")
    public String timestamp = "";
    @JsonProperty("readingvalue")
    public String value = "";
    @JsonProperty("deviceid")
    public String deviceid = "";
    @JsonProperty("sensor")
    public String sensor = "";

    public double getLat() {
        return Double.parseDouble(value.substring(value.indexOf("Lat:") + 4, value.indexOf(" Long:")));
    }

    public double getLong() {
        return Double.parseDouble(value.substring(value.lastIndexOf("Long:") + 5, value.indexOf(" Alt:")));
    }
}
