package evolveconference.safelive.model;

import java.util.ArrayList;
import java.util.List;

public class Patient {
    public int id;
    public String name;
    public int photoId;
    public List<Measure> measures = new ArrayList<>();
    public int risk;

    public Patient(int id, String name, int photoId, int risk) {
        this.id = id;
        this.name = name;
        this.photoId = photoId;
        this.risk = risk;
    }

    public void addMeasure(Measure measure) {
        measures.add(measure);
    }
}
