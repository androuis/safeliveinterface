package evolveconference.safelive.model;

import evolveconference.safelive.utils.Generator;

public class HeartMeasure extends Measure {

    private int value;
    private int risk;

    public HeartMeasure(int value) {
        this.value = value;
        risk = Generator.randomRisk();
    }

    @Override
    public String getLabel() {
        return String.format("%s", value);
    }

    @Override
    public int getRisk() {
        return risk;
    }
}
