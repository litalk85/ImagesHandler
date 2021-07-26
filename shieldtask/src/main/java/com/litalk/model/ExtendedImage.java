package com.litalk.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "image_tbl")
public class ExtendedImage extends ImageInfo{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long KeyId;
	
	@NotNull
	private Date downloadTime;
	
	@NotNull
	private String localImgPath;
	
	@NotNull
	private Double fileSize;

	public ExtendedImage(int _albumId, int _id, String _title, String _url, String _thumbnailUrl,
			String _localImgPath, Date _downloadTime, Double _fileSize) {
		super(_albumId, _id, _title, _url, _thumbnailUrl);
		this.downloadTime = _downloadTime;
		this.localImgPath = _localImgPath;
		this.fileSize = _fileSize;
		this.KeyId = (long) -1;
	}
	public ExtendedImage() 
	{
		super();
		
	}
	public ExtendedImage(ImageInfo imgInfo, String _localImgPath, Date _downloadTime, Double _fileSize) {
		super(imgInfo.getAlbumId(), imgInfo.getId(), imgInfo.getTitle(), imgInfo.getUrl(), imgInfo.getThumbnailUrl());
		this.downloadTime = _downloadTime;
		this.localImgPath = _localImgPath;
		this.fileSize = _fileSize;
		this.KeyId = (long) -1;
	}
	public Date getDownloadTime() {
		return downloadTime;
	}
	public void setDownloadTime(Date downloadTime) {
		this.downloadTime = downloadTime;
	}
	public String getLocalImgPath() {
		return localImgPath;
	}
	public void setLocalImgPath(String localImgPath) {
		this.localImgPath = localImgPath;
	}
	public Double getFileSize() {
		return fileSize;
	}
	public void setFileSize(Double fileSize) {
		this.fileSize = fileSize;
	}
}
