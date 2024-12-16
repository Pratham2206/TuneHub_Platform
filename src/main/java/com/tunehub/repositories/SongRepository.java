package com.tunehub.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tunehub.entities.Song;

public interface SongRepository extends JpaRepository<Song, Integer> {
    Song findByName(String name);

    @Query(value = "SELECT * FROM song LIMIT ?1", nativeQuery = true) // Adjust table name if necessary
    List<Song> findTopN(int limit);
}