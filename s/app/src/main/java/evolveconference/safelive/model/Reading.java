package evolveconference.safelive.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by abacalu on 2/21/2016.
 */
public class Reading {
    public static final String TYPE_PEDOMETER = "pedometer";
    public static final String TYPE_PEDOMETER_SYNTAX = "type=" + TYPE_PEDOMETER;
    public static final String STEPS_SYNTAX = "Steps=";
    public static final String STEPS_AFRETNOON_SYNTAX = "AfternoonSteps=";
    public static final String STEPS_EVENING_SYNTAX = "EveningSteps=";
    public static final String STEPS_MIDNIGHT_SYNTAX = "MidnightSteps=";
    public static final String STEPS_MORNING_SYNTAX = "MorningSteps=";
    public static final String STEPS_NIGHT_SYNTAX = "NightSteps=";
    public static final String STEPS_NOON_SYNTAX = "NoonSteps=";

    @JsonProperty("readingid")
    public int id = 0;
    @JsonProperty("readingtimestamp")
    public String timestamp = "";
    @JsonProperty("readingvalue")
    public String value = "";
    @JsonProperty("deviceid")
    public String deviceid = "";
    @JsonProperty("sensor")
    public String sensor = "";

    public double getLat() {
        return Double.parseDouble(value.substring(value.indexOf("Lat:") + 4, value.indexOf(" Long:")));
    }

    public double getLong() {
        return Double.parseDouble(value.substring(value.lastIndexOf("Long:") + 5, value.indexOf(" Alt:")));
    }

    public int getStepsTotal() {
        if (value.contains(TYPE_PEDOMETER_SYNTAX)) {
            return Integer.parseInt(value.substring(value.indexOf(STEPS_SYNTAX) + STEPS_SYNTAX.length(), value.indexOf(STEPS_AFRETNOON_SYNTAX) - 1));
        }
        return 0;
    }

    public int getStepsAfternoon() {
        if (value.contains(TYPE_PEDOMETER_SYNTAX)) {
            return Integer.parseInt(value.substring(value.indexOf(STEPS_AFRETNOON_SYNTAX) + STEPS_AFRETNOON_SYNTAX.length(), value.indexOf(STEPS_EVENING_SYNTAX) - 1));
        }
        return 0;
    }

    public int getStepsEvening() {
        if (value.contains(TYPE_PEDOMETER_SYNTAX)) {
            return Integer.parseInt(value.substring(value.indexOf(STEPS_EVENING_SYNTAX) + STEPS_EVENING_SYNTAX.length(), value.indexOf(STEPS_MIDNIGHT_SYNTAX) - 1));
        }
        return 0;
    }

    public int getStepsMidnight() {
        if (value.contains(TYPE_PEDOMETER_SYNTAX)) {
            return Integer.parseInt(value.substring(value.indexOf(STEPS_MIDNIGHT_SYNTAX) + STEPS_MIDNIGHT_SYNTAX.length(), value.indexOf(STEPS_MORNING_SYNTAX) - 1));
        }
        return 0;
    }

    public int getStepsMorning() {
        if (value.contains(TYPE_PEDOMETER_SYNTAX)) {
            return Integer.parseInt(value.substring(value.indexOf(STEPS_MORNING_SYNTAX) + STEPS_MORNING_SYNTAX.length(), value.indexOf(STEPS_NIGHT_SYNTAX) - 1));
        }
        return 0;
    }

    public int getStepsNight() {
        if (value.contains(TYPE_PEDOMETER_SYNTAX)) {
            return Integer.parseInt(value.substring(value.indexOf(STEPS_NIGHT_SYNTAX) + STEPS_NIGHT_SYNTAX.length(), value.indexOf(STEPS_NOON_SYNTAX) - 1));
        }
        return 0;
    }

    public int getStepsNoon() {
        if (value.contains(TYPE_PEDOMETER_SYNTAX)) {
            return Integer.parseInt(value.substring(value.indexOf(STEPS_NOON_SYNTAX) + STEPS_NOON_SYNTAX.length(), value.length() - 1));
        }
        return 0;
    }
}
