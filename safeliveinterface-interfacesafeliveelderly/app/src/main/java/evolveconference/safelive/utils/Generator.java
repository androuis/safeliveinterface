package evolveconference.safelive.utils;

import java.util.Random;

import evolveconference.safelive.R;

public class Generator {
    private static Random r = new Random();
    private static int[] riskColor = new int[]{ R.color.wheel_green, R.color.wheel_orange, R.color.wheel_red };

    public static int randomRisk() {
        return riskColor[r.nextInt(3)];
    }

    public static int randomAlertWarningRisk() {
        return riskColor[r.nextInt(2) + 1];
    }
}
