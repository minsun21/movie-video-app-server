package com.video.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
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

//	public getThumbnail() {
//		int frameNumber = 0;
//	    
//		  Picture picture = FrameGrab.getFrameFromFile(source, frameNumber);
//		 
//		  BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
//		  ImageIO.write(bufferedImage, IMAGE_PNG_FORMAT, thumbnail);
//	}
}
