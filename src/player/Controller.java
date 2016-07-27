package player;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
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

public class Controller implements Initializable, Playable {
    @FXML
    private BorderPane mainWindow;
    @FXML
    private VBox playListPanel;
    @FXML
    private Slider volume;
    @FXML
    private Button prevButton;
    @FXML
    private Button addButton;
    @FXML
    private Button openButton;
    @FXML
    private Button playListButton;
    @FXML
    private Button nextButton;
    @FXML
    private Label name;
    @FXML
    private ListView<BorderPane> trackList;
    @FXML
    private Label time;
    @FXML
    private Label fullTime;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button playButton;
    @FXML
    private Button stopButton;
    @FXML
    private Slider slider;

    private Boolean isPlaying = false;
    private Boolean isSliderPressing = false;
    private AnimationTimer animationTimer;
    private Timeline timeline;
    private ArrayList<Track> tracks;
    private int trackIndex;
    private int trackSelectPoPMenu;
    private int prevSelect;
    private boolean openTrackList = true;
    private ContextMenu contextMenu;


    @FXML
    public void play() {
        if (!isPlaying) {
            Player.play();
            isPlaying = true;
            playButton.setText("Pause");
            timeline.play();
        } else {
            pause();
        }
        setColorList(trackIndex);
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
        playButton.setText("Play");
    }

    private void newTrack() {
        Player.newTrack(tracks.get(trackIndex).getPath());
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

    @FXML
    private void next() {
        listPlay(++trackIndex);
    }

    @FXML
    private void prev() {
        listPlay(--trackIndex);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createPopupMenu();
        slide();
        listAction();
        setVolume();
        volume.setValue(Player.getVolume());

        playListButton.setOnMouseClicked(event -> {
            if (openTrackList) {
                mainWindow.getScene().getWindow().setHeight(135);
                trackList.setPrefHeight(0);
                openTrackList = false;
            } else {
                mainWindow.getScene().getWindow().setHeight(445);
                trackList.setPrefHeight(300);
                openTrackList = true;
            }
        });

        timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
            time.setText(Model.getValueTime(Player.getTimeValue()));
            if (!isSliderPressing) {
                slider.setValue(Player.getTimeValue());
                updateProgressBar();
            }
            if (Player.getTimeValue() == 100) {
                listPlay(++trackIndex);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);

    }


    public void dragFile() {
        mainWindow.getScene().setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });

        mainWindow.getScene().setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                for (File file : db.getFiles()) {
                    addToList(file);
                    addToListView();
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void updateProgressBar() {
        progressBar.setProgress((Player.getTimeValue() + 1) / 100);
    }

    @FXML
    private void addFiles() {
        FileChooser fileChooser = new FileChooser();
        List<File> list = fileChooser.showOpenMultipleDialog(time.getScene().getWindow());

        if (list != null) {
            for (File file : list) {
                addToList(file);
            }
            addToListView();
            listPlay(trackIndex, false);
        }
    }

    @FXML
    private void openFiles() {
        tracks = new ArrayList<>();
        trackIndex = 0;
        addFiles();
    }

    private void addToList(File file) {
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            String length = Model.getTime(audioFile.getAudioHeader().getTrackLength());
            String name = file.getName();
            tracks.add(new Track(name, length, file.getAbsolutePath()));
        } catch (TagException | ReadOnlyFileException | InvalidAudioFrameException | CannotReadException | IOException e) {
            e.printStackTrace();
        }
    }

    private void addToListView() {
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
                trackIndex = (trackList.getSelectionModel().getSelectedIndex());
                listPlay(trackIndex);
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.hide();
                contextMenu.show(trackList, event.getScreenX(), event.getScreenY());
            }
        });
    }


    private void listPlay(int index) {
        listPlay(index, true);
    }

    private void listPlay(int index, boolean play) {
        stop();
        if (index < 0) {
            trackIndex = tracks.size() - 1;
        } else if (index > tracks.size() - 1) {
            trackIndex = 0;
        } else {
            trackIndex = index;
        }

        newTrack();
        if (play) {
            play();
        }
        setParameters(trackIndex);
    }

    private void setParameters(int index) {
        this.name.setText(tracks.get(index).getName());
        this.fullTime.setText(tracks.get(index).getTime());
    }

    private void setVolume() {
        volume.valueProperty().addListener((observable, oldValue, newValue) -> {
            Player.setVolume(newValue.floatValue());
        });
    }

    void createPopupMenu() {
        contextMenu = new ContextMenu();
        contextMenu.setOnShowing(e -> {
        });
        contextMenu.setOnShown(e -> trackSelectPoPMenu = trackList.getSelectionModel().getSelectedIndex());

        MenuItem item1 = new MenuItem("Play");
        item1.setOnAction(e -> listPlay(trackSelectPoPMenu));

        MenuItem item2 = new MenuItem("Remove");
        item2.setOnAction(e -> {
            tracks.remove(trackSelectPoPMenu);
            addToListView();
        });
        contextMenu.getItems().addAll(item1, item2);
    }

    private void setColorList(int n) {
        trackList.getItems().get(prevSelect).setStyle("");
        trackList.getItems().get(n).setStyle("-fx-background-color: #e5e5e5");
        prevSelect = n;
    }

    private void setText(String string) {
        name.setText(string);
        TranslateTransition transTransition = new TranslateTransition(new Duration(5000));
        transTransition.setNode(name);
        transTransition.setToX(-name.getText().getBytes().length * 10);
        transTransition.setAutoReverse(true);
        transTransition.setCycleCount(5);
        transTransition.play();
    }
}
