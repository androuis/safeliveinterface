package evolveconference.safelive.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by abacalu on 2/18/2016.
 */
public class Resident {
    @JsonProperty("resid")
    public int id = 0;
    @JsonProperty("resfirstname")
    public String firstName = "";
    @JsonProperty("reslastname")
    public String lastName = "";
    @JsonProperty("resgender")
    public String gender = "";
    @JsonProperty("resphoto")
    public String photo = "";
    @JsonProperty("reslocation")
    public String location = "";
    @JsonProperty("resriskclass")
    public int riskClass = 0;
}
