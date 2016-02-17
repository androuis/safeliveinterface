package evolveconference.safelive.model;

import evolveconference.safelive.utils.Generator;

public class TemperatureMeasure extends Measure {

    private final int risk;
    private int value;

    public TemperatureMeasure(int value) {
        this.value = value;
        risk = Generator.randomRisk();
    }

    @Override
    public String getLabel() {
        return String.format("%s°C", value);
    }

    @Override
    public int getRisk() {
        return risk;
    }
}
