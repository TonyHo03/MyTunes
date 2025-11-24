package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.db.DBConnector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.nio.file.Files;
import java.io.File;
import java.sql.*;
import java.sql.Time;
import java.util.List;

public class MyTunesDAO_DB implements IMyTunesDataAccess{

    private DBConnector dbConnector = new DBConnector();

    private final String PATH_STRING = "src/main/resources/dk/easv/mytunes/songs";

    public MyTunesDAO_DB() throws Exception {}

    @Override
    public void createSong(String title, String artist, String category, Time time, String filePath) throws Exception {

        try (Connection conn = dbConnector.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("INSERT INTO dbo.Songs (Title, Artist, Category, File)");

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

            String sql = "SELECT * FROM dbo.Songs";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                int Id = rs.getInt("Id");
                String Title = rs.getString("Title");
                String Artist = rs.getString("Artist");
                String Category = rs.getString("Category");
                Time Time = rs.getTime("Time");
                String File = rs.getString("File");

                for (File song: songs) {

                    if (!song.getPath().equals(File)) {

                        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(song);
                        AudioFormat format = audioInputStream.getFormat();
                        double duration = audioInputStream.getFrameLength() / format.getFrameRate();

                        int durationInSeconds = (int) duration;
                        int hours = durationInSeconds / 3600;
                        int minutes = (durationInSeconds % 3600) / 60;
                        int seconds = durationInSeconds % 60;

                        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                        Time time = java.sql.Time.valueOf(timeString);


                        String tempTitle = song.getName().split(".")[1];
                        createSong(tempTitle, "", "", time, song.getPath());
                        break;

                    }
                }
            }
        }

        for (File song: songs) {

            System.out.println(song.getName());

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
