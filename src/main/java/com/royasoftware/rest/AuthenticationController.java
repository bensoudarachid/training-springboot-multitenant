package com.royasoftware.rest;

import com.royasoftware.model.Account;
import com.royasoftware.service.AccountService;
import com.royasoftware.service.RoleService;
import com.royasoftware.settings.email.SmtpMailSender;
import com.royasoftware.settings.errors.InvalidRequestException;
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
	
	@RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Account> register(@RequestParam("username") String username,
			@RequestParam("password") String password, @RequestParam("email") String email) {

		// System.out.println("errors = "+errors);
		// Check if account is unique
		// if(errors.hasErrors()){
		// throw new InvalidRequestException("Username already exists", errors);
		// }
		// try{

//		account.setRegisterId(new Integer(rand));
//		account.setRegisterDate(new Date());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Account account = new Account();
		account.setUsername(username);
		account.setPassword(password);
		Account createdAccount = accountService.createNewAccount(account);
		createdAccount.setPassword("");
		return new ResponseEntity<Account>(createdAccount, HttpStatus.CREATED);
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
		
		if(account == null)
			return new ResponseEntity<String>("User not found", HttpStatus.BAD_REQUEST);
		System.out.println("account.getRegistrationId()="+account.getRegistrationId());
		System.out.println("param regid="+regid);
		if( !account.getRegistrationId().equals(regid) )
//			return new ResponseEntity<String>("Bad request!", HttpStatus.BAD_REQUEST);
			throw new MessagingException("Bad request!!");
		if( account.isEnabled() )
			return new ResponseEntity<String>("This account is already active", HttpStatus.OK);
		account.setEnabled(true);
		account.setRegistrationId(null);
		account = accountService.saveAccount(account);
		return new ResponseEntity<String>("ok", HttpStatus.OK);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ExceptionJSONInfo handleDataIntegrityException(HttpServletRequest request, Exception ex) {
		System.out.println("Ok. Here is the DataIntegrityViolationException handler."+ex.getClass().getName()+". message="+ex.getMessage());
		ExceptionJSONInfo response = new ExceptionJSONInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setMessage("User exists already");
		return response;
	}
	
	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ExceptionJSONInfo handleGeneralException(HttpServletRequest request, Exception ex) {
		System.out.println("Ok. Here is the general exception handler."+ex.getClass().getName()+". message="+ex.getMessage());
		ExceptionJSONInfo response = new ExceptionJSONInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setMessage(ex.getMessage());
		return response;
	}

}
