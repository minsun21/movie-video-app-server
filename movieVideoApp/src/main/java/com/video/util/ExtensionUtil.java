package com.video.util;

public class ExtensionUtil {
	// 확장자 제거
	public static String getRemoveExtension(String fileName) {
		int idx = fileName.lastIndexOf(".");
		String removeExtensionName = fileName.substring(0, idx);
		return removeExtensionName;
	}
	
	// 확장자 추출
	public static String getExtension(String fileName) {
		int index = fileName.lastIndexOf(".");
		String extension = fileName.substring(index + 1);
		return extension;
	}
}
