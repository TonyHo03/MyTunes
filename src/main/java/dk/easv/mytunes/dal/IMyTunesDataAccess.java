package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;

import java.util.List;

public interface IMyTunesDataAccess {


    void createSong() throws Exception;

    void createPlaylist() throws Exception;

    List<Song> getAllSongs() throws Exception;

    List<Playlist> getAllPlaylists() throws Exception;


}
