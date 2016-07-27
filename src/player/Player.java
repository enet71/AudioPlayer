package player;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;

class Player {
    private static String path;
    private static Play play;
    private static Long pauseValue;
    private static boolean isPlaying = false;
    private static int length;
    private static FloatControl volumeControl;

    static {
        openVolume();
    }

    static void play() {
        isPlaying = true;
        Thread thread = new Thread(play);
        thread.start();
    }

    static void play(String path) {
        play(path, 0);
    }

    static void play(int value) {
        play(path, value);
    }

    static void play(String path, int value) {
        if (!isPlaying) {
            newTrack(path);
            play();
        } else {
            stop();
        }
    }

    static void newTrack(String path) {
        play = new Play(path);
        Player.path = path;
        length = play.getLength();
    }

    static void stop() {
        pause();
        newTrack(path);
    }

    static void pause() {
        if (isPlaying) {
            play.pause();
            isPlaying = false;
        }
    }

    static void slide(int value) {
        play.slide(value);
        play();
    }

    static int getLength() {
        return length;
    }

    static double getTimeValue() {
        return play.getTimeValue();
    }

    static void setVolume(Float v) {
        volumeControl.setValue(v / 100);
    }

    static float getVolume() {
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