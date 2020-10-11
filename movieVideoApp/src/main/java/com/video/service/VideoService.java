package com.video.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.video.domain.Member;
import com.video.domain.VIDEOAUTHORITY;
import com.video.domain.VIDEOCATEGORY;
import com.video.domain.Video;
import com.video.repository.MemberRepository;
import com.video.repository.VideoRepository;
import com.video.util.ExtensionUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class VideoService {
	private final VideoRepository videoRepository;
	private final MemberRepository memberRepository;
	
	private final String FILE_PATH = "src/main/resources/static/videos/";
	private final String THUMBNAIL_PATH = "src/main/resources/static/thumbnail/";
	private final String JPGE = "jpg";

	public void submitVideo(Map<String, String> submitInfo) throws Exception {
		try {
			// 1. 동일 이름인지 확인
			String title = submitInfo.get("title");
			if (videoRepository.findByTitle(title) != null)
				throw new Exception("title 이름이 중복됩니다");
			
			Member member = memberRepository.findByEmail(submitInfo.get("user"));

			// 2. DB에 저장
			VIDEOAUTHORITY auth = (submitInfo.get("auth").equals("0")) ? VIDEOAUTHORITY.PRIVATE : VIDEOAUTHORITY.PUBLIC;
			VIDEOCATEGORY category = getCategory(submitInfo.get("category"));
			Video video = Video.builder().path(submitInfo.get("path")).title(title).desc(submitInfo.get("desc"))
					.authority(auth).category(category).member(member).build();
			videoRepository.save(video);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public VIDEOCATEGORY getCategory(String category) {
		switch (category) {
		case "0":
			return VIDEOCATEGORY.FILM_ANIMATION;
		case "1":
			return VIDEOCATEGORY.AUTOS_VEHICLES;
		case "2":
			return VIDEOCATEGORY.MUSIC;
		case "3":
			return VIDEOCATEGORY.PETS_ANIMALS;
		default:
			break;
		}
		return null;
	}

	public Map<String, Object> uploadVideo(MultipartFile file) throws IOException, Exception {
		// 1. video 서버에 저장
		Map<String, Object> result = new HashMap<String, Object>();
		String randomUid = UUID.randomUUID().toString();
		String uid = getUidName(randomUid, file.getOriginalFilename());

		File targetFile = new File(FILE_PATH + uid);
		BufferedInputStream fileStream = new BufferedInputStream(file.getInputStream());
		FileUtils.copyInputStreamToFile(fileStream, targetFile);

		// 2. thumnail 이미지 추출
		String bytes = getThumbnail(targetFile, randomUid);

		result.put("result", "success");
		result.put("uid", uid);
		result.put("bytes", bytes);
		return result;
	}

	public String getThumbnail(File targetFile, String uid) throws Exception, IOException {
		long frameTime = 5000;
		FFmpegFrameGrabber video = new FFmpegFrameGrabber(targetFile);
		video.start();
		video.setTimestamp(frameTime * 1000l);
		
		// 2분 16초
		File thumnailFile = new File(THUMBNAIL_PATH + uid + "." + JPGE);
		ImageIO.write(video.grab().getBufferedImage(), JPGE, thumnailFile);
		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(thumnailFile))) {
			return Base64.encodeBase64String(IOUtils.toByteArray(in));
		}
	}

	public String getUidName(String randomUid, String fileName) {
		String ext = ExtensionUtil.getExtension(fileName);
		return randomUid + "." + ext;
	}
}
