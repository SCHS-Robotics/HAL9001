/*
 * Filename: BeatBox.java
 * Author: Dylan Zueck and Cole Savage
 * Team Name: Crow Force and Level Up
 * Date: 8/10/19
 */

package com.SCHSRobotics.HAL9001.util.misc;

import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;

import java.util.Collection;
import java.util.Map;
import java.util.Random;

//TODO add more features and test class.

/**
 * A class for managing, playing, and editing songs.
 */
public class BeatBox {

    //A hashmap mapping song names to the actual song classes.
    private Map<String, MediaPlayer> songs;

    /**
     * Constructor for BeatBox.
     */
    public BeatBox() {}

    /**
     * Adds a song to the list of songs.
     *
     * @param songName The name of the song.
     * @param song The song class.
     */
    public void addSong(String songName, MediaPlayer song){
        songs.put(songName, song);
    }

    /**
     * Removes a song from the list of songs.
     *
     * @param songName The name of the song to remove.
     */
    public void removeSong(String songName){
        songs.remove(songName);
    }

    /**
     * Play a song.
     *
     * @param songName The name of the song to play.
     */
    public void playSong(String songName){
        songs.get(songName).start();
    }

    /**
     * Stop playing a song.
     *
     * @param songName The name of the song to stop playing.
     */
    public void stopSong(String songName){
        songs.get(songName).stop();
    }

    /**
     * Sets if a song should infinitely loop.
     *
     * @param songName The name of the song to loop.
     * @param loop Whether or not the song should infinitely loop.
     */
    public void setSongLoop(String songName, boolean loop){
        songs.get(songName).setLooping(loop);
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
     * @param name The name of the song to base boost.
     * @param level The level of base boost, between 0 and 1.
     */
    public void baseBoost(String name, int level) {

        level = level*1000;

        BassBoost bassBoost = new BassBoost(0,songs.get(name).getAudioSessionId());
        songs.get(name).attachAuxEffect(bassBoost.getId());
        songs.get(name).setAuxEffectSendLevel(level);
    }
}
