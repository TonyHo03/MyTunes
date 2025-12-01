package dk.easv.mytunes.gui;

import dk.easv.mytunes.Main;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.gui.model.MyTunesModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {


    private MyTunesModel myTunesModel;


    public TableView<Song> tblSongs;
    @FXML
    private ListView<Song> lstPlaylistSong;
    @FXML
    private TableColumn<Song, String> titleColumn, artistColumn, categoryColumn, timeColumn;
    @FXML
    private TextField txtFldFilter;
    @FXML
    private Button btnSearch;
    @FXML
    private Label lblPlayBtn;
    @FXML
    private Label lblCurrentSong;
    @FXML
    private Button btnPlay;
    @FXML
    private Slider sldrVolume;
    @FXML
    private Slider sldrPlayback;


    public TableView<Playlist> tblPlaylist;
    @FXML
    private TableColumn<Playlist, String> clmName;
    @FXML
    private TableColumn<Playlist, Integer> clmSongs;
    @FXML
    private TableColumn<Playlist, Time> clmDuration;

    private boolean isFiltering = false;

    private boolean isPlaying = false;

    private Song currentSong = null;

    private Playlist selectedPlaylist;

    private URI uri;

    private Media media;

    private MediaPlayer player;

    private ObservableList<Song> currentSongList = FXCollections.observableArrayList();

    private int currentSongIndex = 0;

    public MainViewController() {
        try {
            myTunesModel = new MyTunesModel();
            myTunesModel.loadSongs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCurrentSong() {

        currentSong = currentSongList.get(currentSongIndex);

        uri = Paths.get(currentSong.getFilePath()).toUri();

        media = new Media(uri.toString());

        player = new MediaPlayer(media);


    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("Title"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("Artist"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("Category"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("Duration"));

        clmName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        clmSongs.setCellValueFactory(new PropertyValueFactory<>("Songs"));
        clmDuration.setCellValueFactory(new PropertyValueFactory<>("Duration"));

        tblSongs.setItems(myTunesModel.getObservableSongs());
        tblPlaylist.setItems(myTunesModel.getPlaylist());

        tblPlaylist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            try {
                selectedPlaylist = newValue;
                if (selectedPlaylist != null) {
                    currentSongList = myTunesModel.getSongObservableList2(selectedPlaylist);
                    lstPlaylistSong.setItems(currentSongList);
                    currentSong = currentSongList.getFirst();
                }
                updateCurrentSong();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        // Lytter til værdiændring af volume-slider, og ændrer lydstyrke baseret på dens nye værdi.
        sldrVolume.valueProperty().addListener((obs, oldValue, newValue) -> {

            if (player != null) {

                player.setVolume(newValue.doubleValue()/100);
            }

        });
        /*
        sldrPlayback.valueProperty().addListener((obs, oldValue, newValue) -> {

            if (player != null) {

                double durationInSeconds = player.getTotalDuration().toSeconds();

                player.seek(Duration.seconds(durationInSeconds / (newValue.doubleValue()/100)));
            }

        });*/

    }

    @FXML
    private void onSearchBtnClick() {

        if (!isFiltering) {
            btnSearch.setText("C");
            FilteredList<Song> filteredList = new FilteredList<>(myTunesModel.getObservableSongs());
            filteredList.setPredicate(song -> {

                if (txtFldFilter.getText().isBlank()) {
                    return true;
                }

                String filter = txtFldFilter.getText().toLowerCase();

                if (song.getTitle().toLowerCase().contains(filter)) {
                    return true;
                } else if (song.getArtist().toLowerCase().contains(filter)) {
                    return true;
                } else if (song.getCategory().toLowerCase().contains(filter)) {
                    return true;
                } else {
                    return false;
                }

            });

            ObservableList<Song> newList = FXCollections.observableArrayList();
            newList.addAll(filteredList);
            tblSongs.setItems(newList);
        } else {

            btnSearch.setText("\uD83D\uDD0E");
            tblSongs.setItems(myTunesModel.getObservableSongs());

        }

        isFiltering = !isFiltering;
    }

    @FXML
    private void onNewPlaylistClick(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/views/PlaylistView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();


            stage.setTitle("New Playlist");
            stage.setScene(scene);

            PlaylistViewController controller = fxmlLoader.getController();
            controller.setParentController(this);
            controller.setStage(stage);
            controller.setModel(myTunesModel);
            stage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeletePlaylistClick(ActionEvent actionEvent) {
        Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();

        if  (selectedPlaylist != null) {
            try {
                myTunesModel.deletePlaylist(selectedPlaylist);

                System.out.println(myTunesModel.getPlaylist());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void btnOnNewSong(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/views/SongView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("New/Edit Song");

            stage.initModality(Modality.APPLICATION_MODAL);

            SongViewController controller = fxmlLoader.getController();
            controller.setParentController(this);
            controller.setModel(myTunesModel);
            controller.setStage(stage);

            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e) {

        }

    }

    @FXML
    private void deleteSong(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/views/DeleteSongView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Delete song confirmation");
        stage.setScene(scene);

        DeleteSongViewController controller = fxmlLoader.getController();
        controller.setParentController(this);
        controller.setSongToDelete(tblSongs.getSelectionModel().getSelectedItem());
        controller.setModel(myTunesModel);
        controller.setStage(stage);

        stage.show();
    }

    @FXML
    private void onAddSongToPlaylistClick() {

        try {

            Song selectedSong = tblSongs.getSelectionModel().getSelectedItem();
            Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();

            if (selectedSong != null && selectedPlaylist != null) {
                myTunesModel.addSongToPlaylist(selectedSong, selectedPlaylist);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void onDeleteSongPlaylistClick() {
        try {
            Song selectedSong = lstPlaylistSong.getSelectionModel().getSelectedItem();
            Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();

            myTunesModel.deleteSongFromPlaylist(selectedSong, selectedPlaylist);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onUpBtnClick() {

        if (isPlaying) {
            btnPlay.fire();
        }

        try {
            Song selectedSong = lstPlaylistSong.getSelectionModel().getSelectedItem();
            int previousIndex = lstPlaylistSong.getSelectionModel().getSelectedIndex();
            int newIndex = (previousIndex > 0) ? previousIndex - 1 : 0;

            currentSongList.remove(previousIndex);
            currentSongList.add(newIndex, selectedSong);

            System.out.println(previousIndex + " : " + newIndex + " - " + selectedSong.getTitle());

            lstPlaylistSong.getSelectionModel().select(newIndex);

            updateCurrentSong();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void onDownBtnClick() {

        if (isPlaying) {
            btnPlay.fire();
        }

        try {
            Song selectedSong = lstPlaylistSong.getSelectionModel().getSelectedItem();
            int previousIndex = lstPlaylistSong.getSelectionModel().getSelectedIndex();

            int newIndex = (previousIndex < lstPlaylistSong.getItems().size() - 1) ? previousIndex + 1 : lstPlaylistSong.getItems().size() - 1;

            currentSongList.remove(previousIndex);
            currentSongList.add(newIndex, selectedSong);


            lstPlaylistSong.getSelectionModel().select(newIndex);

            updateCurrentSong();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onPlayBtnClick() {

        if (currentSong == null) {return;}

        isPlaying = !isPlaying;

        if (isPlaying) {
            System.out.println("Playing");
            lblPlayBtn.setStyle("-fx-font-size: 24px");
            lblPlayBtn.setText("⏸");
            lblCurrentSong.setText(currentSong.toString() + " is currently playing.");

            player.play();

        }
        else {
            System.out.println("Paused");
            lblPlayBtn.setStyle("-fx-font-size: 28px");
            lblPlayBtn.setText("▶");
            lblCurrentSong.setText("Nothing is currently playing.");

            player.pause();

        }

    }

    @FXML
    private void onNextTrackClick() {

        player.stop();

        if (selectedPlaylist != null) {

            int indexOfSong = 0;

            for (int i = 0; i < currentSongList.size(); i++) {

                    if (currentSong.getTitle().equals(currentSongList.get(i).getTitle())) {

                        indexOfSong = i;
                        break;

                    }

                }

            if (indexOfSong < currentSongList.size() - 1) {

                currentSongIndex = indexOfSong + 1;

                currentSong = currentSongList.get(currentSongIndex);

                if (isPlaying) {
                    btnPlay.fire();
                }

                updateCurrentSong();

            }
        }
    }

    @FXML
    private void onPreviousTrackClick() {

        player.stop();

        if (selectedPlaylist != null) {

            int indexOfSong = 0;

            for (int i = 0; i < currentSongList.size(); i++) {

                if (currentSong.getTitle().equals(currentSongList.get(i).getTitle())) {

                    indexOfSong = i;
                    break;

                }

            }

            if (indexOfSong > 0) {

                currentSongIndex = indexOfSong - 1;

                currentSong = currentSongList.get(currentSongIndex);

                if (isPlaying) {
                    btnPlay.fire();
                }

                updateCurrentSong();

            }

        }
    }
}