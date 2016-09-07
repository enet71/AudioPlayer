package setting;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class Save {
    private static File file = new File("src/setting/settings.json");
    private static JSONObject jsonObject;

    public static void save(){
        read();
        put("FOLDERPATH", Settings.FOLDERPATH);
        put("REPEAT", String.valueOf(Settings.REPEAT));
        write();
    }

    private static void save(String attribute, String value) {
        read();
        put(attribute, value);
        write();
    }

    private static void put(String attribute, String value) {
        jsonObject.put(attribute, value);
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

    private static void write() {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonObject.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
