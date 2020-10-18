package com.video.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.video.domain.Comment;
import com.video.domain.Video;

public interface CommentRepository extends JpaRepository<Comment,Long>{

	List<Comment> findByVideo(Video video);

}
