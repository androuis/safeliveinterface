package evolveconference.safelive.model;

import evolveconference.safelive.utils.Generator;

public class BloodPressureMeasure extends Measure {

    private final int min;
    private final int max;
    private int risk;

    public BloodPressureMeasure(int min, int max) {
        this.min = min;
        this.max = max;
        risk = Generator.randomRisk();
    }

    @Override
    public String getLabel() {
        return String.format("%s/%s", min, max);
    }

    @Override
    public int getRisk() {
        return risk;
    }
}
