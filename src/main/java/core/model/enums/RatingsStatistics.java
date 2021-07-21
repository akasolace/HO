package core.model.enums;


public enum RatingsStatistics {
    POWER_RATINGS(1),
    HATSTATS_TOTAL(2),
    HATSTATS_DEF(3),
    HATSTATS_MID(4),
    HATSTATS_OFF(5);
    private int value;

    RatingsStatistics(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
/*
    public static RatingsStatistics getCode(String sector, String stat){

        if (stat.equalsIgnoreCase("max"))
        return switch (sector.toLowerCase()){
            case "total" -> HATSTATS_TOTAL_MAX;
            case "def" -> HATSTATS_DEF_MAX;
            case "mid" -> HATSTATS_MID_MAX;
            case "off" -> HATSTATS_OFF_MAX;
            default -> throw new IllegalStateException("Unexpected value: " + sector.toLowerCase());
        };
        else if (stat.equalsIgnoreCase("avg")){
            return switch (sector.toLowerCase()){
                case "total" -> HATSTATS_TOTAL_AVG;
                case "def" -> HATSTATS_DEF_AVG;
                case "mid" -> HATSTATS_MID_AVG;
                case "off" -> HATSTATS_OFF_AVG;
                default -> throw new IllegalStateException("Unexpected value: " + sector.toLowerCase());
            };
        }

        throw new IllegalStateException("Unexpected value: " + sector.toLowerCase());
    }
*/
}
