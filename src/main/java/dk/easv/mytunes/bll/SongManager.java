package dk.easv.mytunes.bll;

//project imports
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.IMyTunesDataAccess;
import dk.easv.mytunes.dal.MyTunesDAO_DB;

//java import
import java.util.List;

public class SongManager {

    private IMyTunesDataAccess myTunesDAO = new MyTunesDAO_DB();

    private PlaylistManager playlistManager;

    public SongManager() throws Exception {

        playlistManager = new PlaylistManager();

    }

    public void loadSongs() throws Exception {

        myTunesDAO.loadSongs();

    }

    public List<Song> getAllSongs() throws Exception {

        return myTunesDAO.getAllSongs();

    }

    public void createSong(Song newSong) throws Exception {

        myTunesDAO.createSong(newSong);

    }

    public void deleteSong(Song song) throws Exception {

        playlistManager.deleteSongFromAllPlaylists(song);

        myTunesDAO.deleteSong(song.getId());
    }

    public void addSongToPlaylist(Song song, Playlist playlist) throws Exception {

        myTunesDAO.addSongToPlaylist(song, playlist);

    }

    public void deleteSongFromPlaylist(Song song, Playlist playlist) throws Exception {

        myTunesDAO.deleteSongFromPlaylist(song, playlist);

    }

    public void editSong(Song selectedSong, Song newSong) throws Exception{

        myTunesDAO.editSong(selectedSong, newSong);

    }

}
