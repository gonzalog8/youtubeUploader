package com.gonza.youtubeupldr.controller;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gonza.youtubeupldr.service.VideoService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
    @Autowired
    private ServletContext context;
    @Autowired
    private VideoService videoService;

    public void setContext(ServletContext context) {
        this.context = context;
    }

	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping(value = "/oauth2callback", method = RequestMethod.GET)
	public String oauthCallback(HttpServletRequest request, ModelMap model) {
		String authToken = (String) request.getParameter("code");
		logger.info("oauth2callback.authToken= " + authToken);
		
		model.addAttribute("authToken", authToken);
		return "upload";
	}
	
	@RequestMapping(value = "/refreshTokenCallback", method = RequestMethod.GET)
	public String tokenCallback(HttpServletRequest request, ModelMap model) {
		String authToken = (String) request.getAttribute("code");
		logger.info("oauth2callback.authToken= " + authToken);
		
		model.addAttribute("authToken", authToken);
		return "upload";
	}
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public String uploadPage(Locale locale, Model model) {
		return "upload";
	}
	
	@RequestMapping(value = "/uploadVideo", method = RequestMethod.POST)
    public @ResponseBody
    String upload(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		String accessToken = request.getParameter("accessToken");
		String refreshToken = request.getParameter("refreshToken");
        List<MultipartFile> mpffiles = request.getFiles("file");
        List<String> files = new ArrayList<String>();
        if(mpffiles!=null && !mpffiles.isEmpty()){
            String uploadId = videoService.calculateUploadId();
            for(MultipartFile file:mpffiles){
                if(videoService.checkVideoContentType(file)){
                    files.add(videoService.saveMultipartToDisk(uploadId, file, accessToken, refreshToken));
                }
            }
            if(files.isEmpty()){
                return "msg:No valid files uploaded. Please, check and try again";
            }
        }
        
        return Arrays.toString(files.toArray()).replace("[", "").replace("]", "").replace(" ", "");

    }
	
	@RequestMapping(value = "/uploadStaticVideo", method = RequestMethod.GET)
	public ResponseEntity uploadStaticVideo(HttpServletRequest request) {
		logger.info("uploadStaticVideo controller invoked");
		String accessToken = request.getParameter("accessToken");
		String refreshToken = request.getParameter("refreshToken");
		try{
			videoService.uploadStaticVideo(accessToken, refreshToken);
			return new ResponseEntity(HttpStatus.OK);
		}catch(Exception e){
			logger.error("uploadStaticVideo.ERROR. " + e.getMessage());
			e.printStackTrace();
			
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
    
}
