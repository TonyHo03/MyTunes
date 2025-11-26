package dk.easv.mytunes.be;

import java.sql.Time;

public class Song {

    private int id;
    private String title;
    private String artist;
    private String category;
    private Time duration;
    private String filePath;

    public Song(int Id, String Title, String Artist, String Category, Time Duration, String FilePath) {

        this.id = Id;
        this.title = Title;
        this.artist = Artist;
        this.category = Category;
        this.duration = Duration;
        this.filePath = FilePath;

    }

    public Song(String Title, String Artist, String Category, Time Duration, String FilePath) {

        this.title = Title;
        this.artist = Artist;
        this.category = Category;
        this.duration = Duration;
        this.filePath = FilePath;

    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String Title) {
        this.title = Title;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String Artist) {
        this.artist = Artist;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String Category) {
        this.category = Category;
    }

    public Time getDuration() {
        return this.duration;
    }

    public void setDuration(Time Duration) {
        this.duration = Duration;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String FilePath) {
        this.filePath = FilePath;
    }

}
