package dk.easv.mytunes.gui.model;

//project imports
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.bll.PlaylistManager;
import dk.easv.mytunes.bll.SongManager;
//java imports
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

    public ObservableList<Playlist> getPlaylist() {return playlistObservableList;}

    public ObservableList<Song> getSongObservableList2(Playlist playlist) throws Exception {

        songObservableList2.setAll(playlistManager.getAllSongsFromPlaylist(playlist));

        return songObservableList2;
    }

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

        for (Playlist playlist : playlistObservableList) {
            if (songObservableList2.contains(songToBeDeleted)) {
                songObservableList2.remove(songToBeDeleted);
            }
        }
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

    public void addSongToPlaylist(Song song, Playlist playlist) throws Exception {

        songManager.addSongToPlaylist(song, playlist);

        songObservableList2.add(song);

        playlistObservableList.setAll(playlistManager.getAllPlaylists());

    }

    public void deleteSongFromPlaylist(Song song, Playlist playlist) throws Exception {
        songManager.deleteSongFromPlaylist(song, playlist);

        Song foundSong = null;

        for (Song selectedSong: songObservableList2) {

            if (song.getTitle().equals(selectedSong.getTitle())) {

                foundSong = selectedSong;

            }
        }

        if (foundSong != null) {
            songObservableList2.remove(foundSong);
        }

        playlistObservableList.setAll(playlistManager.getAllPlaylists());
    }

    public void editSong(Song selectedSong, Song newSong) throws Exception{
        songManager.editSong(selectedSong, newSong);

        songObservableList.setAll(songManager.getAllSongs());
    }

}
