package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;

import java.sql.Time;
import java.util.List;

public interface IMyTunesDataAccess {


    void createSong(String title, String artist, String category, Time time, String filePath) throws Exception;

    void loadSongs() throws Exception;

    void createPlaylist() throws Exception;

    List<Song> getAllSongs() throws Exception;

    List<Playlist> getAllPlaylists() throws Exception;


}
