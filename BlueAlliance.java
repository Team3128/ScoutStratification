
/**
 * @author Mitchell Shapiro
 * January 2020
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Abandoned The Blue Alliance integration was abandoned due to the difficulty
 * of parsing the received data. The data is in the JSON format but my attempts
 * to get a library to read them were unsuccessful. The token.txt file contains
 * a token for the Blue Alliance (not pushed to github), and the
 * RequestResults.txt file is an example of what is returned. I am leaving this
 * class in case someone wants to continue this portion in the future. I have
 * also heard the FIRST has its own api, but I did not have time to investigate.
 */
public class BlueAlliance {

    private String token;

    public BlueAlliance() {
        if (readToken()) {
            getEvents();
        }
    }

    private void getEvents() {

        String address = Constants.BLUE_ALLIANCE_SERVER + "/team/frc3128/events";

        URL url;
        try {
            url = new URL(address);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            con.setRequestProperty("X-TBA-Auth-Key", token);
            con.setRequestMethod("GET");

            int status = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            // writeResults(content);
            System.out.println("Results:\n" + content);

            in.close();

            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeResults(StringBuffer results) {
        try {
            FileWriter fw = new FileWriter("RequestResults.txt");

            fw.append(results);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean readToken() {
        File path = new File("token.txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            token = reader.readLine();
            reader.close();
            System.out.println("Blue Alliance token loaded");
            return true;
        } catch (IOException e) {
            System.out.println("Failed to read Blue Alliance token!");
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        new BlueAlliance();
    }
}