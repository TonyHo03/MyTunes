package dk.easv.mytunes.bll;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.IMyTunesDataAccess;
import dk.easv.mytunes.dal.MyTunesDAO_DB;

import java.util.List;

public class SongManager {

    private IMyTunesDataAccess myTunesDAO = new MyTunesDAO_DB();

    public SongManager() throws Exception {}

    public void loadSongs() throws Exception {

        myTunesDAO.loadSongs();

    }

    public List<Song> getAllSongs() throws Exception {

        return myTunesDAO.getAllSongs();

    }

    public void createSong(Song newSong) throws Exception {

        myTunesDAO.createSong(newSong);

    }

    public void deleteSong(Song song) throws Exception
    {
        myTunesDAO.deleteSong(song.getId());
    }

    public void addSongToPlaylist(Song song, Playlist playlist) throws Exception {

        myTunesDAO.addSongToPlaylist(song, playlist);

    }
    public void deleteSongFromPlaylist(String song, String playlist) throws Exception {
        myTunesDAO.deleteSongFromPlaylist(song, playlist);
    }

}
