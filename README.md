# Before You Begin
This program requires Oracle Java 1.8 JRE or higher to be installed on your machine.

All you need to do is download ScoutStratification.jar, and run it. The other files here are the source code, which you do not need.

# Importing the match data

## Enter the match data with a .csv file
You first need a .csv file formated correctly. There should be 6 columns, one for each team per match. Each row represents each match, so each row will have 6 teams. Other rows/columns with text will not affect the import as long as none of them are only numbers. Import without anything else in the .csv if you have problems.

IMPORTANT: You must save this as a comma delited .csv file:

    From Google Sheets, choose File -> Download -> Comma-Separated values (.csv, current sheet). Make sure you are on the correct tab for the data you want.

    From Excel, choose File -> Save As -> Comma Delimited CSV, and ignore any warnings. All of the Excel's CSV exports that I have tried worked, but try some others if it will not import.

Press the "Select CSV" button and navigate to the location of your .csv file. Click your file, then press open and verify that your file's path appears next to the "Select CSV" button.

A .csv file will take precedence over data in the text area. 

## Enter the match data manually
Enter the team numbers into the big text area. Format the manual entry by entering each team's number for each match, ie: the first 6 numbers make up the first match. The team numbers must be separated by single spaces. (Copy pasting directly from a spreadsheet won't work)

# Configuring the Options

The options panel to the right of the text area allows you to configure some parameters for the optimization. They are prefilled with default values that will work.

## Scouts
The number of scouts to assign. Values greater than 6 will result in at least one scout per match being assigned to break.

## Max Passes
The maximum number of passes that the optimization will run. Changing this value will not usually have an effect as the optimization usually completes after 2-3 passes. A bigger number means that the optimization could potentially take longer to run, but the amount of duplicates will be less. If you are reaching this amount of passes, consider increasing this value.

## Break Length
The number of matches that a break lasts. Changing this value has no effect if Scouts is not >= 7.

# Running the Optimization
After importing your match data, the "Assign Scouts" button will become available. Tune your settings, then press this button to begin the optimization. This button will be disabled until the optimmization completes.

# Other Buttons
The GUI contains two other buttons, "Clear CSV" and "Abort".

## Clear CSV
This button becomes available once a .csv file is selected. Pressing it will unselect the .csv file, allowing you to use the data in the text area instead of a .csv.

## Abort
This button becomes available while the optimization is running. Pressing this will stop the optimization immediately, and no results will be generated.

# Results
After the optimization completes, the results will be printed in the console, and a .csv file will be generated. The location the .csv file is saved to will be printed in the console. It should be in the same directory as where your ScoutStratification.jar is. This .csv file contains the optimal scout assignment found, labeled with Matches and Scouts.

# Reading the console
The console will provide some helpful information for debugging problems. Invalid options will be printed to the console. The progress of the optimization is also displayed.

# How it works
The optimization works by looping through each match, generating all possible permutations for the teams. Each one is compared by the number of duplicates it creates. Several passes are done to try to further optimize the results.