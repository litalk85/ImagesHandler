package com.litalk.repository;

import com.litalk.model.ExtendedImage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ExtendedImage, Integer>{

	/**
	 * @param albumId
	 * @return List<ExtendedImage>
	 * Retrieves a list of extended images from the local MySql database by the albumId
	 */
	List<ExtendedImage> findByAlbumId(int albumId);

	/**
	 * @param albumId
	 * @param id
	 * @return ExtendedImage
	 * Retrieves an extended image from the local MySql database by the albumId
	 */
	ExtendedImage findImageByAlbumIdAndId(int albumId, int id);
	
}
