package com.tunehub.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tunehub.entities.Song;
import com.tunehub.repositories.SongRepository;

@Service
public class SongServiceImplementation implements SongService {
    @Autowired
    SongRepository repo;
    
    @Override
    public void addSong(Song song) {
        repo.save(song);
    }

    @Override
    public List<Song> fetchAllSongs() {
        return repo.findAll();
    }

    @Override
    public boolean songExists(String name) {
        Song song = repo.findByName(name);
        return song != null;
    }
    
    @Override
    public void updateSong(Song song) {
        repo.save(song);
    }

    @Override
    public List<Song> fetchLimitedSongs(int limit) {
        return repo.findTopN(limit); // Custom method in repository
    }
}