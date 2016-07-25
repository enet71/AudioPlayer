package player;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable,Playable  {
    @FXML
    private Label name;
    @FXML
    private ListView trackList;
    @FXML
    private Label time;
    @FXML
    private Label fullTime;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button play;
    @FXML
    private Button stop;
    @FXML
    private Slider slider;

    private Boolean isPlaying = false;
    private Boolean isSliderPressing = false;
    private AnimationTimer animationTimer;
    private Timeline timeline;
    private ArrayList<Track> tracks;


    @FXML
    public void play() {
        if (!isPlaying) {
            Player.play();
            isPlaying = true;
            play.setText("Pause");
            timeline.play();
        } else {
            pause();
        }
        fullTime.setText(Model.getValueTime(100));
    }

    @FXML
    public void stop() {
        if (isPlaying) {
            stopPlay();
            Player.stop();
            progressBar.setProgress(0);
            time.setText("0:00");
        }
    }

    @FXML
    public void pause() {
        if (isPlaying) {
            stopPlay();
            Player.pause();
        }
    }

    private void stopPlay() {
        isPlaying = false;
        timeline.stop();
        play.setText("Play");
    }

    @FXML
    private void slide() {
        slider.setOnMousePressed(event -> {
            isSliderPressing = true;
        });

        slider.setOnMouseDragged(event -> progressBar.setProgress(slider.getValue() / 100));

        slider.setOnMouseReleased((MouseEvent event) -> {
            if (isPlaying) {
                Player.slide((int) slider.getValue());
            }
            isSliderPressing = false;
            progressBar.setProgress(slider.getValue() / 100);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        slide();
        listAction();

        timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
            time.setText(Model.getValueTime(Player.getTimeValue()));
            if (!isSliderPressing) {
                slider.setValue(Player.getTimeValue());
                updateProgressBar();
            }
            if (Player.getTimeValue() == 100) {
                stop();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private void updateProgressBar() {
        progressBar.setProgress((Player.getTimeValue() + 1) / 100);
    }



    @FXML
    private void openFiles() {
        FileChooser fileChooser = new FileChooser();
        List<File> list = fileChooser.showOpenMultipleDialog(time.getScene().getWindow());
        tracks = new ArrayList<>();


        if(list != null) {
            for (File file : list) {
                try {
                    AudioFile audioFile = AudioFileIO.read(file);
                    String length = Model.getTime(audioFile.getAudioHeader().getTrackLength());
                    String name = file.getName();
                    tracks.add(new Track(name, length, file.getAbsolutePath()));

                } catch (TagException | ReadOnlyFileException | InvalidAudioFrameException | CannotReadException | IOException e) {
                    e.printStackTrace();
                }

            }
            stop();
            addToList();
            Player.newTrack(tracks.get(0).getPath());
            setParameters(tracks.get(0).getName(),tracks.get(0).getTime());
        }
    }

    private void addToList() {
        trackList.getItems().clear();
        for (Track track : tracks) {
            BorderPane borderPane = new BorderPane();
            Label label = new Label(track.getName());
            label.setWrapText(true);
            label.setMaxWidth(420);
            borderPane.setLeft(label);
            borderPane.setRight(new Label(track.getTime()));
            trackList.getItems().add(borderPane);
        }
    }

    private void listAction() {
        trackList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Track track = tracks.get(trackList.getSelectionModel().getSelectedIndex());
                stop();
                Player.newTrack(track.getPath());
                play();
                setParameters(track.getName(), track.getTime());
            }
        });
    }

    private void setParameters(String name, String time){
        this.name.setText(name);
        this.fullTime.setText(time);
    }

    private void setText(String string){
        name.setText(string);
        TranslateTransition transTransition = new TranslateTransition(new Duration(5000));
        transTransition.setNode(name);
        transTransition.setToX(-name.getText().getBytes().length * 10);
        transTransition.setAutoReverse(true);
        transTransition.setCycleCount(5);
        transTransition.play();
    }
}
