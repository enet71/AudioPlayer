package setting;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Load {
    private static File file = new File("src/setting/settings.json");
    private static JSONObject jsonObject;

    public static void load() {
        read();
        Settings.FOLDERPATH = jsonObject.get("FOLDERPATH").toString();
        Settings.REPEAT = Integer.parseInt(jsonObject.get("REPEAT").toString());
    }

    private static void read() {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            JSONParser parser = new JSONParser();
            jsonObject = (JSONObject) parser.parse(reader);
            reader.close();
            fileReader.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
