
/**
 * @author Mitchell Shapiro
 * January 2020
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//Class to assign scouts optimally to matches
public class Sorter {

    private int numMatches;
    private int numScouts;
    private int breakLength;
    private Match[] matches;
    private Scout[] scouts;
    private boolean running;// Boolean that lets us know if we are currently running
    private boolean forceStop;

    // Default constructor
    public Sorter() {
        forceStop = false;// Initialize forceStop
    }

    /**
     * Run the optimization matches is the match String, formated as each team in a
     * match, then the next match. Each team is represented by its team number.
     * numScouts is the number of scouts. numMaxPasses is the maximum amount of
     * times to let the optimization pass over the matches, it will usually end much
     * earlier, after 2-3 passes. breakLength is the number of matches a break
     * lasts.
     */
    public void run(String matches, int numScouts, int numMaxPasses, int breakLength) {
        // Make sure we are not running
        if (!running) {
            // Catch all errors so if it crashes, we still set running to false
            try {
                running = true;
                forceStop = false;// Reset forceStop
                this.numScouts = numScouts;
                this.breakLength = breakLength;
                numMatches = matches.split(" ").length / Constants.TEAMS_PER_MATCH;
                System.out.println("Number of Scouts: " + numScouts + "\n" + "Max Passes: " + numMaxPasses + "\n"
                        + "Break Length: " + breakLength + "\n" + "Matches: " + numMatches);
                makeMatches(matches);
                scouts = new Scout[numScouts];
                makeScouts();
                assignBreaks();
                optimize(numMaxPasses);// This locks this thread until the optimization completes
                System.out.println("Completed optimization");
                if (forceStop) {
                    running = false;// Set running to false as we end here
                    return;
                }

                for (Scout scout : scouts) {
                    System.out.println(Arrays.toString(scout.getTeamAssignments()) + ": " + scout.countDuplicates());
                }
                System.out.println("Total Duplicates: " + countTotalDuplicates());
                // Make the csv, with the name time/date stamped
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
                LocalDateTime now = LocalDateTime.now();
                String time = dtf.format(now);
                makeCsv("Stratified Scouts " + time + ".csv");
                System.out.println("Done");
                running = false;
            } catch (Exception e) {
                e.printStackTrace();
                running = false;
            }
            // If we are running, do nothing
        } else {
            System.out.println("Already Running!");
        }
    }

    // Set force stop to true
    public void abort() {
        forceStop = true;
    }

    // Return if the optimization is running
    public boolean isRunning() {
        return running;
    }

    // Populate the matches array, to be changed to import from a .csv or The Blue
    // Alliance? or plain text paste in GUI
    private void makeMatches(String matchString) {
        matches = new Match[numMatches];

        String regional = matchString;
        String[] split = regional.split(" ");

        int matchIndex = 0;
        int teamIndex = 0;
        String[] tempTeams = new String[Constants.TEAMS_PER_MATCH];
        for (String string : split) {
            if (teamIndex > Constants.TEAMS_PER_MATCH - 1) {
                matches[matchIndex] = new Match(matchIndex, tempTeams);
                teamIndex = 0;
                matchIndex++;
            }
            tempTeams[teamIndex] = string;
            teamIndex++;
        }
        matches[matchIndex] = new Match(matchIndex, tempTeams);
    }

    // Populate the scouts list with new Scout objects
    private void makeScouts() {
        for (int i = 0; i < scouts.length; i++) {
            scouts[i] = new Scout(i, numMatches);
        }
    }

    // Assign breaks to the scouts so that only 6 are assigned to watch robots at a
    // time
    private void assignBreaks() {
        // No breaks if there are only as many scouts as teams per match
        if (numScouts <= Constants.TEAMS_PER_MATCH)
            return;

        int scoutsOnBreak = numScouts - 6;
        int scoutIndex = 0;

        for (int i = 0; i < numMatches;) {
            for (int j = 0; j < scoutsOnBreak; j++) {
                for (int k = 0; k < breakLength; k++) {
                    if (i + k >= numMatches)
                        break;// This occurs when there are less spots available at the last few matches than
                              // Constants.BREAK_LENGTH
                    scouts[scoutIndex % numScouts].assignTeam(i + k, new Team());
                }
                scoutIndex++;
            }
            i += breakLength;// Skip over the matches we already assigned
        }
    }

    // Assign the optimal schedule to scouts
    // Takes in the maximum amount of passes to complete
    private void optimize(int maxPasses) {
        // Make sure maxPasses is in bounds (1, infinity)
        if (maxPasses < 1) {
            System.out.println("Max Passes must be >= 1, using 1");
            maxPasses = 1;
        }

        // These ints store the duplicate total from the last two passes
        int lastResult = -1;
        int secondLastResult = -1;

        // Loop until we hit the maximum passes value
        for (int i = 0; i < maxPasses; i++) {
            // If the last and second to last duplicate totals are the same, we have reached
            // the optimal solution and do not need to continue, so we break
            if (lastResult != -1 && secondLastResult != -1 && lastResult == secondLastResult) {
                System.out.println("Stopping passes as optimal solution has already been found");
                break;
            }

            // Loop through each match
            for (Match match : matches) {
                // This ArrayList is populated with all possible permutations of the teams in
                // this match (720 permutations for 6 values)
                ArrayList<Team[]> allPossibleTeams = permute(match.getTeams());

                // These variables store the current best assignment for this match
                int minDuplicates;// The duplicate value to beat
                Team[] bestAssignment;// An array containing the scout assignments, ie: [1224, 2635, 3128, 4040, 509,
                                      // 6216], where 1224 is Scout 1's assignment, 2635 is Scout 2's assignment, etc

                // Check if this is not the first pass
                if (i > 0) {
                    // If this is not the first run, the solution to beat is the current solution
                    minDuplicates = countTotalDuplicates();// Gets the current duplicate total
                    bestAssignment = new Team[Constants.TEAMS_PER_MATCH];
                    int index = 0;
                    // Sets the best assignment to each scout's current assignment for this match
                    // This is needed as we need to reassign this solution if a better one is not
                    // found
                    for (Scout scout : scouts) {
                        // First ensure this scout is not on break
                        if (scout.isOnBreak(match))
                            continue;
                        bestAssignment[index] = scout.getAssignmentForMatch(match);
                        index++;
                    }
                } else {
                    // If this is the first pass, use empty values
                    minDuplicates = -1;
                    bestAssignment = null;
                }

                // Loop through every team permutation in allPossibleTeams
                for (Team[] teams : allPossibleTeams) {

                    if (forceStop) {
                        System.out.println("Force Stop was true, aborting");
                        return;
                    }

                    // Set the scouts to use this permutation, so we can compare the results
                    assignScouts(match.getMatchNumber(), teams);

                    // This is the current duplicate total as if using this permutation
                    int thisDuplicates = countTotalDuplicates();

                    // If minDuplicates is -1, we accept this permutation (the first one for this
                    // match) as the solution
                    // Otherwise, we only accept the permutation if it beats the current best
                    // duplicate total
                    if (minDuplicates == -1 || thisDuplicates < minDuplicates) {
                        minDuplicates = thisDuplicates;// Update minDuplicates to the new best duplicate total
                        bestAssignment = teams;// Set bestAssignment to this permutation
                        // If there are no duplicates, this is the best solution, so we can skip the
                        // rest of the permutations for this match
                        if (thisDuplicates == 0) {
                            break;
                        }
                    }
                }

                // The assignment should be set to real team numbers, somethign has broken if it
                // was not
                if (bestAssignment == null) {
                    System.out.println("Failed to assign scouts, bestAssignment was null for a match! Exiting...");
                    System.exit(1);
                }

                // Assign the scouts to use the best assignment found
                assignScouts(match.getMatchNumber(), bestAssignment);
            }
            // Update the last and second to last duplicate totals
            secondLastResult = lastResult;
            lastResult = countTotalDuplicates();
            // Print the pass and duplicate total result
            System.out.println("Pass: " + i + " Duplicates: " + lastResult);
        } // End pass loop
    }

    // Assign the assignments array to the scouts for the given match number
    private void assignScouts(int matchNumber, Team[] assignments) {
        int index = 0;// Keeps track of which Scout to use
        // Loop through each team in this permutation, and assign the scouts
        for (Team team : assignments) {
            // Ensure that this scout is not on break, if so, we just ignore it
            while (scouts[index].isOnBreak(matchNumber)) {
                index++;
            }
            scouts[index].assignTeam(matchNumber, team);
            index++;
        }
    }

    // Returns the total amount of duplicate assignments from all scouts
    private int countTotalDuplicates() {
        int total = 0;
        // Loop through all scouts and sum the individual duplicates from each
        for (Scout scout : scouts) {
            total += scout.countDuplicates();
        }
        return total;
    }

    // Save the results of this run in a .csv file
    // Columns are separated by commas, rows are separated by new lines
    // Takes in the path to make the .csv file at
    private void makeCsv(String path) {
        try {
            FileWriter csvWriter = new FileWriter(path);
            csvWriter.append(",");// Leave the square 0, 0 empty

            // Populate the first row with the scout names
            String scoutString = "";
            for (Scout scout : scouts) {
                scoutString += (scout.toString() + ",");
            }
            // Remove the trailing comma from this line
            scoutString = scoutString.substring(0, scoutString.length() - 1);
            csvWriter.append(scoutString + "\n");

            // Populate the rest of rows with a match label, and scout assignments
            for (Match match : matches) {
                csvWriter.append(match.toString() + ",");// Write the match label
                // Write the assignment of each scout for this match
                String assignmentString = "";
                for (Scout scout : scouts) {
                    assignmentString += (scout.getAssignmentForMatch(match) + ",");
                }
                // Remove the trailing comma from this line
                assignmentString = assignmentString.substring(0, assignmentString.length() - 1);
                csvWriter.append(assignmentString + "\n");
            }

            csvWriter.flush();// Write to the .csv file
            csvWriter.close();// Close the FileWriter to avoid memory leak
            System.out.println("Successfully created csv file at: " + System.getProperty("user.dir") + "\\" + path);
        } catch (IOException e) {
            System.out.println("IOException making csv file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Permutation code adapted from (converted to java from javascript):
    // https://stackoverflow.com/questions/18681165/shuffle-an-array-as-many-as-possible
    // Based on:
    // http://en.wikipedia.org/wiki/Steinhaus%E2%80%93Johnson%E2%80%93Trotter_algorithm
    // There are 720 permutations of a list of length 6 (6 teams per match)
    // BEGIN
    private Team[] swap(Team[] arr, int a, int b) {
        Team temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
        return arr;
    }

    private int factorial(int n) {
        int val = 1;
        for (int i = 1; i < n; i++) {
            val *= i;
        }
        return val;
    }

    private ArrayList<Team[]> permute(Team[] teams) {

        ArrayList<Team[]> allPossibleTeams = new ArrayList<Team[]>();

        int total = factorial(teams.length);
        // Print total

        int i = 0;
        int inc = 1;
        for (int j = 0; j < total; j++) {

            while (i < teams.length - 1 && i >= 0) {
                // This is a permutation
                allPossibleTeams.add(teams.clone());

                teams = swap(teams, i, i + 1);
                i += inc;
            }

            // This is a permutation
            allPossibleTeams.add(teams.clone());

            if (inc == 1) {
                teams = swap(teams, 0, 1);
            } else {
                teams = swap(teams, teams.length - 1, teams.length - 2);
            }

            inc *= -1;
            i += inc;
        }
        return allPossibleTeams;
    }
    // END
}