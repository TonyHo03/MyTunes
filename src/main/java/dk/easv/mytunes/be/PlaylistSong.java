package dk.easv.mytunes.be;

public class PlaylistSong {
    private int id, songId, playlistId;

    public PlaylistSong(int id, int songId, int playlistId)
    {
        this.id = id;
        this.songId = songId;
        this.playlistId = playlistId;
    }
    public int  getId()
    {
        return id;
    }
    public int setId(int id)
    {
        this.id = id;
        return id;
    }
    public int getSongId()
    {
        return songId;
    }
    public int getPlaylistId()
    {
        return playlistId;
    }
}
