package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.db.DBConnector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.nio.file.Files;
import com.mpatric.mp3agic.Mp3File;
import java.io.File;
import java.sql.*;
import java.sql.Time;
import java.util.List;

public class MyTunesDAO_DB implements IMyTunesDataAccess{

    private DBConnector dbConnector = new DBConnector();

    private final String PATH_STRING = "src/main/resources/dk/easv/mytunes/songs";

    public MyTunesDAO_DB() throws Exception {}

    @Override
    public void createSong(String title, String artist, String category, Time duration, String filePath) throws Exception {

        try (Connection conn = dbConnector.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("INSERT INTO dbo.Song (Title, Artist, Category, Duration, FilePath) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, title);
            ps.setString(2, artist);
            ps.setString(3, category);
            ps.setTime(4, duration);
            ps.setString(5, filePath);

            ps.execute();

        } catch (SQLException e) {

            e.printStackTrace();
            throw new Exception("Could not create song.");

        }
    }

    @Override
    public void loadSongs() throws Exception {

        File songFolder = new File(PATH_STRING);

        File[] songs = songFolder.listFiles();

        if (songs == null) {
            return;
        }

        try (Connection conn = dbConnector.getConnection()) {

            Statement stmt = conn.createStatement();

            String sql = "SELECT * FROM dbo.Song";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                int Id = rs.getInt("Id");
                String Title = rs.getString("Title");
                String Artist = rs.getString("Artist");
                String Category = rs.getString("Category");
                Time Time = rs.getTime("Duration");
                String file = rs.getString("FilePath");

                for (File song: songs) {
                    System.out.println("looping");
                    if (!song.getPath().equals(file)) {
                        System.out.println("New Song");

                        Mp3File mp3File = new Mp3File("src/main/resources/dk/easv/mytunes/songs/" + song.getName());
                        double duration = mp3File.getLengthInSeconds();

                        int durationInSeconds = (int) duration;
                        int hours = durationInSeconds / 3600;
                        int minutes = (durationInSeconds % 3600) / 60;
                        int seconds = durationInSeconds % 60;

                        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                        Time time = java.sql.Time.valueOf(timeString);


                        String tempTitle = song.getName().split("\\.")[0];
                        createSong(tempTitle, "", "", time, song.getPath());
                        break;

                    }
                }
            }
        } catch (SQLException e) {

            e.printStackTrace();
            throw new Exception("Could not load songs.");

        }

    }

    @Override
    public void createPlaylist() throws Exception {

    }

    @Override
    public List<Song> getAllSongs() throws Exception {
        return List.of();
    }

    @Override
    public List<Playlist> getAllPlaylists() throws Exception {
        return List.of();
    }
}
