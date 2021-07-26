package com.litalk.init;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.litalk.model.ExtendedImage;
import com.litalk.model.ImageInfo;

@Component
public class ImagePoller {

	/**
	 * @param url
	 * @return ResponseEntity<String>
	 * Returns Json response from the given URL 
	 */
	public ResponseEntity<String> getResponseEntity(String url) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity;
		try {
			responseEntity = restTemplate.getForEntity(url, String.class);
		} catch (RestClientException e) {
			return null;
		}
		return responseEntity;
	}

	/**
	 * @param imgInfo
	 * @param filePath
	 * @return ExtendedImage
	 * @throws Exception
	 * Downloads the given image to the given path, append: download time, path and file size and 
	 * returns the ExtendedImage instance 
	 */
	public ExtendedImage getExtendedImageAndDownloadFile(ImageInfo imgInfo, String filePath) throws Exception {
		downloadImageToLocal(imgInfo.getUrl(), filePath);
		BasicFileAttributes fileAttr = getFileAttributes(filePath);
		return new ExtendedImage(imgInfo, filePath, getFileLastModifiedDate(fileAttr), getFileSize(fileAttr));
	}

	/**
	 * @param inputSteram
	 * @return byte[]
	 * @throws IOException
	 * Convert the input stream to byte[]
	 */
	private byte[] covertInputStreamToByteArray(InputStream inputSteram) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int n = 0;
		while (-1 != (n = inputSteram.read(buf))) {
			out.write(buf, 0, n);
		}
		out.close();
		return out.toByteArray();
	}

	/**
	 * @param url
	 * @param localFullPath
	 * @throws Exception
	 * Downloads given the url image to the given local path 
	 */
	private void downloadImageToLocal(String url, String localFullPath) throws Exception {
		URL imageUrl = new URL(url);
		InputStream in = new BufferedInputStream(imageUrl.openStream());
		FileOutputStream fos = new FileOutputStream(localFullPath);
		fos.write(covertInputStreamToByteArray(in));
		in.close();
		fos.close();
	}

	/**
	 * @param filePath
	 * @return BasicFileAttributes
	 * Return the given file path attributes
	 */
	private BasicFileAttributes getFileAttributes(String filePath) {
		Path file = Paths.get(filePath);
		try {
			return Files.readAttributes(file, BasicFileAttributes.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param attr
	 * @return Date
	 * Returns the file last modified date 
	 */
	private Date getFileLastModifiedDate(BasicFileAttributes attr) {
		Date date = new Date();
		date.setTime(attr.lastModifiedTime().toMillis());
		return date;
	}

	/**
	 * @param attr
	 * @return Double
	 * Returns the file size
	 */
	private Double getFileSize(BasicFileAttributes attr) {
		return attr.size() / 1024.0;
	}

}
