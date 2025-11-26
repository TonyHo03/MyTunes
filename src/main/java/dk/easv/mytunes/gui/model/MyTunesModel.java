package dk.easv.mytunes.gui.model;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.bll.PlaylistManager;
import dk.easv.mytunes.bll.SongManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MyTunesModel {

    private PlaylistManager playlistManager = new PlaylistManager();
    private SongManager songManager = new SongManager();
    private ObservableList<Song> songObservableList;
    private ObservableList<Playlist> playlistObservableList;

    public MyTunesModel() throws Exception {
        songObservableList = FXCollections.observableArrayList();
        playlistObservableList = FXCollections.observableArrayList();
        songObservableList.addAll(songManager.getAllSongs());
        playlistObservableList.addAll(playlistManager.getAllPlaylists());
    }

    public ObservableList<Song> getObservableSongs() {return songObservableList;}

    public void loadSongs() throws Exception {

        songManager.loadSongs();

    }

    public void createSong(Song newSong) throws Exception {

        songManager.createSong(newSong);

        songObservableList.add(newSong);

    }

    public void deleteSong(Song songToBeDeleted) throws Exception {

        songManager.deleteSong(songToBeDeleted);

        songObservableList.remove(songToBeDeleted);
    }

    public void createNewPlaylist(Playlist playlist) throws Exception {
        playlistManager.createPlaylist(playlist);
    }

    public void savePlaylist(Playlist playlist) throws Exception {
        playlistManager.editPlaylist(playlist);
    }

    public void deletePlaylist(Playlist playlist) throws Exception {
        playlistManager.deletePlaylist(playlist);

        playlistObservableList.remove(playlist);
    }
    public ObservableList<Playlist> getPlaylist() {
        return playlistObservableList;
    }

}
