package dk.easv.mytunes.gui;

import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.bll.util.DurationCalc;
import dk.easv.mytunes.gui.model.MyTunesModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Time;

public class SongViewController {

    private MainViewController parent;
    private Stage stage;
    private MyTunesModel myTunesModel;
    @FXML
    private TextField txtFldFilePath, txtFldTitle, txtFldArtist, txtFldDuration, txtFldNewTitle, txtFldChosenMovie, txtFldNewArtist, txtFldNewDuration;
    @FXML
    private ChoiceBox<String> cbCategory, cbNewCategory;
    @FXML
    private Button btnAdd;

    @FXML
    private void onChooseFileClick() {

        FileChooser fileChooser = new FileChooser();
        File song = fileChooser.showOpenDialog(stage);

        if (song == null) {return;}

        int dotIndex = song.getName().lastIndexOf(".");

        if (song.getName().substring(dotIndex).equals(".mp3") || song.getName().substring(dotIndex).equals(".wav")) {

            txtFldFilePath.setText(song.getPath());
            txtFldTitle.setDisable(false);
            txtFldArtist.setDisable(false);
            cbCategory.setDisable(false);

            try {
                txtFldDuration.setText(DurationCalc.getDuration(song));
            } catch (Exception e) {
                e.printStackTrace();
            }

            btnAdd.setDisable(false);

        } else {
            txtFldFilePath.setText("Invalid filetype!");
            txtFldTitle.setDisable(true);
            txtFldArtist.setDisable(true);
            cbCategory.setDisable(true);
            txtFldDuration.setText("");
            btnAdd.setDisable(true);
        }
    }

    @FXML
    private void onAddSongClick() {

        try {
            if (!txtFldTitle.getText().isBlank()) {

                myTunesModel.createSong(new Song(txtFldTitle.getText(), txtFldArtist.getText(), cbCategory.getValue(), Time.valueOf(txtFldDuration.getText()), txtFldFilePath.getText()));
                stage.close();

            }
        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    @FXML
    private void onEditSongClick() {

        if (!txtFldChosenMovie.getText().isBlank()) {
            
            stage.close();

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

}
