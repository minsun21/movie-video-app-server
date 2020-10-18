package com.video.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.video.domain.Comment;
import com.video.domain.Member;
import com.video.domain.Video;
import com.video.repository.CommentRepository;
import com.video.repository.MemberRepository;
import com.video.repository.VideoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
	private final CommentRepository commentRepository;
	private final VideoRepository videoRepository;
	private final MemberRepository memberRepository;
	
	public List<Comment> allComment(Long videoId){
		Video video = videoRepository.findById(videoId).get();
		List<Comment> commentList = commentRepository.findByVideo(video);
		return commentList;
	}

	public void writeComment(Map<String, String> commentInfo) {
		// 답글은 아직..
		Member writer = memberRepository.findById(Long.valueOf(commentInfo.get("writer"))).get();
		Video video = videoRepository.findById(Long.valueOf(commentInfo.get("videoId"))).get();
		Comment comment = Comment.builder().content(commentInfo.get("content")).writer(writer).video(video).build();
		commentRepository.save(comment);
	}
}
