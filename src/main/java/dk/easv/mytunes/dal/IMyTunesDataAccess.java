package dk.easv.mytunes.dal;

//project imports
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.PlaylistSong;
import dk.easv.mytunes.be.Song;

//java import
import java.util.List;

public interface IMyTunesDataAccess {


    void createSong(Song song) throws Exception;

    void deleteSong(int songId) throws Exception;

    void loadSongs() throws Exception;

    void createPlaylist(Playlist playlist) throws Exception;

    void editPlaylist(Playlist oldPlaylist, Playlist newplaylist) throws Exception;

    void deletePlaylist(Playlist playlist) throws Exception;

    void updatePlaylistInfo(Playlist playlist) throws Exception;

    void editSong(Song selectedSong, Song newSong) throws Exception;

    void addSongToPlaylist(Song song, Playlist playlist) throws Exception;

    void deleteSongFromPlaylist(Song song, Playlist playlist) throws Exception;

    List<Song> getAllSongs() throws Exception;

    List<Playlist> getAllPlaylists() throws Exception;

    List<Song> getAllSongsFromPlaylist(Playlist playlist) throws Exception;

    List<PlaylistSong> loadPlaylistSongs() throws Exception;
}
