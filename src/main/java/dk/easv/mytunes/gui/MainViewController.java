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
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Time;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {


    private MyTunesModel myTunesModel;

    @FXML
    private TableView<Song> tblSongs;
    @FXML
    private ListView<String> lstPlaylistSong;
    @FXML
    private TableColumn<Song, String> titleColumn, artistColumn, categoryColumn, timeColumn;
    @FXML
    private TextField txtFldFilter;
    @FXML
    private Button btnSearch;


    public TableView<Playlist> tblPlaylist;
    @FXML
    private TableColumn<Playlist, String> clmName;
    @FXML
    private TableColumn<Playlist, Integer> clmSongs;
    @FXML
    private TableColumn<Playlist, Time> clmDuration;

    @FXML
    private DialogPane newSongUIPopUp;
    @FXML
    private TextField txtTitle, txtArtist, txtTime, txtFile;
    @FXML
    private ChoiceBox<String> txtCategory;

    private boolean isFiltering = false;

    public MainViewController() {
        try {
            myTunesModel = new MyTunesModel();
            myTunesModel.loadSongs();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                lstPlaylistSong.getItems().clear();
                for (Song song : myTunesModel.getSongObservableList2(newValue)) {
                    lstPlaylistSong.getItems().add(song.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        //lstPlaylistSong.setItems(myTunesModel.getObservableSongs());
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
            stage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeletePlaylistClick(ActionEvent actionEvent) {
        Playlist selectedPlaylist = (Playlist) tblPlaylist.getSelectionModel().getSelectedItem();

        if  (selectedPlaylist != null) {
            try {
                myTunesModel.deletePlaylist(selectedPlaylist);

                tblPlaylist.refresh();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void btnOnNewSong(ActionEvent actionEvent) {
        newSongUIPopUp.setVisible(true);
    }

    @FXML
    private void deleteSong(ActionEvent event) throws Exception {
        Song selectedSong = tblSongs.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            try
            {
                myTunesModel.deleteSong(selectedSong);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onBtnCancel(ActionEvent actionEvent) {
        newSongUIPopUp.setVisible(false);
    }

    @FXML
    private void onBtnSave(ActionEvent actionEvent) throws Exception {
        newSongUIPopUp.setVisible(false);

        try {
            Song newSong = new Song(txtTitle.getText(), txtArtist.getText(), txtCategory.getValue(), Time.valueOf(txtTime.getText()), txtFile.getText());

            myTunesModel.createSong(newSong);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAddSongToPlaylistClick() {

        try {
            Song selectedSong = tblSongs.getSelectionModel().getSelectedItem();
            Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();

            myTunesModel.addSongToPlaylist(selectedSong, selectedPlaylist);
        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    @FXML
    private void onBtnEditSong(ActionEvent actionEvent) {


    }

    @FXML
    private void onBtnEditSongCancel(ActionEvent actionEvent) {


    }

    @FXML
    private void onBtnEditSongSave(ActionEvent actionEvent) {


    }
}
