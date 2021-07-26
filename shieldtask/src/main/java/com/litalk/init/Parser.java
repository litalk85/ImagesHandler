package com.litalk.init;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.litalk.model.ImageInfo;

@Component
public class Parser {
	private ObjectMapper mapper;
	private JsonNode root;

	Parser() {
		this.mapper = new ObjectMapper();
	}

	/**
	 * @param response
	 * @return List<ImageInfo>
	 * Converts the response into ImageInfo list
	 */
	public List<ImageInfo> parseToImageList(ResponseEntity<String> response) {
		List<ImageInfo> imgList = new ArrayList<>();
		try {
			this.root = mapper.readTree(response.getBody());
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return null;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
		//Convert the JsonNodes into ImageInfo instances and add it to the  imgList
		for (JsonNode node : this.root) {
			if (node.get("albumId") != null && node.get("id") != null && node.get("title") != null
					&& node.get("url") != null && node.get("thumbnailUrl") != null) {
				int albumId = node.get("albumId").intValue();
				int id = node.get("id").intValue();
				String title = node.get("title").textValue();
				String url = node.get("url").textValue();
				String thumbnailUrl = node.get("thumbnailUrl").textValue();
				ImageInfo img = new ImageInfo(albumId, id, title, url, thumbnailUrl);
				imgList.add(img);
			}
		}
		return imgList;
	}
}