package dk.easv.mytunes.gui;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.gui.model.MyTunesModel;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import java.sql.Time;

public class PlaylistViewController {

    @FXML
    private Stage stage;
    @FXML
    private MainViewController parentController;

    @FXML
    private TextField txtFldPlaylistName, txtFldPlaylistNameEdit;
    private MyTunesModel myTunesModel = new MyTunesModel();

    public PlaylistViewController() throws Exception {
    }

    public void onCreatePlaylistClick() throws Exception {
        myTunesModel.createNewPlaylist(new Playlist(0, txtFldPlaylistName.getText(), 0, Time.valueOf("00:00:00")));
        stage.close();
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setParentController(MainViewController parentController) {
        this.parentController = parentController;
    }

    public void onCancelClick() {
        stage.close();
    }

    public void onSavePlaylistClick() throws Exception {
        myTunesModel.savePlaylist(new Playlist(0, txtFldPlaylistNameEdit.getText(), 0, Time.valueOf("00:00:00")));
        stage.close();
    }
}
