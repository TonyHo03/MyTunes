package dk.easv.mytunes.gui;

//project imports
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.gui.model.MyTunesModel;
//java imports
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javax.swing.*;


public class DeleteSongViewController {

    private MainViewController parent;
    private Song songToDelete;
    private Stage stage;
    private MyTunesModel myTunesModel;

    public DeleteSongViewController() {
        try {
            myTunesModel = new MyTunesModel();
        } catch (Exception e) {
            }
    }

    public void setStage (Stage stage){
        this.stage = stage;
    }

    public void setParentController(MainViewController parent) {
        this.parent = parent;
    }
        public void setModel(MyTunesModel model) {
        this.myTunesModel = model;
    }

    public void setSongToDelete (Song song) {
        this.songToDelete = song;
    }

    @FXML
    private void onDontDeleteClick(ActionEvent actionEvent) {
        stage.close();
    }

    @FXML
    private void onDeleteSongfileClick(ActionEvent actionEvent) {
        try {
            if (parent != null) {
                parent.stopPlayback();
            }
            myTunesModel.deleteSong(songToDelete);
        } catch (Exception e) {
            e.printStackTrace();
        }
        stage.close();
    }

    }

