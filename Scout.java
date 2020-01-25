/**
 * @author Mitchell Shapiro
 * January 2020
 */

import java.util.ArrayList;

//Class to represent each scout
public class Scout {

    private Team[] teamAssignments;//The assigned teams for this scout for all matches, one team for each match
    private int number;//The scout's number

    //Initialize with the scout's number and the total number of matches
    public Scout(int number, int numMatches) {
        teamAssignments = new Team[numMatches];
        this.number = number;
    }

    //Return the teamAssignments array
    public Team[] getTeamAssignments() {
        return teamAssignments;
    }

    //Return the team assignment for a given match
    public Team getAssignmentForMatch(Match match) {
        return teamAssignments[match.getMatchNumber()];
    }

    //Return if this scout is on break for the given match
    public boolean isOnBreak(Match match) {
        return isOnBreak(match.getMatchNumber());
    }

    //Return if this scout is on break for the given match number
    public boolean isOnBreak(int matchNumber) {
        return teamAssignments[matchNumber] != null && teamAssignments[matchNumber].isBreak();
    }

    //Return the amount of times this scout is assigned to the given team
    public int getTeamCount(Team team) {
        return getArrayCount(teamAssignments, team);
    }

    //Return the total amount of duplicate team assignments
    public int countDuplicates() {
        //ArrayList to store all the teams we have already checked
        ArrayList<Team> checkedTeams = new ArrayList<Team>();

        int duplicates = 0;
        for (Team team : teamAssignments) {
            //Stop this loop if this team is null so we do not continue checking all the null teams after this one
            if (team == null) break;
            //Check if we already counted this team, so we do not duplicate the duplicates, or if this team is a break
            if (getArrayCount(checkedTeams, team) != 0 || team.isBreak()) continue;
            checkedTeams.add(team);//Add this team as already checked
            duplicates += Math.max(0, getTeamCount(team) - 1);//- 1 as not counting the first occurence, limit to 0 (probably not needed)
        }
        return duplicates;
    }

    //Count the duplicate occurences of the given team
    public int countDuplicatesForTeam(Team team) {
        return Math.max(0, getArrayCount(teamAssignments, team) - 1);//- 1 as not counting the first occurence, limit to 0 (needed as team may not be contained)
    }

    //Return the occurences of the object in the array
    //T is a generic, representing any Object type
    private static <T> int getArrayCount(T[] array, T object) {
        int count = 0;
        //Loop through all values in the array, and increment count if a value is the same as the object we are checking for
        for (T checking : array) {
            if (checking != null && checking.equals(object))
                count++;
        }
        return count;
    }

    //Same as above, just for ArrayList
    private static <T> int getArrayCount(ArrayList<T> array, T object) {
        int count = 0;
        for (T checking : array) {
            if (checking != null && checking.equals(object))
                count++;
        }
        return count;
    }

    //Assigns a team to scout for the given match (the first match is 0)
    public void assignTeam(int match, Team team) {
        teamAssignments[match] = team;
    }

    @Override
    public String toString() {
        return "Scout " + (number + 1);
    }
}