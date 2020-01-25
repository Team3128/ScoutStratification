
/**
 * @author Mitchell Shapiro
 * January 2020
 */

import java.util.ArrayList;

//Class to store a match and its properties
public class Match {

    // Team array to store the team number of every team in this match
    private Team[] teams;
    // Scout ArrayList to store the scouts on break for this match
    private ArrayList<Scout> breakScouts = new ArrayList<Scout>();

    // The number of this match (the first match is 0)
    private int matchNumber;

    // Initialized with the match number and a list of the teams in this match
    public Match(int matchNumber, String... teams) {
        setTeams(teams);
        setMatchNumber(matchNumber);
    }

    // Return the teams array
    public Team[] getTeams() {
        return teams;
    }

    // Return the match number
    public int getMatchNumber() {
        return matchNumber;
    }

    // Update teams with the provided array
    public void setTeams(String[] teamStrings) {
        teams = new Team[Constants.TEAMS_PER_MATCH];

        int index = 0;
        for (String teamString : teamStrings) {
            teams[index] = new Team(teamString);
            index++;
        }
    }

    // Return true if the given scout is on break for this match, false if not
    public boolean isOnBreak(Scout scout) {
        return breakScouts.contains(scout);
    }

    // Add the given scout as one on break, if the scout is not already set
    public void setBreakScout(Scout scout) {
        if (!breakScouts.contains(scout))
            breakScouts.add(scout);
    }

    // Set the match number to the provided int
    public void setMatchNumber(int matchNumber) {
        this.matchNumber = matchNumber;
    }

    @Override
    public String toString() {
        return "Match " + (matchNumber + 1);
    }
}