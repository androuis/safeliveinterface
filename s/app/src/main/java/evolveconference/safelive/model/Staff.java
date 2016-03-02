package evolveconference.safelive.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by abacalu on 2/18/2016.
 */
public class Staff {
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
}
