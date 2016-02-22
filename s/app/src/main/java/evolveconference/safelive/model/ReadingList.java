package evolveconference.safelive.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abacalu on 2/21/2016.
 */
public class ReadingList {
    @JsonProperty("resource")
    public List<Reading> record = new ArrayList<>();
}
