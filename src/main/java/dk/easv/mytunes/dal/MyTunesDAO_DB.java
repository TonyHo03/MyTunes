package dk.easv.mytunes.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.PlaylistSong;
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
        String sql = "INSERT INTO dbo.Song (Title, Artist, Category, Duration, FilePath) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); {

            ps.setString(1, song.getTitle());
            ps.setString(2, song.getArtist());
            ps.setString(3, song.getCategory());
            ps.setTime(4, song.getDuration());
            ps.setString(5, song.getFilePath());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                 try {
                     java.lang.reflect.Method setIdMethod = Song.class.getDeclaredMethod("setId", int.class);
                     setIdMethod.setAccessible(true);
                     setIdMethod.invoke(song, generatedId);
                 } catch (Exception e) {
                     throw new Exception("Kunne ikke opdatere Song-ID", e);
                 }
            }
            }
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
    public void editPlaylist(Playlist oldPlaylist, Playlist newplaylist) throws Exception {
        try (Connection conn = dbConnector.getConnection()){
            System.out.println("Editing playlist " + oldPlaylist.getName());
            PreparedStatement stmt = conn.prepareStatement("UPDATE dbo.PLaylist SET Name = ? WHERE Name = ?");
            stmt.setString(1,newplaylist.getName());
            stmt.setString(2,oldPlaylist.getName());

            stmt.executeUpdate();
        }
    }

    @Override
    public void  deletePlaylist(Playlist playlist) throws Exception {
        try (Connection conn = dbConnector.getConnection()){
            System.out.println("Deleting playlist " + playlist.getName());
            int playlistId = 0;

            PreparedStatement stmt1 = conn.prepareStatement("SELECT Id FROM dbo.Playlist WHERE Name = ?");
            stmt1.setString(1,playlist.getName());
            ResultSet rs1 = stmt1.executeQuery();

            if (rs1.next()) {
                playlistId = rs1.getInt("Id");
            }

            PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM dbo.SongPlaylist WHERE PlaylistId = ?");
            stmt2.setInt(1, playlistId);
            stmt2.executeUpdate();

            PreparedStatement stmt3 = conn.prepareStatement("DELETE FROM dbo.PLaylist WHERE Id = ?");
            stmt3.setInt(1, playlistId);
            stmt3.executeUpdate();

        }
    }

    @Override
    public List<Song> getAllSongsFromPlaylist(Playlist playlist) throws Exception {
        List<Song> songList = new ArrayList<>();

        int playlistId = 0;
        List<Integer> songIds = new ArrayList<>();

        if (playlist == null) {return songList;}

        try (Connection conn = dbConnector.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("SELECT Id FROM dbo.Playlist WHERE Name = ?");
            ps.setString(1, playlist.getName());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                playlistId = rs.getInt("Id");
                System.out.println("playlistId: " + playlistId);
            }

            PreparedStatement ps2 = conn.prepareStatement("SELECT SongId FROM dbo.SongPlaylist WHERE PlaylistId = ?");
            ps2.setInt(1, playlistId);
            ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) {

                int SongId  = rs2.getInt("SongId");
                System.out.println("SongId: " + SongId);

                songIds.add(SongId);

            }

            PreparedStatement ps3 = conn.prepareStatement("SELECT * FROM dbo.Song");
            ResultSet rs3 = ps3.executeQuery();

            while (rs3.next()) {

                String Title = rs3.getString("Title");
                String Artist = rs3.getString("Artist");
                String Category = rs3.getString("Category");
                Time Duration = rs3.getTime("Duration");
                String FilePath = rs3.getString("FilePath");


                for (int songId : songIds) {

                    if(songId == rs3.getInt("Id")) {
                        System.out.println("Song added");
                        songList.add(new Song(songId, Title, Artist, Category, Duration, FilePath));

                    }

                }
            }

            return songList;

        } catch (SQLException e) {

            e.printStackTrace();
            throw new Exception("Could not fetch all songs.");

        }
    }

    @Override
    public List<PlaylistSong> loadPlaylistSongs() throws Exception {

        File songFolder = new File(PATH_STRING);

        File[] songs = songFolder.listFiles();



        // Hvis der er ingen sange i mappen, så skal funktionen ikke køre.
        if (songs == null) {
            return null;
        }

        try (Connection conn = dbConnector.getConnection()) {

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM dbo.SongPlaylist",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = stmt.executeQuery();

            // Tjekker om der er overhovedet er rækker i resultset. Hvis ikke, så er der ingen sange i databasen.
            if (!rs.next()) {
                System.out.println("Der er INGEN sange i databasen. Indsætter alle sange.");

                for (File song: songs) {

                    String tempTitle = song.getName().split("\\.")[0];

                    //createSong(new Song(0, tempTitle, "", "", time, song.getPath()));
                }

                return null;

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
        return null;
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

            PreparedStatement delete = conn.prepareStatement("DELETE FROM dbo.SongPlaylist WHERE SongId = ?");
            delete.setInt(1, songId);
            int rowsAffected2 = delete.executeUpdate();

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


    @Override
    public void editSong(Song selectedSong, Song newSong) throws Exception{
        String sql = "UPDATE dbo.Song SET Title = ?, Artist = ?, Category = ?, Duration = ?, FilePath = ? WHERE Id = ?";
        try (Connection conn = dbConnector.getConnection()){
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, newSong.getTitle());
            pstmt.setString(2, newSong.getArtist());
            pstmt.setString(3, newSong.getCategory());
            pstmt.setTime(4, newSong.getDuration());
            pstmt.setString(5, newSong.getFilePath());
            pstmt.setInt(6, selectedSong.getId());

            pstmt.execute();


        } catch (SQLServerException e) {
            throw new Exception(e);
        }

    }

    @Override
    public void addSongToPlaylist(Song song, Playlist playlist) throws Exception {

        try (Connection conn = dbConnector.getConnection()) {

            int songId = 0;
            int playlistId = 0;
            int rowCount = 0;

            PreparedStatement select1 = conn.prepareStatement("SELECT Id FROM dbo.Song WHERE Title = ?");
            select1.setString(1, song.getTitle());

            PreparedStatement select2 = conn.prepareStatement("SELECT Id FROM dbo.Playlist WHERE Name = ?");
            select2.setString(1, playlist.getName());

            ResultSet rs1 = select1.executeQuery();
            ResultSet rs2 = select2.executeQuery();

            if (rs1.next()) {

                songId = rs1.getInt("Id");

            }

            if (rs2.next()) {

                playlistId = rs2.getInt("Id");

            }

            PreparedStatement setCondition = conn.prepareStatement("SELECT * FROM dbo.SongPlaylist WHERE SongId = ? AND PlaylistId = ?");
            setCondition.setInt(1, songId);
            setCondition.setInt(2, playlistId);

            ResultSet condition = setCondition.executeQuery();

            if (condition.next()) {
                System.out.println("Sang findes allerede i playlisten.");
                return;
            }

            PreparedStatement add = conn.prepareStatement("INSERT INTO dbo.SongPlaylist (SongId, PlaylistId) VALUES (?, ?)");
            add.setInt(1, songId);
            add.setInt(2, playlistId);

            add.execute();


            PreparedStatement selectAmount = conn.prepareStatement("SELECT * FROM dbo.SongPlaylist WHERE PlaylistId = ?");
            selectAmount.setInt(1, playlistId);

            ResultSet rs3 = selectAmount.executeQuery();

            while (rs3.next()) {

                rowCount++;

            }

            PreparedStatement updatePlaylist = conn.prepareStatement("UPDATE dbo.Playlist SET Songs = ? WHERE Id = ?");
            updatePlaylist.setInt(1, rowCount);
            updatePlaylist.setInt(2, playlistId);

            int rowsAffected = updatePlaylist.executeUpdate();


        } catch (SQLException e) {

            throw new Exception("Could not create song.", e);

        }

    }
    public void deleteSongFromPlaylist(String song, String playlist) throws Exception {
        String sql = "DELETE FROM dbo.SongPlaylist WHERE SongId = ? and PlaylistId = ?";

        try (Connection conn = dbConnector.getConnection()) {

            int songId = 0;
            int playlistId = 0;
            int rowCount = 0;

            PreparedStatement select1 = conn.prepareStatement("SELECT Id FROM dbo.Song WHERE Title = ?");
            select1.setString(1, song);

            PreparedStatement select2 = conn.prepareStatement("SELECT Id FROM dbo.Playlist WHERE Name = ?");
            select2.setString(1, playlist);

            ResultSet rs1 = select1.executeQuery();
            ResultSet rs2 = select2.executeQuery();

            if (rs1.next()) {

                songId = rs1.getInt("Id");

            }

            if (rs2.next()) {

                playlistId = rs2.getInt("Id");

            }

            PreparedStatement delete = conn.prepareStatement(sql);
            delete.setInt(1, songId);
            delete.setInt(2, playlistId);
            delete.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Fejl under sletning af sang fra databasen", e);
        }
    }
}