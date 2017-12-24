package com.royasoftware.rest;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.ext.awt.image.codec.util.MemoryCacheSeekableStream;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;

import com.royasoftware.LetsencryptMonitor;
import com.royasoftware.TenantContext;
import com.royasoftware.model.Tenant;
import com.royasoftware.model.Training;
import com.royasoftware.service.TrainingService;
import com.royasoftware.settings.security.CustomUserDetails;
import com.royasoftware.utils.SVGValidator;

@RestController
// RequestMapping("/reactor/api/**")
@RequestMapping("/api/**")
public class ProfileController extends BaseController {
	@Autowired
	private TrainingService trainingService;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.IMAGE_PNG_VALUE }, value = "/profile/logo")
	public ResponseEntity<Object> getProfileLogo(@RequestParam("width") Integer width,
			@RequestParam("height") Integer height) throws Exception {
//		logger.info("getProfileLogo " + " width=" + width + " height=" + height);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		// CustomUserDetails activeUser = TenantContext.getCurrentUser();
		// if( activeUser== null )
		// return new ResponseEntity<Object>(null,
		// HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
		// else
		// logger.info("User connected as bound parameter: name = " +
		// activeUser.getUsername() + ", id = "
		// + activeUser.getId() + ", role = " +
		// activeUser.getAuthorities().toString());
		// logger.info("Calling Get rest controller get training image " +
		// _param);
		width = width > 200 ? 200 : width;
		height = height > 200 ? 200 : height;

		// logger.info("Get Training Image "+_param+", width "+width+",
		// height"+height);
		rdmTimeRdmSuccess();
		// TenantContext.getCurrentUser().getId();
		// TenantContext.getCurrentTenantStoragePath();

		File dir = new File(TenantContext.getCurrentTenantStoragePath("profile"));

		// list the files using a anonymous FileFilter
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().startsWith("logo.");
			}
		});
		File file = null;
		if (files == null || files.length == 0)
			return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
		file = files[0];
		for (int i = 1; i < files.length; i++)
			files[i].delete();

		// File file = new
		// File(TenantContext.getCurrentUserStoragePath()+_param+".png");
		byte[] ret;

		if (file.exists()) {
			// Open the file, then read it in.
			// BufferedImage originalImage = null;
			Image scaledImg = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (file.getName().endsWith(".svg")) {
				ImageIO.write(generatePngFromSvg(file, width, height), "png", baos);
				// FileInputStream ios = new FileInputStream(file);
				// int read = 0;
				// byte[] buffer = new byte[4096];
				// while ((read = ios.read(buffer)) != -1) {
				// baos.write(buffer, 0, read);
				// }
			} else {
				BufferedImage originalImage = ImageIO.read(file);
				scaledImg = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
				BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = thumbnail.createGraphics();

				g.setColor(Color.decode("#f4faff"));
				g.fillRect(0, 0, width, height);

				Point2D center = new Point2D.Float(width / 2, width / 2);
				float[] dist = { 0.0f, 0.5f };
				Color[] colors = { Color.decode("#ffffff"), Color.decode("#d7e7f4") };
				RadialGradientPaint p = new RadialGradientPaint(center, width, dist, colors);

				g.setPaint(p);
				// g.setPaint(Color.decode("#d7e7f4"));
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.fillOval(0, 0, width, height);

				// g.drawImage(scaledImg,0,0,new Color(197, 211, 226),null);

				g.drawImage(scaledImg, 0, 0, null);
				g.dispose();
				g.setComposite(AlphaComposite.Src);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				ImageIO.write(thumbnail, "jpg", baos);
			}
			baos.flush();
			ret = baos.toByteArray();
			baos.close();

			// ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			// InputStream inStream = new FileInputStream(file);
			// copy(inStream, outStream);
			// inStream.close();
			// outStream.close();
			// ret = outStream.toByteArray();
		} else {
			ret = null;
		}
		return new ResponseEntity<Object>(ret, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/profile/data")
	public ResponseEntity<Tenant> getTenantData() throws Exception {
//		logger.info("Here i am get tenant data");
		

    	Properties prop = new Properties();
    	InputStream input = null;
    	String filename = TenantContext.getCurrentTenantStoragePath("profile")+"data.properties";
		input = new FileInputStream(filename);
		if(input==null){
			logger.error("Sorry, unable to find " + filename);
			return new ResponseEntity<Tenant>(HttpStatus.NOT_FOUND);
		}

		//load a properties file from class path, inside static method
		prop.load(input);

	    Tenant tenant = new Tenant();
		tenant.setName1(prop.getProperty("tenant.name1"));
//		logger.info("prop.getProperty(name1)="+prop.getProperty("name1")); 
		tenant.setName2(prop.getProperty("tenant.name2"));
		return new ResponseEntity<Tenant>(tenant, HttpStatus.OK);
		// return "Hello mama: "+_param;
	}

}