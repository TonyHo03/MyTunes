package dk.easv.mytunes.be;

import java.sql.Time;

public class Playlist {
    private int id, songs;
    private String name;
    private Time duration;

    public Playlist(int id, String name, int songs, Time duration) {
        this.id = id;
        this.name = name;
        this.songs = songs;
        this.duration = duration;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSongs() {
        return songs;
    }

    public void setSongs(int Songs) {
        this.songs = Songs;
    }
    public Time getDuration() {
        return duration;
    }

    public void setDuration(Time Duration) {
        this.duration = Duration;
    }

    @Override
    public String toString() {
        return getName();
    }
}