package dk.easv.mytunes.bll;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.PlaylistSong;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.MyTunesDAO_DB;

import java.util.List;

public class PlaylistManager {
    private MyTunesDAO_DB myTunesDAO = new MyTunesDAO_DB();

    public PlaylistManager() throws Exception {
    }
    public void createPlaylist(Playlist playlist) throws Exception {
        myTunesDAO.createPlaylist(playlist);
    }
    public void editPlaylist(Playlist oldPlaylist, Playlist newplaylist) throws Exception {
        myTunesDAO.editPlaylist(oldPlaylist,newplaylist);
    }
    public void deletePlaylist(Playlist playlist) throws Exception {
        myTunesDAO.deletePlaylist(playlist);
    }
    public List<Playlist> getAllPlaylists() throws Exception {
        return myTunesDAO.getAllPlaylists();
    }
    public List<Song> getAllSongsFromPlaylist(Playlist playlist) throws Exception {
        return myTunesDAO.getAllSongsFromPlaylist(playlist);
    }
    public void loadPlaylistSongs() throws Exception {
        myTunesDAO.loadPlaylistSongs();
    }
}
