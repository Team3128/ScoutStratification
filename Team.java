/**
 * @author Mitchell Shapiro January 2020
 */

// Class to represent a team, to provide consistency across the classes
public class Team {

    // The team number, as a String
    private String number;
    // Boolean to store whether this team is a break or not
    private boolean isBreak;

    // Initiialize with the team number as a String
    public Team(String number) {
        this.number = number;
        isBreak = false;
    }

    // Initialize this team as a break
    public Team() {
        number = Constants.BREAK_NAME;
        isBreak = true;
    }

    // Return the team number
    public String getNumber() {
        return number;
    }

    // Update the team number to the provided String
    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isBreak() {
        return isBreak;
    }

    public void setIsBreak(boolean isBreak) {
        this.isBreak = isBreak;
    }

    @Override
    public String toString() {
        return number;
    }

    // Equal if the other object is a team, and has the same team number
    @Override
    public boolean equals(Object other) {
        if (other instanceof Team) {// Check if the other object is a Team object
            Team otherTeam = (Team) other;// Cast the other object to a Team object
            return number.equals(otherTeam.getNumber()) && isBreak == otherTeam.isBreak();// Check if the team numbers
                                                                                          // and break status are the
                                                                                          // same
        }
        return false;
    }
}