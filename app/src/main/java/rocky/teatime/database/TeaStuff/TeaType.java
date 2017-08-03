package rocky.teatime.database.TeaStuff;

/**
 * A simple enum for different types of teas
 * @author Rocky Petkov
 * @version Final
 */
public enum TeaType {
    BLACK, GREEN, WHITE, PUERH, YELLOW, OOLONG, HERBAL;

    /**
     * Gets the enum value of a given integer
     * @param someNum An integer which will correspond with an enum value
     * @return An instance of TeaType which corresponds with the supplied integer
     */
    public static TeaType fromInt(int someNum) {
        switch(someNum) {
            case 0:
                return BLACK;
            case 1:
                return GREEN;
            case 2:
                return WHITE;
            case 3:
                return PUERH;
            case 4:
                return YELLOW;
            case 5:
                return OOLONG;
            case 6:
                return HERBAL;
        }
        return null;    // Default case is to return null
    }
}
