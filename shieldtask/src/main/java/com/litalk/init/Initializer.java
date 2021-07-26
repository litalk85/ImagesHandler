package com.litalk.init;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import com.litalk.controller.ImageController;
import com.litalk.model.ExtendedImage;
import com.litalk.model.ImageInfo;

@RestController
@Component
public class Initializer {
	private static final String IMAGE_NAME = "img";
	private static int imageNumber = 1;
	private static final String URL = "https://shield-j-test.s3.amazonaws.com/photo.txt";
	private static String LOCAL_PATH = null;
	private List<ImageInfo> imgsList;
	private List<ExtendedImage> imgsFullDataList;
	public static final String NO_RESPONSE = "Couldn't get response from \" + URL + \"\\nExiting...";
	public static final String FAILED_TO_PARSE = "Couldn't parse the response from \" + URL + \"\\nExiting...";
	public static final String INIT_ERROR = "Error occured while initializing the application";

	@Autowired
	ImagePoller imgPoller;
	@Autowired
	Parser parser;
	@Autowired
	ImageController imgController;

	/**
	 * @throws Exception
	 * Called on application startup
	 * Initialize: creates the local path (if it doesn't exists)
	 * 			   Gets Json response from the given URL
	 * 			   Parse the response: Converts the response into ImageInfo list 
	 * 			   Download the images to the local path 
	 * 			   Append download date, local path, file size and creates an Extended image list
	 * 			   Saves the Extended image list into the local MySql database
	 */
	@PostConstruct
	public void onCreate() throws Exception {
		// retrieve the text file from the URL
		createLocalPathIfDoesntExist();
		ResponseEntity<String> response = imgPoller.getResponseEntity(URL);
		if (response == null) {
			ExitWithMsg(NO_RESPONSE);
		}
		// parse the response to imageInfo list
		this.imgsList = parser.parseToImageList(response);
		if (this.imgsList == null || this.imgsList.isEmpty()) {
			ExitWithMsg(FAILED_TO_PARSE);
		}
		initImgsFullDataListAndDownloadImgs();
		imgController.saveImgsList(imgsFullDataList);
	}

	/**
	 * Creates the images local path directory if it doesn't exist
	 */
	private void createLocalPathIfDoesntExist() {
		if (LOCAL_PATH == null) {
			LOCAL_PATH = System.getProperty("user.dir") + "\\saved_images";
			if (LOCAL_PATH != null) {
				File pathAsFile = new File(LOCAL_PATH);
				if (!Files.exists(Paths.get(LOCAL_PATH))) {
					pathAsFile.mkdir();
				}
			}
		}
	}

	/**
	 * @throws Exception
	 * Downloads the imgsList images to the local path 
	 * Append download date, local path, file size and insert it to the Extended image list (imgsFullDataList)
	 */
	private void initImgsFullDataListAndDownloadImgs() throws Exception {
		this.imgsFullDataList = new ArrayList<>();
		try {
			for (ImageInfo imgInfo : imgsList) {
				String fileFullPath = LOCAL_PATH + "\\" + this.renderNewFileName() + ".jpg";
				this.imgsFullDataList.add(imgPoller.getExtendedImageAndDownloadFile(imgInfo, fileFullPath));
			}
		} catch (Exception e) {
			e.printStackTrace();
			ExitWithMsg(INIT_ERROR);
		}
	}
	
	/**
	 * @return String
	 * Creates and returns the current image file name
	 */
	private String renderNewFileName() {
		String name = IMAGE_NAME + imageNumber;
		imageNumber++;
		return name;
	}
	
	/**
	 * @param msg
	 * Inner prints a given Error message
	 * And Exits the application
	 */
	private void ExitWithMsg(String msg) {
		System.err.println(msg);
		System.exit(1);
	}
}