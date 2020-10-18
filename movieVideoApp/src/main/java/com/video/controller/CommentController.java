package com.video.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.video.domain.Comment;
import com.video.service.CommentService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {
	private final CommentService commentService;
	
	@PostMapping("/get")
	public List<Comment> allComment(Map<String, String> commentInfo){
		// 해당 비디오의 코멘트들 전부 가져옴
		Long videoId = Long.valueOf(commentInfo.get("videoId"));
		List<Comment> commentList = commentService.allComment(videoId);
		return commentList;
	}
	
	@PostMapping("/write")
	public List<Comment> writeComment(Map<String, String> commentInfo){
		// 코멘트 작성하기
		commentService.writeComment(commentInfo);
		return null;
	}
}
