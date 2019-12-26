package com.royasoftware.school.rest;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import com.royasoftware.school.TenantContext;
import com.royasoftware.school.cluster.SpringExtension;
import com.royasoftware.school.exception.ValidationException;
import com.royasoftware.school.model.Account;
import com.royasoftware.school.model.Training;
import com.royasoftware.school.service.AccountService;
//import com.royasoftware.school.service.TrainingServFrEndActor;
import com.royasoftware.school.service.TrainingService;
//import com.royasoftware.school.service.TrainingServFrEndActor.Trainings;
import com.royasoftware.school.settings.security.CustomUserDetails;

@RestController
@RequestMapping("/api/**")
public class VersionController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(method = RequestMethod.GET, value = "/version")
	public ResponseEntity<String> getVersion(@RequestParam("vernr") String _versionNr) throws Exception {
//		logger.info("version nr 5 =" + _versionNr);
//		Runtime rt = Runtime.getRuntime();
		// String[] commands = { "gitversion" };
		// Process proc = rt.exec(commands);
//		Properties prop = new Properties();
//		// BufferedReader fr = new BufferedReader(new
//		// SmartEncodingInputStream(new
//		// FileInputStream("gitversion.properties")).getReader());
//		FileInputStream fr = new FileInputStream("gitversion.properties");
//		prop.load(fr);
//		logger.info("prop=" + prop);
//		String vers = null;
//		if (prop.getProperty("\"SemVer\"") != null) {
//			vers  = prop.getProperty("\"SemVer\"") + "_" + prop.getProperty("\"CommitsSinceVersionSource\"");
//			// String vers = prop.getProperty("SemVer") + "_" +
//			// prop.getProperty("\"CommitsSinceVersionSource\"");
//			vers = vers.replace(",", "").replace("\"", "");
//			logger.info("vers 1 =" + vers);
//		} else {
//			vers = prop.getProperty("GitVersion_SemVer") + "_" + prop.getProperty("GitVersion_CommitsSinceVersionSource");
//			// String vers = prop.getProperty("SemVer") + "_" +
//			// prop.getProperty("\"CommitsSinceVersionSource\"");
//			vers = vers.replace(",", "").replace("\"", "");
//			logger.info("vers 2 =" + vers);
//		}
		if (API_VERSION.equals(_versionNr))
			return new ResponseEntity<String>(API_VERSION, HttpStatus.OK);
		else
			return new ResponseEntity<String>("", HttpStatus.NOT_FOUND);
	}

}