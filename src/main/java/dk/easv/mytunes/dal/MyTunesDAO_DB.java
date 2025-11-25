package dk.easv.mytunes.dal;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.db.DBConnector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
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

        // Hvis der er ingen sange i mappen, så skal funktionen ikke køre.
        if (songs == null) {
            return;
        }

        try (Connection conn = dbConnector.getConnection()) {

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            String sql = "SELECT * FROM dbo.Song";
            ResultSet rs = stmt.executeQuery(sql);

            // Tjekker om der er overhovedet er rækker i resultset. Hvis ikke, så er der ingen sange i databasen.
            if (!rs.next()) {
                System.out.println("Der er INGEN sange i databasen. Indsætter alle sange.");

                for (File song: songs) {
                    String timeString = getDuration(song);
                    Time time = java.sql.Time.valueOf(timeString);

                    String tempTitle = song.getName().split("\\.")[0];

                    createSong(tempTitle, "", "", time, song.getPath());
                }

                return;

            } else {rs.beforeFirst();}

            // Loop igennem alle sange for at tjekke om de allerede eksisterer i databasen.
            for (File song: songs) {

                int rowCount = 0;
                int notFound = 0;

                // Loop igennem resultset.
                while (rs.next()) {
                    rowCount++;

                    int Id = rs.getInt("Id");
                    String Title = rs.getString("Title");
                    String Artist = rs.getString("Artist");
                    String Category = rs.getString("Category");
                    Time Time = rs.getTime("Duration");
                    String file = rs.getString("FilePath");

                    // Hvis sangen ikke har samme filsti som rækken i databasen, så tæller den "notFound" op.
                    if (!song.getPath().equals(file)) {
                        notFound++;
                    }
                }

                // Hvis true, så betyder det at sangen ikke fandtes i alle rækker af resultset.
                if (notFound == rowCount) {

                    rowCount = 0;
                    notFound = 0;
                    System.out.println("Sangen er ikke i databasen: " + rowCount + " " + notFound);

                    Time time = Time.valueOf(getDuration(song));
                    String tempTitle = song.getName().split("\\.")[0];

                    createSong(tempTitle, "", "", time, song.getPath());
                }

                // Resetter resultset, så den viser den første række igen.
                rs.beforeFirst();
            }
        } catch (SQLException e) {

            e.printStackTrace();
            throw new Exception("Could not load songs.");
        }
    }

    private static String getDuration(File song) throws IOException, UnsupportedTagException, InvalidDataException {
        Mp3File mp3File = new Mp3File("src/main/resources/dk/easv/mytunes/songs/" + song.getName());
        double duration = mp3File.getLengthInSeconds();

        int durationInSeconds = (int) duration;
        int hours = durationInSeconds / 3600;
        int minutes = (durationInSeconds % 3600) / 60;
        int seconds = durationInSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
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
