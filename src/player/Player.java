package player;

class Player{
    private static String path;
    private static Play play;
    private static Long pauseValue;
    private static boolean isPlaying = false;
    private static int length;

    static void play() {
        isPlaying = true;
        Thread thread = new Thread(play);
        thread.start();
    }

    static void play(String path) {
        play(path,0);
    }

    static void play(int value) {
        play(path,value);
    }

    static void play(String path,int value) {
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
        if(isPlaying) {
            play.pause();
            isPlaying = false;
        }
    }

    static void slide(int value) {
        play.slide(value);
        play();
    }

    static int getLength(){
        return length;
    }



    static double getTimeValue() {
        return play.getTimeValue();
    }


}