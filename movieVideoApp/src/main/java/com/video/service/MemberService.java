package com.video.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.video.domain.Member;
import com.video.domain.ROLE;
import com.video.repository.MemberRepository;
import com.video.util.Constants;
import com.video.util.ExtensionUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
	private final MemberRepository memberRepository;

	@Transactional
	public String registerMember(Map<String, Object> registerInfo) {
		String email = (String) registerInfo.get("email");
		String image = (String) registerInfo.get("image");
		String imagePath = getAvatarImage(image);
		File imageFile = new File(imagePath);
		try (BufferedInputStream fileStream = new BufferedInputStream(new FileInputStream(imageFile))) {
			duplicateInfo(email);
			String password = (String) registerInfo.get("password");
			String name = (String) registerInfo.get("name");
			FileUtils.copyInputStreamToFile(fileStream, new File(Constants.MEMBER_IMAGE_PATH + imagePath));
			Member member = Member.builder().email(email).password(password).name(name).role(ROLE.USER)
					.imagePath(imagePath).build();
			memberRepository.save(member);

		} catch (Exception e) {
			return e.getMessage();
		} finally {
			if (image != null)
				imageFile.delete();
		}
		return "success";
	}

	public String getAvatarImage(String imagePath) {
		if (imagePath == null)
			return Constants.BASE_MEMBER_IMAGE_PATH;
		return Constants.TEMPORIARY_PATH + imagePath;
	}

	public Map<String, String> loginMember(Map<String, Object> loginInfo) {
		Map<String, String> result = new HashMap<String, String>();
		try {
			String email = (String) loginInfo.get("email");
			Member findMember = memberRepository.findByEmail(email);
			if (findMember != null) {
				String passsword = (String) loginInfo.get("password");
				if (findMember.getPassword().equals(passsword)) {
					result.put("email", email);
					result.put("id", String.valueOf(findMember.getId()));
					result.put("loginSuccess", "success");
				} else {
					result.put("loginSuccess", "비밀번호가 맞지 않습니다");
				}
			} else {
				result.put("loginSuccess", "해당 회원이 존재하지 않습니다");
			}
		} catch (Exception e) {
			result.put("loginSuccess", e.getMessage());
		}
		return result;
	}

	public Map<String, Object> uploadImage(MultipartFile image) {
		Map<String, Object> result = new HashMap<String, Object>();
		String randomUid = UUID.randomUUID().toString();
		String uid = ExtensionUtil.getUidName(randomUid, image.getOriginalFilename());
		File targetFile = new File(Constants.TEMPORIARY_PATH + uid);
		try (BufferedInputStream fileStream = new BufferedInputStream(image.getInputStream())) {
			FileUtils.copyInputStreamToFile(fileStream, targetFile);
			try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(targetFile))) {
				String bytes = Base64.encodeBase64String(IOUtils.toByteArray(in));
				System.out.println("bytes: " + bytes);
				result.put("bytes", bytes);
			}
			result.put("result", "success");
			result.put("uid", uid);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void duplicateInfo(String email) throws Exception {
		Member findMember = memberRepository.findByEmail(email);
		if (findMember != null)
			throw new Exception("중복 회원이 있습니다");
	}
}
