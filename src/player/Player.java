package player;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;

public class Player {
    private static String path;
    private static Play play;
    private static boolean isPlaying = false;
    private static int length;
    private static FloatControl volumeControl;

    static {
        openVolume();
    }

    public static void play() {
        isPlaying = true;
        Thread thread = new Thread(play);
        thread.start();
    }

    public static void play(String path) {
        if (!isPlaying) {
            newTrack(path);
            play();
        } else {
            stop();
        }
    }

    public static void newTrack(String path) {
        play = new Play(path);
        Player.path = path;
        length = play.getLength();
    }

    public static void stop() {
        pause();
        newTrack(path);
    }

    public static void pause() {
        if (isPlaying) {
            play.pause();
            isPlaying = false;
        }
    }

    public static void slide(int value) {
        play.slide(value);
        if (isPlaying) {
            play();
        }
    }

    public static int getLength() {
        return length;
    }

    public static double getTimeValue() {
        return play.getTimeValue();
    }

    public static void setVolume(Float v) {
        volumeControl.setValue(v / 100);
    }

    public static float getVolume() {
        return volumeControl.getValue()*100;
    }

    private static void openVolume() {
        Port.Info source = Port.Info.SPEAKER;

        if (!AudioSystem.isLineSupported(source)) {
            source = Port.Info.LINE_OUT;
        }
        if (!AudioSystem.isLineSupported(source)) {
            source = Port.Info.HEADPHONE;
        }

        try {
            Port outline = (Port) AudioSystem.getLine(source);
            outline.open();
            volumeControl = (FloatControl) outline.getControl(FloatControl.Type.VOLUME);
        } catch (LineUnavailableException ex) {
            System.err.println("source not supported");
            ex.printStackTrace();
        }

    }
}