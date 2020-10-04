package com.video.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.video.domain.Video;
import com.video.repository.MemberRepository;
import com.video.repository.VideoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class VideoService {
	private final VideoRepository videoRepository;
	
	public void uploadVideo() {
	}

}
