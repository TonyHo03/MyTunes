package dk.easv.mytunes.gui.model;

import dk.easv.mytunes.bll.PlaylistManager;
import dk.easv.mytunes.bll.SongManager;

public class MyTunesModel {

    private PlaylistManager playlistManager = new PlaylistManager();
    private SongManager songManager = new SongManager();

    public MyTunesModel() throws Exception {
    }

    public void loadSongs() throws Exception {

        songManager.loadSongs();

    }

}
