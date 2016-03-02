package evolveconference.safelive.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by abacalu on 2/18/2016.
 */
public class Anomaly {
    @JsonProperty("anomalyid")
    public int id = 0;
    @JsonProperty("residentid")
    public int residentId = 0;
    @JsonProperty("anomalytimestamp")
    public String timestamp = "";
    @JsonProperty("anomalyvalue")
    public String value = "";
    @JsonProperty("context")
    public String context = "";
    @JsonProperty("eventtype")
    public int eventType = 0;
    @JsonProperty("TypeOfAnomaly")
    public String anomaly = "";
}
