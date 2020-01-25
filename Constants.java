
/**
 * @author Mitchell Shapiro January 2020
 */

import java.awt.Dimension;

public class Constants {
    // I don't expect this to ever change but just in case
    public static final int TEAMS_PER_MATCH = 6;
    public static final int DEFAULT_SCOUTS = 7;
    public static final int DEFAULT_MAX_PASSES = 5;
    public static final int DEFAULT_BREAK_LENGTH = 3;// How many matches a break lasts

    public static final String APPLICATION_NAME = "Scout Stratification Tool";
    public static final String BLUE_ALLIANCE_SERVER = "https://www.thebluealliance.com/api/v3";
    public static final String TEXT_HINT = "Enter teams";
    public static final String NONE_SELECTED_TEXT = "None Selected";
    public static final String BREAK_NAME = "BREAK";

    public static final Dimension SCREEN_SIZE = new Dimension(600, 250);
}