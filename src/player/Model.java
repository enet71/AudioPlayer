package player;

public class Model {
    public static String getTime(double time) {
        int min = (int) (time / 60);
        int sec = (int) (time - min * 60);
        String s = "";
        if (sec < 10)
            s = "0";
        return min + ":" + s + sec;
    }

    public static String getValueTime(double value) {
        double time = Player.getLength() * value / 100;
        return getTime(time);
    }
}
