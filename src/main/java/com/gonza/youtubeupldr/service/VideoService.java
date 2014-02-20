package com.gonza.youtubeupldr.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VideoService {

	private static final Logger logger = LoggerFactory.getLogger(VideoService.class);
	
    @Autowired
    private ServletContext context;

    public void setContext(ServletContext context) {
        this.context = context;
    }

    @Autowired
    private UploadVideo uploadVideo;

    public void setUploadVideo(UploadVideo uploadVideo) {
        this.uploadVideo = uploadVideo;
    }
    
    public void uploadStaticVideo(String accessToken, String refreshToken){
    	logger.info("uploadVideo service is :" + (uploadVideo == null ? " null!!" : " not null"));
    	uploadVideo.init(null, accessToken, refreshToken);
    }
    
	public String saveMultipartToDisk(String uploadId, MultipartFile file, String accessToken, String refreshToken) throws Exception {
		Date time = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:S");
        logger.info("Saving file to own server starts at: " + sdf.format(time));
		String filePath = this.context.getRealPath("assets/videos")+"/"+uploadId;
        logger.info("filePath: " + filePath);
        File dir = new File(filePath);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        
        File multipartFile = new File(filePath+"/"+file.getOriginalFilename());
        file.transferTo(multipartFile);
        time = Calendar.getInstance().getTime();
        logger.info("Saving file to own server finished at: " + sdf.format(time));
        String youtubeUrl = uploadVideo.init(filePath+"/"+file.getOriginalFilename(), accessToken, refreshToken);
        time = Calendar.getInstance().getTime();
        logger.info("Saving file to youtube finished at: " + sdf.format(time));
        return youtubeUrl;
    }
    
    public boolean checkVideoContentType(MultipartFile file) throws Exception{
        if(file.getContentType().equals("video/mp4") || file.getContentType().equals("video/ogg") || file.getContentType().equals("video/webm")){
            return true;
        }
        return false;
    }
    
    public String calculateUploadId(){
    	Date today = Calendar.getInstance().getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hh.mm.ss");
    	logger.info("uploadId: " + sdf.format(today));
    	
    	return sdf.format(today);
    	
    }
    
    
}
