package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;

import java.sql.Time;
import java.util.List;

public interface IMyTunesDataAccess {


    void createSong(Song song) throws Exception;

    void deleteSong(int songId) throws Exception;

    void loadSongs() throws Exception;

    void createPlaylist(Playlist playlist) throws Exception;

    void editPlaylist(Playlist playlist) throws Exception;

    void deletePlaylist(Playlist playlist) throws Exception;

    List<Song> getAllSongs() throws Exception;

    List<Playlist> getAllPlaylists() throws Exception;


}
