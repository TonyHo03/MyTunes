package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.db.DBConnector;

import java.util.List;

public class MyTunesDAO_DB implements IMyTunesDataAccess{

    private DBConnector dbConnector = new DBConnector();

    public MyTunesDAO_DB() throws Exception {}

    @Override
    public void createSong() throws Exception {

    }

    @Override
    public void createPlaylist() throws Exception {

    }

    @Override
    public List<Song> getAllSongs() throws Exception {
        return List.of();
    }

    @Override
    public List<Playlist> getAllPlaylists() throws Exception {
        return List.of();
    }
}
