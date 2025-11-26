package dk.easv.mytunes.dal;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.db.DBConnector;

import java.io.IOException;
import com.mpatric.mp3agic.Mp3File;
import java.io.File;
import java.sql.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class MyTunesDAO_DB implements IMyTunesDataAccess{

    private DBConnector dbConnector = new DBConnector();

    private final String PATH_STRING = "src/main/resources/dk/easv/mytunes/songs";

    public MyTunesDAO_DB() throws Exception {}

    @Override
    public void createSong(Song song) throws Exception {

        try (Connection conn = dbConnector.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("INSERT INTO dbo.Song (Title, Artist, Category, Duration, FilePath) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, song.getTitle());
            ps.setString(2, song.getArtist());
            ps.setString(3, song.getCategory());
            ps.setTime(4, song.getDuration());
            ps.setString(5, song.getFilePath());

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

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM dbo.Song",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = stmt.executeQuery();

            // Tjekker om der er overhovedet er rækker i resultset. Hvis ikke, så er der ingen sange i databasen.
            if (!rs.next()) {
                System.out.println("Der er INGEN sange i databasen. Indsætter alle sange.");

                for (File song: songs) {
                    String timeString = getDuration(song);
                    Time time = java.sql.Time.valueOf(timeString);


                    String tempTitle = song.getName().split("\\.")[0];

                    createSong(new Song(0, tempTitle, "", "", time, song.getPath()));
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

                    String FilePath = rs.getString("FilePath");

                    // Hvis sangen ikke har samme filsti som rækken i databasen, så tæller den "notFound" op.
                    if (!song.getPath().equals(FilePath)) {
                        notFound++;
                    }
                }

                // Hvis true, så betyder det at sangen ikke fandtes i alle rækker af resultset.
                if (notFound == rowCount) {
                    System.out.println("Sangen er ikke i databasen: " + rowCount + " " + notFound);

                    Time time = Time.valueOf(getDuration(song));
                    String tempTitle = song.getName().split("\\.")[0];

                    createSong(new Song(0, tempTitle, "", "", time, song.getPath()));
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
    public void createPlaylist(Playlist playlist) throws Exception {

        try (Connection conn = dbConnector.getConnection()){
            System.out.println("Creating playlist " + playlist.getName());
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO dbo.Playlist (Name, Songs, Duration) Values (?, ?, ?)");
            stmt.setString(1,playlist.getName());
            stmt.setInt(2,playlist.getSongs());
            stmt.setTime(3,playlist.getDuration());
            stmt.execute();

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Could not create playlist.");
        }

    }

    @Override
    public void editPlaylist(Playlist playlist) throws Exception {
        try (Connection conn = dbConnector.getConnection()){
            System.out.println("Editing playlist " + playlist.getName());
            PreparedStatement stmt = conn.prepareStatement("UPDATE dbo.PLaylist SET Name = ? WHERE Name = ?");
            stmt.setString(1,playlist.getName());
            stmt.executeUpdate();
        }
    }

    @Override
    public void  deletePlaylist(Playlist playlist) throws Exception {
        try (Connection conn = dbConnector.getConnection()){
            System.out.println("Deleting playlist " + playlist.getName());
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM dbo.PLaylist WHERE Name = ?");
            stmt.setString(1,playlist.getName());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Song> getAllSongs() throws Exception {

        List<Song> songList = new ArrayList<>();

        try (Connection conn = dbConnector.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM dbo.Song");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int Id = rs.getInt("Id");
                String Title = rs.getString("Title");
                String Artist = rs.getString("Artist");
                String Category = rs.getString("Category");
                Time Duration = rs.getTime("Duration");
                String FilePath = rs.getString("FilePath");

                songList.add(new Song(Id, Title, Artist, Category, Duration, FilePath));

            }

            return songList;

        } catch (SQLException e) {

            e.printStackTrace();
            throw new Exception("Could not fetch all songs.");

        }
    }

    @Override
    public List<Playlist> getAllPlaylists() throws Exception {
        List<Playlist> playlist = new ArrayList<>();

        try (Connection conn = dbConnector.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM dbo.Playlist");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int Id = rs.getInt("Id");
                String Name = rs.getString("Name");
                int Songs = rs.getInt("Songs");
                Time Duration = rs.getTime("Duration");

                playlist.add(new Playlist(Id, Name, Songs, Duration));

            }

            return playlist;

        } catch (SQLException e) {

            e.printStackTrace();
            throw new Exception("Could not fetch all playlists.");

        }
    }

    @Override
    public void deleteSong(int songId) throws Exception {

        String sql = "DELETE FROM dbo.Song WHERE Id = ?";

        try (Connection conn = dbConnector.getConnection()) {
            PreparedStatement select = conn.prepareStatement("Select * FROM dbo.song where id = ?");
            select.setInt(1, songId);
            ResultSet rs = select.executeQuery();

            while(rs.next()){
                File song = new File(rs.getString("FilePath"));
                song.delete();
                System.out.println(song.isDirectory());
            }

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, songId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new Exception("Sangen blev ikke fundet i databasen (ID: " + songId + ")");
            }

        } catch (SQLException e) {
            throw new Exception("Fejl under sletning af sang fra databasen", e);
        }
    }
}
