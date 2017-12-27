package com.royasoftware.school.rest;

import com.royasoftware.school.model.Account;
import com.royasoftware.school.service.AccountService;
import com.royasoftware.school.service.RoleService;
import com.royasoftware.school.settings.school.email.SmtpMailSender;
import com.royasoftware.school.settings.school.errors.InvalidRequestException;
import com.royasoftware.school.tools.EmailValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * This controller is responsible to manage the authentication system. Login -
 * Register - Forgot password - Account Confirmation
 */
@RestController
// RequestMapping("/pub/**")
public class AuthenticationController extends BaseController {

	@Autowired
	protected AuthenticationManager authenticationManager;

	@Autowired
	private AccountService accountService;

	@Autowired
	private SmtpMailSender smtpMailSender;

	@RequestMapping(value = "/sample", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Account> sampleGet(HttpServletResponse response) {
		return new ResponseEntity<Account>(accountService.findByUsername("papidakos"), HttpStatus.CREATED);
	}

	@RequestMapping(value = "/sample", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Account> sample(HttpServletResponse response) {
		return new ResponseEntity<Account>(accountService.findByUsername("papidakos"), HttpStatus.CREATED);
	}

	@RequestMapping(value = "/forgot-password", method = RequestMethod.GET)
	public ResponseEntity<String> forgotPassword() throws MessagingException {
		String response = "{success: true}";
		smtpMailSender.send("r.bensouda@gmx.com", "Password forgot", "Forgot password url");
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	/**
	 * 
	 * Create a new user account
	 * 
	 * @param account
	 *            user account
	 * @return created account as json
	 * 
	 */

	@RequestMapping(value = "/registerold", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Account register(@RequestParam("username") String username, @RequestParam("password") String password,
			@RequestParam("email") String email) throws Exception {

		System.out.println("register ");
		// Check if account is unique
		// if(errors.hasErrors()){
		// throw new InvalidRequestException("Username already exists", errors);
		// }
		// try{

		// account.setRegisterId(new Integer(rand));
		// account.setRegisterDate(new Date());
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if (!new EmailValidator().validate(email))
			throw new Exception("Email is not valid");
		if (password.length() < 8) {
			throw new Exception("Password should be greater than 8 characters");
		}

		Account existingAccount = accountService.findByUsername(username);
		if (existingAccount != null)
			throw new Exception("This Account (User name) exists already");
		Account account = new Account();
		account.setUsername(username);
		account.setPassword(password);
		Account createdAccount = accountService.createNewAccount(account);

		createdAccount.setPassword("");
		// return new ResponseEntity<Account>(createdAccount,
		// HttpStatus.CREATED);
		return createdAccount;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody // ResponseEntity<Map<String, Object>>  works too
	public Map<String, Object> registernew(@RequestParam("username") String username,
			@RequestParam("password") String password, @RequestParam("email") String email) throws Exception {

		// System.out.println("errors = "+errors);
		// Check if account is unique
		// if(errors.hasErrors()){
		// throw new InvalidRequestException("Username already exists", errors);
		// }
		// try{
		HashMap<String, Object> errorMap = new HashMap();
		System.out.println("register hna ");

		// account.setRegisterId(new Integer(rand));
		// account.setRegisterDate(new Date());
		HashMap<String, Object> registerResponse = new HashMap();
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if (username == null || username.length() == 0) {
			errorMap.put("username", "required");
		}else if(username.length() > 25){
			errorMap.put("username", "too long (25 chars max)");
		}
		if (password == null || password.length() == 0) {
			errorMap.put("password", "required");
		} else if (password.length() < 8) {
			// throw new Exception("Password should be greater than 8
			// characters");
			errorMap.put("password", "should be greater than 8 characters");
			// registerResponse.put("error", "Password should be greater than 8
			// characters");
			// return registerResponse;
		}
		if (email == null || email.length() == 0) {
			errorMap.put("email", "required");
		} else if (!new EmailValidator().validate(email)) {
			// throw new Exception("Email is not valid");
			errorMap.put("email", "not valid");
		}
		if (!errorMap.containsKey("username")) {
			Account existingAccount = accountService.findByUsername(username);
			if (existingAccount != null)
				errorMap.put("username", "account (User name) exists already");
		}
		// throw new Exception("This Account (User name) exists already");
		if (errorMap.isEmpty()) {
			Account account = new Account();
			account.setUsername(username);
			account.setPassword(password);
			Account createdAccount = accountService.createNewAccount(account);
			createdAccount.setPassword("");
			registerResponse.put("account", createdAccount);
		}
		else
			registerResponse.put("error", errorMap);
//		return new ResponseEntity<Map<String, Object>>(registerResponse, HttpStatus.OK);
		return registerResponse;
		// }catch(Exception e){
		// System.out.println("Ok. Here is the exc handler.
		// "+e.getClass().getName()+". message="+e.getMessage());
		//// ClientErrorInformation error = new
		// ClientErrorInformation(e.toString());
		//// Map<String,Object> errorMap = new HashMap<String,Object>();
		//// errorMap.put(e.toString(), error);
		// return new ResponseEntity(e.getMessage(),
		// HttpStatus.INTERNAL_SERVER_ERROR);
		// }
	}

	/**
	 * 
	 * Create a new user account
	 * 
	 * @param account
	 *            user account
	 * @return created account as json
	 * 
	 */

	@RequestMapping(value = "/regmailconfirm", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> registermailconfirm(@RequestParam("id") Long userid,
			@RequestParam("regid") Integer regid) throws MessagingException {
		Account account = accountService.findByUserid(userid);

		if (account == null)
			return new ResponseEntity<String>("User not found", HttpStatus.BAD_REQUEST);
		System.out.println("account.getRegistrationId()=" + account.getRegistrationId());
		System.out.println("param regid=" + regid);
		if (!account.getRegistrationId().equals(regid))
			// return new ResponseEntity<String>("Bad request!",
			// HttpStatus.BAD_REQUEST);
			throw new MessagingException("Bad request!!");
		if (account.isEnabled())
			return new ResponseEntity<String>("This account is already active", HttpStatus.OK);
		account.setEnabled(true);
		account.setRegistrationId(null);
		account = accountService.saveAccount(account);
		return new ResponseEntity<String>("ok", HttpStatus.OK);
	}

	// @ExceptionHandler(DataIntegrityViolationException.class)
	// @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	// public @ResponseBody ExceptionJSONInfo
	// handleDataIntegrityException(HttpServletRequest request, Exception ex) {
	// System.out.println("Ok. Here is the DataIntegrityViolationException
	// handler."+ex.getClass().getName()+". message="+ex.getMessage());
	// ExceptionJSONInfo response = new ExceptionJSONInfo();
	// response.setUrl(request.getRequestURL().toString());
	// response.setMessage("User exists already");
	// return response;
	// }
	//
	// @ExceptionHandler(RuntimeException.class)
	// @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	// public @ResponseBody ExceptionJSONInfo
	// handleGeneralException(HttpServletRequest request, Exception ex) {
	// System.out.println("Ok. Here is the general exception
	// handler."+ex.getClass().getName()+". message="+ex.getMessage());
	// ExceptionJSONInfo response = new ExceptionJSONInfo();
	// response.setUrl(request.getRequestURL().toString());
	// response.setMessage(ex.getMessage());
	// return response;
	// }

}
