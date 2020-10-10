package com.video.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.video.domain.Video;

public interface VideoRepository extends JpaRepository<Video, Long>{

	Video findByTitle(String title);

	Video findByPath(String path);

}
