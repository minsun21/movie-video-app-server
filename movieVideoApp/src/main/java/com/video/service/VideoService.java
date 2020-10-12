package com.video.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

import lombok.AllArgsConstructor;
import lombok.Data;
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<VideoDTO> getAllVideo() {
		List<VideoDTO> list = videoRepository.findAll().stream().map(VideoDTO::new).collect(Collectors.toList());
		return list;
	}

	public void submitVideo(Map<String, String> submitInfo) throws Exception {
		try {
			// 1. 동일 이름인지 확인
			String title = submitInfo.get("title");
			if (videoRepository.findByTitle(title) != null)
				throw new Exception("title 이름이 중복됩니다");

			// 2. login info
			Member member = memberRepository.findByEmail(submitInfo.get("user"));

			String videoPath = submitInfo.get("path");
			// 3. thumbnail
			String thumbnail = ExtensionUtil.getRemoveExtension(videoPath) + "." + JPGE;

			// 2. DB에 저장
			VIDEOAUTHORITY auth = (submitInfo.get("auth").equals("0")) ? VIDEOAUTHORITY.PRIVATE : VIDEOAUTHORITY.PUBLIC;
			VIDEOCATEGORY category = getCategory(submitInfo.get("category"));
			Video video = Video.builder().videoPath(videoPath).title(title).desc(submitInfo.get("desc")).authority(auth)
					.category(category).member(member).thumbnailPath(thumbnail).uploadDate(LocalDate.now())
					.viewCount("0").build();
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

	@SuppressWarnings("unused")
	@AllArgsConstructor
	public class Result<T> {
		private T data;
		private String result;

	}

	@Data
	public class VideoDTO {
		private Long id;
		
		private String title;

		private String desc;

		private String path;

		private VIDEOAUTHORITY authority;

		private VIDEOCATEGORY category;

		private String member;

		private String viewCount;

		private LocalDate uploadDate;

		private String thumbnail;

		public VideoDTO(Video video) {
			this.id = video.getId();
			this.title = video.getTitle();
			this.desc = video.getDesc();
			this.path = video.getVideoPath();
			this.authority = video.getAuthority();
			this.category = video.getCategory();
			if(video.getMember() != null)
				this.member = video.getMember().getName();
			else
				this.member="test@test.com";
			this.viewCount = video.getViewCount();
			this.uploadDate = video.getUploadDate();
			this.thumbnail = getImage(video.getThumbnailPath());
		}

		private String getImage(String thumbnailPath) {
			File thumnailFile = new File(THUMBNAIL_PATH + thumbnailPath);
			try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(thumnailFile))) {
				try {
					return Base64.encodeBase64String(IOUtils.toByteArray(in));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return thumbnailPath;
		}
	}

}
