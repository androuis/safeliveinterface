package evolveconference.safelive.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abacalu on 2/18/2016.
 */
public class ResidentList {
    @JsonProperty("resource")
    public List<Resident> record = new ArrayList<>();
}
