package player;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Play implements Runnable, Playable {
    private AdvancedPlayer playMP3;
    private long songTotalLength;
    private InputStream fileInputStream;
    private BufferedInputStream bufferedInputStream;
    private String path;
    private long pauseLocation = 0;
    private Path file;

    Play(String path) {
        this.path = path;
        createStream();
    }

    public void play() {
        play(pauseLocation);
    }

    public void play(Long pauseLocation) {

        // source = Port.Info.SPEAKER;
        //        source = Port.Info.LINE_OUT;
        Info source = Port.Info.HEADPHONE;

        if (AudioSystem.isLineSupported(source)) {
            try {
                Port outline = (Port) AudioSystem.getLine(source);
                outline.open();
                FloatControl volumeControl = (FloatControl) outline.getControl(FloatControl.Type.VOLUME);
                System.out.println("       volume: " + volumeControl.getValue());
                float v = 0.33F;
                volumeControl.setValue(v);
                System.out.println("   new volume: " + volumeControl.getValue());
                v = 0.01F;
                volumeControl.setValue(v);
                System.out.println("newest volume: " + volumeControl.getValue());
            } catch (LineUnavailableException ex) {
                System.err.println("source not supported");
                ex.printStackTrace();
            }
        }


        try {
            createStream();

            playMP3 = new AdvancedPlayer(bufferedInputStream);
            playMP3.play();

        } catch (JavaLayerException e) {
            e.printStackTrace();
        }

    }

    private void createStream() {
        try {
            file = Paths.get(path);
            fileInputStream = Files.newInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            songTotalLength = bufferedInputStream.available();
            bufferedInputStream.skip(pauseLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getSongTotalLength() {
        return songTotalLength;
    }

    public int getLength() {
        try {
            AudioFile audioFile = AudioFileIO.read(file.toFile());
            return audioFile.getAudioHeader().getTrackLength();
        } catch (TagException | IOException | ReadOnlyFileException | InvalidAudioFrameException | CannotReadException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void stop() {
        playMP3.close();
    }

    public void pause() {
        try {
            pauseLocation = songTotalLength - bufferedInputStream.available();
        } catch (IOException e) {
            System.out.println("Error pause");
        } finally {
            playMP3.close();
        }
    }

    public void slide(int value) {
        if (value > 99) {
            value = 99;
        }
        pauseLocation = (songTotalLength * value) / 100;
        if (playMP3 != null) {
            playMP3.close();
            createStream();
        }
    }

    public double getTimeValue() {
        try {
            double time = (100 - ((bufferedInputStream.available() * 100.0) / songTotalLength));
            return time;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void run() {
        play(pauseLocation);
    }
}
