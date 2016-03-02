package evolveconference.safelive.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abacalu on 2/18/2016.
 */
public class AnomalyList {
    @JsonProperty("resource")
    public List<Anomaly> record = new ArrayList<>();
}
