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
    private ObservableList<Song> songObservableList2;

    public MyTunesModel() throws Exception {
        songObservableList = FXCollections.observableArrayList();
        songObservableList2 = FXCollections.observableArrayList();
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

        playlistObservableList.add(playlist);
    }

    public void savePlaylist(Playlist oldPlaylist, Playlist newplaylist) throws Exception {
        playlistManager.editPlaylist(oldPlaylist,newplaylist);
    }

    public void deletePlaylist(Playlist playlist) throws Exception {
        playlistManager.deletePlaylist(playlist);

        playlistObservableList.remove(playlist);
    }
    public ObservableList<Playlist> getPlaylist() {
        return playlistObservableList;
    }
    public void loadPlaylistSongs() throws Exception {
        songManager.loadSongs();
    }

    public ObservableList<Song> getSongObservableList2(Playlist playlist) throws Exception {
        songObservableList2.setAll(playlistManager.getAllSongsFromPlaylist(playlist));;
        return songObservableList2;
    }

    public void addSongToPlaylist(Song song, Playlist playlist) throws Exception {

        songManager.addSongToPlaylist(song, playlist);

        playlistObservableList.setAll(playlistManager.getAllPlaylists());

    }

}
