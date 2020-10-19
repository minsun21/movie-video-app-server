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
import com.video.util.Constants;
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<VideoDTO> getAllVideo() {
		List<VideoDTO> list = videoRepository.findAll().stream().map(VideoDTO::new).collect(Collectors.toList());
		return list;
	}

	public void submitVideo(Map<String, String> submitInfo) throws Exception, IOException {
		String videoPath = submitInfo.get("path");

		File tempVideo = new File(Constants.TEMPORIARY_PATH + videoPath);
		
		try (BufferedInputStream fileStream = new BufferedInputStream(new FileInputStream(tempVideo))) {
			// 1. 동일 이름인지 확인
			String title = submitInfo.get("title");
			if (videoRepository.findByTitle(title) != null)
				throw new Exception("title 이름이 중복됩니다");

			// 2. login info
			Member member = memberRepository.findByEmail(submitInfo.get("user"));

			// 3. video path 이동
			File videoFilePath = new File(Constants.VIDEO_FILE_PATH + videoPath);
			FileUtils.copyInputStreamToFile(fileStream, videoFilePath);

			// 4. thumbnail 이동
			String thumbnail = ExtensionUtil.getRemoveExtension(videoPath) + "." + Constants.JPGE;
			File tempThumbnail = new File(Constants.TEMPORIARY_PATH+thumbnail);
			File videoThumbnail = new File(Constants.THUMBNAIL_PATH + thumbnail);
			try (BufferedInputStream memberFileStream = new BufferedInputStream(new FileInputStream(tempThumbnail))) {
				FileUtils.copyInputStreamToFile(memberFileStream, videoThumbnail);
			}
			
			// 5. DB에 저장
			VIDEOAUTHORITY auth = (submitInfo.get("auth").equals("0")) ? VIDEOAUTHORITY.PRIVATE : VIDEOAUTHORITY.PUBLIC;
			VIDEOCATEGORY category = getCategory(submitInfo.get("category"));
			Video video = Video.builder().videoPath(videoPath).title(title).desc(submitInfo.get("desc")).authority(auth)
					.category(category).member(member).thumbnailPath(thumbnail).uploadDate(LocalDate.now())
					.viewCount("0").build();
			videoRepository.save(video);

			// temp 파일들 삭제
			tempThumbnail.delete();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally {
			tempVideo.delete();
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
		String uid = ExtensionUtil.getUidName(randomUid, file.getOriginalFilename());

		File targetFile = new File(Constants.TEMPORIARY_PATH + uid);
		try (BufferedInputStream fileStream = new BufferedInputStream(file.getInputStream())) {
			FileUtils.copyInputStreamToFile(fileStream, targetFile);

			// 2. thumnail 이미지 추출
			String bytes = getThumbnail(targetFile, randomUid);

			result.put("result", "success");
			result.put("uid", uid);
			result.put("bytes", bytes);
		}
		return result;
	}

	public String getThumbnail(File targetFile, String uid) throws Exception, IOException {
		long frameTime = 5000;
		FFmpegFrameGrabber video = new FFmpegFrameGrabber(targetFile);
		video.start();
		video.setTimestamp(frameTime * 1000l);

		// 2분 16초
		File thumnailFile = new File(Constants.TEMPORIARY_PATH + uid + "." + Constants.JPGE);
		ImageIO.write(video.grab().getBufferedImage(), Constants.JPGE, thumnailFile);
		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(thumnailFile))) {
			return Base64.encodeBase64String(IOUtils.toByteArray(in));
		}
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

		private String videoFilePath;

		private VIDEOAUTHORITY authority;

		private VIDEOCATEGORY category;

		private String memberName;

		private String memberImage;

		private String viewCount;

		private LocalDate uploadDate;

		private String thumbnail;
		
		private Long memberId;

		public VideoDTO(Video video) {
			this.id = video.getId();
			this.title = video.getTitle();
			this.desc = video.getDesc();
			this.videoFilePath = video.getVideoPath();
			this.authority = video.getAuthority();
			this.category = video.getCategory();
			this.memberName = video.getMember().getName();
			this.memberId = video.getMember().getId();
			this.viewCount = video.getViewCount();
			this.uploadDate = video.getUploadDate();
			try {
				this.memberImage = getImage(video.getMember().getImagePath(), false);
				this.thumbnail = getImage(video.getThumbnailPath(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public VideoDTO(Video video, boolean detail) {
			this.id = video.getId();
			this.title = video.getTitle();
			this.desc = video.getDesc();
			this.authority = video.getAuthority();
			this.category = video.getCategory();
			this.memberName = video.getMember().getName();
			this.memberId = video.getMember().getId();
			this.viewCount = video.getViewCount();
			this.uploadDate = video.getUploadDate();
			try {
				this.memberImage = getImage(video.getMember().getImagePath(), false);
				this.thumbnail = getImage(video.getThumbnailPath(), true);
				// video 객체도 전달 해야 함

				this.videoFilePath = getVideoFile(video.getVideoPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getVideoFile(String videoPath) throws IOException {
		// 1) 파일을 가져와서
		// 2) 바이트화 시켜서 전달
		File videoFile = new File(Constants.VIDEO_FILE_PATH + videoPath);
		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(videoFile))) {
			return Base64.encodeBase64String(IOUtils.toByteArray(in));
		}
	}

	private String getImage(String thumbnailPath, boolean isThumbnail) throws IOException {
		String path = "";
		if (isThumbnail)
			path = Constants.THUMBNAIL_PATH;
		else
			path = Constants.MEMBER_IMAGE_PATH;
		File thumnailFile = new File(path + thumbnailPath);
		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(thumnailFile))) {
			return Base64.encodeBase64String(IOUtils.toByteArray(in));
		}
	}

	public VideoDTO getDetailVideo(Long id) {
		Video video = videoRepository.findById(id).get();
		addViewCount(video);
		VideoDTO videoDto = new VideoDTO(video, true);
		return videoDto;
	}

	public Video addViewCount(Video video) {
		video.addViewCount();
		videoRepository.save(video);
		return video;
	}
}
