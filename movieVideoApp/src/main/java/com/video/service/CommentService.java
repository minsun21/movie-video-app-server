package com.video.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.video.domain.Comment;
import com.video.domain.Member;
import com.video.domain.Video;
import com.video.repository.CommentRepository;
import com.video.repository.MemberRepository;
import com.video.repository.VideoRepository;
import com.video.service.VideoService.VideoDTO;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
	private final CommentRepository commentRepository;
	private final VideoRepository videoRepository;
	private final MemberRepository memberRepository;

	public List<CommentDto> allComment(Long videoId) {
		Video video = videoRepository.findById(videoId).get();
		List<CommentDto> commentList = commentRepository.findByVideo(video).stream().map(CommentDto::new)
				.collect(Collectors.toList());
		return commentList;
	}

	public CommentDto writeComment(Map<String, String> commentInfo) {
		Member writer = memberRepository.findById(Long.valueOf(commentInfo.get("writer"))).get();
		Video video = videoRepository.findById(Long.valueOf(commentInfo.get("videoId"))).get();
		Comment comment = Comment.builder().content(commentInfo.get("content")).writer(writer).video(video).build();
		commentRepository.save(comment);
		CommentDto commentDto = new CommentDto(comment);
		return commentDto;
	}

	@Data
	public class CommentDto {
		private Long id;
		private Member member;
		private String content;
		private Comment reponseTo;
		private List<Comment> childList;

		public CommentDto(Comment comment) {
			this.id = comment.getId();
			this.member = comment.getWriter();
			this.content = comment.getContent();
			this.reponseTo = comment.getResponseTo();
			this.childList = comment.getChildList();
		}
	}
}
