package com.SCHSRobotics.HAL9001.util.misc;

import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;

import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A class for managing, playing, and editing songs.
 *
 * @author Dylan Zueck, Crow Force
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 8/10/19
 */
@SuppressWarnings("unused")
public class BeatBox {

    //A hashmap mapping song names to the actual song classes.
    private Map<String, MediaPlayer> songs;

    /**
     * Constructor for BeatBox.
     */
    public BeatBox() {
        songs = new HashMap<>();
    }

    /**
     * Adds a song to the list of songs.
     *
     * @param songName The name of the song.
     * @param song The song class.
     */
    public void addSong(@NotNull String songName, @NotNull MediaPlayer song){
        songs.put(songName, song);
    }

    /**
     * Removes a song from the list of songs.
     *
     * @param songName The name of the song to remove.
     */
    public void removeSong(@NotNull String songName){
        songs.remove(songName);
    }

    /**
     * Play a song.
     *
     * @param songName The name of the song to play.
     */
    public void playSong(@NotNull String songName){
        MediaPlayer song = songs.get(songName);
        ExceptionChecker.assertNonNull(song,new NullPointerException("No song with name "+songName+" is present in this beatbox."));
        song.start();
    }

    /**
     * Stop playing a song.
     *
     * @param songName The name of the song to stop playing.
     */
    public void stopSong(@NotNull String songName){
        MediaPlayer song = songs.get(songName);
        ExceptionChecker.assertNonNull(song,new NullPointerException("No song with name "+songName+" is present in this beatbox."));
        song.stop();
    }

    /**
     * Sets if a song should infinitely loop.
     *
     * @param songName The name of the song to loop.
     * @param loop Whether or not the song should infinitely loop.
     */
    public void setSongLoop(@NotNull String songName, boolean loop){
        MediaPlayer song = songs.get(songName);
        ExceptionChecker.assertNonNull(song,new NullPointerException("No song with name "+songName+" is present in this beatbox."));
        song.setLooping(loop);
    }

    /**
     * Plays a random song from the list of songs.
     */
    public void playRandomSong(){
        Collection songKeys = songs.values();
        MediaPlayer[] songs = (MediaPlayer[]) songKeys.toArray();
        Random random = new Random();
        songs[random.nextInt(songs.length)].start();
    }

    /**
     * Plays every song at once. :)
     */
    public void ultimateBeats(){
        for(MediaPlayer song : songs.values()) {
            song.setLooping(true);
            song.start();
        }
    }

    /**
     * Base boost a song.
     *
     * @param songName The name of the song to base boost.
     * @param level The level of base boost, between 0 and 1.
     */
    public void baseBoost(@NotNull String songName, int level) {

        level = level*1000;

        MediaPlayer song = songs.get(songName);
        ExceptionChecker.assertNonNull(song,new NullPointerException("No song with name "+songName+" is present in this beatbox."));

        BassBoost bassBoost = new BassBoost(0,song.getAudioSessionId());
        song.attachAuxEffect(bassBoost.getId());
        song.setAuxEffectSendLevel(level);
    }
}
