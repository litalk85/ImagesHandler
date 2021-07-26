package com.litalk.init;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.litalk.model.ImageInfo;


public class ParserTest {
	
	private Parser p = new Parser();
	
	@Test
	public void parseToImageListTest() throws JsonMappingException, JsonProcessingException {
		ResponseEntity<String> response = new ResponseEntity<>(
				"[\r\n" + "  {\r\n" + "    \"albumId\": 1,\r\n" + "    \"id\": 1,\r\n"
						+ "    \"title\": \"amazing island view\",\r\n"
						+ "    \"url\": \"https://shield-j-test.s3.amazonaws.com/photo1.jfif\",\r\n"
						+ "    \"thumbnailUrl\": \"https://shield-j-test.s3.amazonaws.com/photo1.jfif\"\r\n" + "  }]",
				HttpStatus.OK);
		ImageInfo expectedImage = new ImageInfo(1, 1, "amazing island view",
				"https://shield-j-test.s3.amazonaws.com/photo1.jfif",
				"https://shield-j-test.s3.amazonaws.com/photo1.jfif");

		List<ImageInfo> imgList = p.parseToImageList(response);
		assertNotNull(imgList);
		assertEquals(1, imgList.size());
		validateImageInfo(imgList.get(0), expectedImage);

	}

	@Test
	public void parseToImageListInvalidJsonAttributeTest() throws JsonMappingException, JsonProcessingException {
		ResponseEntity<String> response = new ResponseEntity<>(
				"[\r\n" + "  {\r\n" + "    \"alblumId\": 1,\r\n" + "    \"id\": 1,\r\n"
						+ "    \"title\": \"amazing island view\",\r\n"
						+ "    \"url\": \"https://shield-j-test.s3.amazonaws.com/photo1.jfif\",\r\n"
						+ "    \"thumbnailUrl\": \"https://shield-j-test.s3.amazonaws.com/photo1.jfif\"\r\n" + "  }]",
				HttpStatus.OK);

		List<ImageInfo> imgList = p.parseToImageList(response);
		assertEquals(0, imgList.size());
	}

	private void validateImageInfo(ImageInfo actualImg, ImageInfo expectedImg) {
		assertEquals(actualImg.getAlbumId(), expectedImg.getAlbumId());
		assertEquals(actualImg.getId(), expectedImg.getId());
		assertEquals(actualImg.getThumbnailUrl(), expectedImg.getThumbnailUrl());
		assertEquals(actualImg.getUrl(), expectedImg.getUrl());
		assertEquals(actualImg.getTitle(), expectedImg.getTitle());
	}

}
