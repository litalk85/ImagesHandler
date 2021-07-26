package com.litalk.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.litalk.model.ExtendedImage;
import com.litalk.repository.ImageRepository;

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = { "com.litalk" })
@RestController
public class ImageController {
	
	public static final String FAILED_TO_RETRIEVE_FROM_DB = "Failed to retrieve images from database";
	public static final String FOR_ALBUM_ID = " for album id = ";
	public static final String URL_MALFORMED = "Failed to retrieve image due to MalformedURLException";
	@Autowired
	private ImageRepository imgRepository;

	/**
	 * @param extImgList
	 * @return List<ExtendedImage>
	 * Called from initializer.
	 * Saves all the images info into the local MySql database
	 */
	public List<ExtendedImage> saveImgsList(List<ExtendedImage> extImgList) {
		return imgRepository.saveAll(extImgList);
	}

	/**
	 * @return List<ResponseEntity<byte[]>>
	 * Returns a list of byte[] representing the images
	 */
	@GetMapping("/get-all-images")
	public List<ResponseEntity<byte[]>> getAllImgs() {
		return getImgsFromImgsList(imgRepository.findAll(), FAILED_TO_RETRIEVE_FROM_DB);
	}

	/**
	 * @param albumId
	 * @return List<ResponseEntity<byte[]>>
	 * Returns a list of byte[] representing the images by the given albumId
	 */
	@GetMapping(value = "/get-images-by-albumid")
	public @ResponseBody List<ResponseEntity<byte[]>> getImgsByAlbumId(@RequestParam("albumId") int albumId) {
		return getImgsFromImgsList(imgRepository.findByAlbumId(albumId), FAILED_TO_RETRIEVE_FROM_DB + FOR_ALBUM_ID + albumId);
	}

	/**
	 * @param albumId
	 * @param id
	 * @param request
	 * @return ResponseEntity<Resource> 
	 * @throws Exception and EntityNotFoundException
	 * Download image by albumId and (image) id into the users local downloads folder
	 */
	@GetMapping(value = "/download-image")
	public ResponseEntity<Resource> downloadFile(@RequestParam("albumId") int albumId, @RequestParam("id") int id,
			HttpServletRequest request) throws Exception {

		ExtendedImage exImg = imgRepository.findImageByAlbumIdAndId(albumId, id);
		if (exImg == null ) {
			throw new EntityNotFoundException(FAILED_TO_RETRIEVE_FROM_DB + FOR_ALBUM_ID + albumId +" and id = "+id);
		}
		String fileName = exImg.getLocalImgPath();
		Path filePath = Paths.get(fileName).toAbsolutePath().normalize();

		// Load file as Resource
		Resource resource;
		try {
			resource = new UrlResource(filePath.toUri());
		} catch (MalformedURLException e) {
			throw new EntityNotFoundException(URL_MALFORMED);
		}

		// Try to determine file's content type
		String contentType = null;
		contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	/**
	 * @param exImglist
	 * @param exceptionMsg
	 * @return List<ResponseEntity<byte[]>>
	 * @throws EntityNotFoundException
	 * Extracts the images from the ExtendedImage list and returns a list of byte[] representing the images 
	 */
	private List<ResponseEntity<byte[]>> getImgsFromImgsList(List<ExtendedImage> exImglist, String exceptionMsg) {
		
		if (exImglist == null || exImglist.isEmpty()) {
			throw new EntityNotFoundException(
					String.format(exceptionMsg));
		}
		List<ResponseEntity<byte[]>> responseList = new ArrayList<>();
		
		for (ExtendedImage exImg : exImglist) {
			byte[] image = new byte[0];
			try {
				image = FileUtils.readFileToByteArray(new File(exImg.getLocalImgPath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			responseList.add(ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image));
		}
		return responseList;
	}

	/************************************************************************************************************/
	// In addition to the requirements
	/************************************************************************************************************/
	/**
	 * @return List<ExtendedImage>
	 * Retrieves images information list from the database
	 * Returns an empty list if there is no data
	 */
	@GetMapping("/get-all-images-info")
	public List<ExtendedImage> getAllImgsInfo() {
		return imgRepository.findAll();
	}

	/**
	 * @return List<ExtendedImage>
	 * Retrieves images information list from the database by the albumId
	 * Returns an empty list if there is no data
	 */
	@GetMapping(value = "/get-images-info-by-albumid")
	public @ResponseBody List<ExtendedImage> getImgsInfoByAlbumId(@RequestParam("albumId") int album_id) {
		return imgRepository.findByAlbumId(album_id);

	}

	/**
	 * @param albumId
	 * @param id
	 * @return
	 * @throws IOException
	 * Retrieves the image path by album id and (image) id from the database and returns it from the local folder
	 */
	@GetMapping(value = "/get-image")
	public ResponseEntity<byte[]> getImageByAlbumIdAndImageId(@RequestParam("albumId") int albumId,
			@RequestParam("id") int id) throws IOException {
		ExtendedImage exImg = imgRepository.findImageByAlbumIdAndId(albumId, id);

		byte[] image = new byte[0];
		if (exImg == null) {
			throw new EntityNotFoundException(FAILED_TO_RETRIEVE_FROM_DB + FOR_ALBUM_ID + albumId +" and id = "+id);
		}
		try {
			image = FileUtils.readFileToByteArray(new File(exImg.getLocalImgPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
	}
}