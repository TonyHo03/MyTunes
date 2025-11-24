package dk.easv.mytunes.bll;

import dk.easv.mytunes.dal.IMyTunesDataAccess;
import dk.easv.mytunes.dal.MyTunesDAO_DB;

public class SongManager {

    private MyTunesDAO_DB myTunesDAO = new MyTunesDAO_DB();

    public SongManager() throws Exception {}

    public void loadSongs() throws Exception {

        myTunesDAO.loadSongs();

    }

}
