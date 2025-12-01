package dk.easv.mytunes.bll.util;

import com.mpatric.mp3agic.Mp3File;
import dk.easv.mytunes.be.Song;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.List;

public class DurationCalc {

    public static String getDuration(File song) throws Exception {

        if (song == null) {
            throw new NullPointerException("Not able to get song duration due to song == null");
        }

        int dotIndex = song.getName().lastIndexOf(".");

        if (song.getName().substring(dotIndex).equals(".mp3")) {

            Mp3File mp3File = new Mp3File(song.getPath());
            double duration = mp3File.getLengthInSeconds();

            int durationInSeconds = (int) duration;
            int hours = durationInSeconds / 3600;
            int minutes = (durationInSeconds % 3600) / 60;
            int seconds = durationInSeconds % 60;

            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        else if (song.getName().substring(dotIndex).equals(".wav")) {

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(song);
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            double duration = (frames+0.0) / format.getFrameRate();

            int durationInSeconds = (int) duration;
            int hours = durationInSeconds / 3600;
            int minutes = (durationInSeconds % 3600) / 60;
            int seconds = durationInSeconds % 60;

            return String.format("%02d:%02d:%02d", hours, minutes, seconds);

        } else {

            return "00:00:00";

        }
    }

    public static String getTotalDuration(List<Song> songs) throws Exception {

        double duration = 0.0;

        for (Song song: songs) {

            Mp3File mp3File = new Mp3File(song.getFilePath());

            duration += (int) mp3File.getLengthInSeconds();

        }

        int durationInSeconds = (int) duration;
        int hours = durationInSeconds / 3600;
        int minutes = (durationInSeconds % 3600) / 60;
        int seconds = durationInSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);

    }
}
