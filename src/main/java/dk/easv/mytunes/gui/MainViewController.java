package dk.easv.mytunes.gui;

import dk.easv.mytunes.gui.model.MyTunesModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainViewController  {

    private MyTunesModel myTunesModel;

    public MainViewController() {
        try {
            myTunesModel = new MyTunesModel();
            myTunesModel.loadSongs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
