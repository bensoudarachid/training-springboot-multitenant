package com.royasoftware;

/*
 * acme4j - Java ACME client
 *
 * Copyright (C) 2015 Richard "Shred" Körber
 *   http://acme4j.shredzone.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.ProcessBuilder.Redirect;
import java.net.URI;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.swing.JOptionPane;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.Registration;
import org.shredzone.acme4j.RegistrationBuilder;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Challenge;
import org.shredzone.acme4j.challenge.Dns01Challenge;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeConflictException;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.CertificateUtils;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
//import ch.qos.logback.core.status.Status;

/**
 * A simple client test tool.
 * <p>
 * Pass the names of the domains as parameters.
 */
//@Component
public class LetsencryptMonitor {
//	private static final String CERT_PATH = "C:/Programm/Apache24/conf/ssl/royasoftware.com-acme4j";
	
	static private String APACHE_PATH = "C:"+File.separator+"Programm"+File.separator+"Apache24";
//	static private String CERT_PATH = "C:/Programm/Apache24/conf/ssl/royasoftware.com-acme4j";
	static {
		if( System.getenv("NODE_ENV")!= null && System.getenv("NODE_ENV").equals("production") )
//			CERT_PATH = "C:/Programme/Apache24/conf/ssl/royasoftware.com-acme4j";
			APACHE_PATH = "C:"+File.separator+"Programme"+File.separator+"Apache24";
	}

	static private String CERT_PATH = APACHE_PATH+File.separator+"conf"+File.separator+"ssl"+File.separator+"royasoftware.com-acme4j";
	
	// File name of the User Key Pair
	private static final File USER_KEY_FILE = new File(CERT_PATH +File.separator+"rbensoudauser.key");

	// File name of the Domain Key Pair
	private static final File DOMAIN_KEY_FILE = new File(CERT_PATH + File.separator+"royasoftwaredomain.key");

	// File name of the CSR
	private static final File DOMAIN_CSR_FILE = new File(CERT_PATH + File.separator+"royasoftwaredomain.csr");

	// File name of the signed certificate
	private static final File DOMAIN_CHAIN_FILE = new File(CERT_PATH + File.separator+"royasoftwaredomain-chain.crt");

	// Challenge type to be used
	private static final ChallengeType CHALLENGE_TYPE = ChallengeType.HTTP;

	// RSA key size of generated key pairs
	private static final int KEY_SIZE = 2048;

	private static final Logger logger = LoggerFactory.getLogger(LetsencryptMonitor.class);

	private enum ChallengeType {
		HTTP, DNS, TLSSNI
	}

	/**
	 * Generates a certificate for the given domains. Also takes care for the
	 * registration process.
	 *
	 * @param domains
	 *            Domains to get a common certificate for
	 */
	public void fetchCertificate(Collection<String> domains) throws IOException, AcmeException {
		// Load the user key file. If there is no key file, create a new one.
		KeyPair userKeyPair = loadOrCreateUserKeyPair();

		// Create a session for Let's Encrypt.
		// Use "acme://letsencrypt.org" for production server
		// Session session = new Session("acme://letsencrypt.org/staging",
		// userKeyPair);
		Session session = new Session("acme://letsencrypt.org", userKeyPair);

		// Get the Registration to the account.
		// If there is no account yet, create a new one.
		Registration reg = findOrRegisterAccount(session);

		// Separately authorize every requested domain.
		for (String domain : domains) {
			authorize(reg, domain);
		}

		// Load or create a key pair for the domains. This should not be the
		// userKeyPair!
		KeyPair domainKeyPair = loadOrCreateDomainKeyPair();

		// Generate a CSR for all of the domains, and sign it with the domain
		// key pair.
		CSRBuilder csrb = new CSRBuilder();
		csrb.addDomains(domains);
		csrb.sign(domainKeyPair);

		// Write the CSR to a file, for later use.
		try (Writer out = new FileWriter(DOMAIN_CSR_FILE)) {
			csrb.write(out);
		}

		// Now request a signed certificate.
		Certificate certificate = reg.requestCertificate(csrb.getEncoded());

		logger.info("Success! The certificate for domains " + domains + " has been generated!");
		logger.info("Certificate URI: " + certificate.getLocation());

		// Download the leaf certificate and certificate chain.
		X509Certificate cert = certificate.download();
		X509Certificate[] chain = certificate.downloadChain();

		// Write a combined file containing the certificate and chain.
		try (FileWriter fw = new FileWriter(DOMAIN_CHAIN_FILE)) {
			CertificateUtils.writeX509CertificateChain(fw, cert, chain);
		}

		// That's all! Configure your web server to use the DOMAIN_KEY_FILE and
		// DOMAIN_CHAIN_FILE for the requested domains.
	}

	/**
	 * Loads a user key pair from {@value #USER_KEY_FILE}. If the file does not
	 * exist, a new key pair is generated and saved.
	 * <p>
	 * Keep this key pair in a safe place! In a production environment, you will
	 * not be able to access your account again if you should lose the key pair.
	 *
	 * @return User's {@link KeyPair}.
	 */
	private KeyPair loadOrCreateUserKeyPair() throws IOException {
		logger.info("USER_KEY_FILE="+USER_KEY_FILE); 
		if (USER_KEY_FILE.exists()) {
			// If there is a key file, read it
			try (FileReader fr = new FileReader(USER_KEY_FILE)) {
				return KeyPairUtils.readKeyPair(fr);
			}

		} else {
			// If there is none, create a new key pair and save it
			KeyPair userKeyPair = KeyPairUtils.createKeyPair(KEY_SIZE);
			try (FileWriter fw = new FileWriter(USER_KEY_FILE)) {
				KeyPairUtils.writeKeyPair(userKeyPair, fw);
			}
			return userKeyPair;
		}
	}

	/**
	 * Loads a domain key pair from {@value #DOMAIN_KEY_FILE}. If the file does
	 * not exist, a new key pair is generated and saved.
	 *
	 * @return Domain {@link KeyPair}.
	 */
	private KeyPair loadOrCreateDomainKeyPair() throws IOException {
		if (DOMAIN_KEY_FILE.exists()) {
			try (FileReader fr = new FileReader(DOMAIN_KEY_FILE)) {
				return KeyPairUtils.readKeyPair(fr);
			}
		} else {
			KeyPair domainKeyPair = KeyPairUtils.createKeyPair(KEY_SIZE);
			try (FileWriter fw = new FileWriter(DOMAIN_KEY_FILE)) {
				KeyPairUtils.writeKeyPair(domainKeyPair, fw);
			}
			return domainKeyPair;
		}
	}

	/**
	 * Finds your {@link Registration} at the ACME server. It will be found by
	 * your user's public key. If your key is not known to the server yet, a new
	 * registration will be created.
	 * <p>
	 * This is a simple way of finding your {@link Registration}. A better way
	 * is to get the URI of your new registration with
	 * {@link Registration#getLocation()} and store it somewhere. If you need to
	 * get access to your account later, reconnect to it via
	 * {@link Registration#bind(Session, URI)} by using the stored location.
	 *
	 * @param session
	 *            {@link Session} to bind with
	 * @return {@link Registration} connected to your account
	 */
	private Registration findOrRegisterAccount(Session session) throws AcmeException {
		Registration reg;

		try {
			// Try to create a new Registration.
			reg = new RegistrationBuilder().create(session);
			logger.info("Registered a new user, URI: " + reg.getLocation());

			// This is a new account. Let the user accept the Terms of Service.
			// We won't be able to authorize domains until the ToS is accepted.
			URI agreement = reg.getAgreement();
			logger.info("Terms of Service: " + agreement);
			acceptAgreement(reg, agreement);
		} catch (AcmeConflictException ex) {
			// The Key Pair is already registered. getLocation() contains the
			// URL of the existing registration's location. Bind it to the
			// session.
			reg = Registration.bind(session, ex.getLocation());
//			logger.info("Account does already exist, URI: " + reg.getLocation(), ex);
			logger.info("Account does already exist, URI: " + reg.getLocation());
		}

		return reg;
	}

	/**
	 * Authorize a domain. It will be associated with your account, so you will
	 * be able to retrieve a signed certificate for the domain later.
	 * <p>
	 * You need separate authorizations for subdomains (e.g. "www" subdomain).
	 * Wildcard certificates are not currently supported.
	 *
	 * @param reg
	 *            {@link Registration} of your account
	 * @param domain
	 *            Name of the domain to authorize
	 */
	private void authorize(Registration reg, String domain) throws AcmeException {
		// Authorize the domain.
		Authorization auth = reg.authorizeDomain(domain);
		logger.info("Authorization for domain " + domain);

		// Find the desired challenge and prepare it.
		Challenge challenge = null;
		switch (CHALLENGE_TYPE) {
		case HTTP:
			challenge = httpChallenge(auth, domain);
			break;

		case DNS:
			challenge = dnsChallenge(auth, domain);
			break;

		case TLSSNI:
			challenge = tlsSniChallenge(auth, domain);
			break;
		}

		if (challenge == null) {
			throw new AcmeException("No challenge found");
		}

		// If the challenge is already verified, there's no need to execute it
		// again.
		if (challenge.getStatus() == Status.VALID) {
			return;
		}

		// Now trigger the challenge.
		challenge.trigger();

		// Poll for the challenge to complete.
		try {
			int attempts = 10;
			while (challenge.getStatus() != Status.VALID && attempts-- > 0) {
				// Did the authorization fail?
				logger.info("Attempt " + (10 - attempts));
				if (challenge.getStatus() == Status.INVALID) {
					throw new AcmeException("Challenge failed... Giving up.");
				}

				// Wait for a few seconds
				Thread.sleep(1000);

				// Then update the status
				challenge.update();
			}
		} catch (InterruptedException ex) {
			logger.error("interrupted", ex);
			Thread.currentThread().interrupt();
		}

		// All reattempts are used up and there is still no valid authorization?
		if (challenge.getStatus() != Status.VALID) {
			throw new AcmeException("Failed to pass the challenge for domain " + domain + ", ... Giving up.");
		}
	}

	/**
	 * Prepares a HTTP challenge.
	 * <p>
	 * The verification of this challenge expects a file with a certain content
	 * to be reachable at a given path under the domain to be tested.
	 * <p>
	 * This example outputs instructions that need to be executed manually. In a
	 * production environment, you would rather generate this file
	 * automatically, or maybe use a servlet that returns
	 * {@link Http01Challenge#getAuthorization()}.
	 *
	 * @param auth
	 *            {@link Authorization} to find the challenge in
	 * @param domain
	 *            Domain name to be authorized
	 * @return {@link Challenge} to verify
	 */
	public Challenge httpChallenge(Authorization auth, String domain) throws AcmeException {
		// Find a single http-01 challenge
		Http01Challenge challenge = auth.findChallenge(Http01Challenge.TYPE);
		if (challenge == null) {
			throw new AcmeException("Found no " + Http01Challenge.TYPE + " challenge, don't know what to do...");
		}

		// Output the challenge, wait for acknowledge...
		logger.info("Please create a file in your web server's base directory.");
		logger.info(
				"It must be reachable at: http://" + domain + "/.well-known/acme-challenge/" + challenge.getToken());
		logger.info("File name: " + challenge.getToken());
		logger.info("Content: " + challenge.getAuthorization());
		logger.info("The file must not contain any leading or trailing whitespaces or line breaks!");
		logger.info("If you're ready, dismiss the dialog...");

		try {
			System.out.println("Write certificate challenge to file: "+APACHE_PATH+"/htdocs/.well-known/acme-challenge/" + challenge.getToken());
			FileWriter fw = new FileWriter(
					APACHE_PATH+"/htdocs/.well-known/acme-challenge/" + challenge.getToken());
//					"C:\\Programm\\Apache24\\htdocs\\.well-known\\acme-challenge\\" + challenge.getToken());
			System.out.println("Write to file done");
			fw.write(challenge.getAuthorization());
			fw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// StringBuilder message = new StringBuilder();
		// message.append("Please create a file in your web server's base
		// directory.\n\n");
		// message.append("http://").append(domain).append("/.well-known/acme-challenge/").append(challenge.getToken()).append("\n\n");
		// message.append("Content:\n\n");
		// message.append(challenge.getAuthorization());
		// acceptChallenge(message.toString());

		return challenge;
	}

	/**
	 * Prepares a DNS challenge.
	 * <p>
	 * The verification of this challenge expects a TXT record with a certain
	 * content.
	 * <p>
	 * This example outputs instructions that need to be executed manually. In a
	 * production environment, you would rather configure your DNS
	 * automatically.
	 *
	 * @param auth
	 *            {@link Authorization} to find the challenge in
	 * @param domain
	 *            Domain name to be authorized
	 * @return {@link Challenge} to verify
	 */
	public Challenge dnsChallenge(Authorization auth, String domain) throws AcmeException {
		// Find a single dns-01 challenge
		Dns01Challenge challenge = auth.findChallenge(Dns01Challenge.TYPE);
		if (challenge == null) {
			throw new AcmeException("Found no " + Dns01Challenge.TYPE + " challenge, don't know what to do...");
		}

		// Output the challenge, wait for acknowledge...
		logger.info("Please create a TXT record:");
		logger.info("_acme-challenge." + domain + ". IN TXT " + challenge.getDigest());
		logger.info("If you're ready, dismiss the dialog...");

		StringBuilder message = new StringBuilder();
		message.append("Please create a TXT record:\n\n");
		message.append("_acme-challenge." + domain + ". IN TXT " + challenge.getDigest());
		acceptChallenge(message.toString());

		return challenge;
	}

	/**
	 * Prepares a TLS-SNI challenge.
	 * <p>
	 * The verification of this challenge expects that the web server returns a
	 * special validation certificate.
	 * <p>
	 * This example outputs instructions that need to be executed manually. In a
	 * production environment, you would rather configure your web server
	 * automatically.
	 *
	 * @param auth
	 *            {@link Authorization} to find the challenge in
	 * @param domain
	 *            Domain name to be authorized
	 * @return {@link Challenge} to verify
	 */
	@SuppressWarnings("deprecation") // until tls-sni-02 is supported
	public Challenge tlsSniChallenge(Authorization auth, String domain) throws AcmeException {
		// Find a single tls-sni-01 challenge
		org.shredzone.acme4j.challenge.TlsSni01Challenge challenge = auth
				.findChallenge(org.shredzone.acme4j.challenge.TlsSni01Challenge.TYPE);
		if (challenge == null) {
			throw new AcmeException("Found no " + org.shredzone.acme4j.challenge.TlsSni01Challenge.TYPE
					+ " challenge, don't know what to do...");
		}

		// Get the Subject
		String subject = challenge.getSubject();

		// Create a validation key pair
		KeyPair domainKeyPair;
		try (FileWriter fw = new FileWriter("tlssni.key")) {
			domainKeyPair = KeyPairUtils.createKeyPair(2048);
			KeyPairUtils.writeKeyPair(domainKeyPair, fw);
		} catch (IOException ex) {
			throw new AcmeException("Could not write keypair", ex);
		}

		// Create a validation certificate
		try (FileWriter fw = new FileWriter("tlssni.crt")) {
			X509Certificate cert = CertificateUtils.createTlsSniCertificate(domainKeyPair, subject);
			CertificateUtils.writeX509Certificate(cert, fw);
		} catch (IOException ex) {
			throw new AcmeException("Could not write certificate", ex);
		}

		// Output the challenge, wait for acknowledge...
		logger.info("Please configure your web server.");
		logger.info("It must return the certificate 'tlssni.crt' on a SNI request to:");
		logger.info(subject);
		logger.info("The matching keypair is available at 'tlssni.key'.");
		logger.info("If you're ready, dismiss the dialog...");

		StringBuilder message = new StringBuilder();
		message.append("Please use 'tlssni.key' and 'tlssni.crt' cert for SNI requests to:\n\n");
		message.append("https://").append(subject).append("\n\n");
		acceptChallenge(message.toString());

		return challenge;
	}

	/**
	 * Presents the instructions for preparing the challenge validation, and
	 * waits for dismissal. If the user cancelled the dialog, an exception is
	 * thrown.
	 *
	 * @param message
	 *            Instructions to be shown in the dialog
	 */
	public void acceptChallenge(String message) throws AcmeException {
		int option = JOptionPane.showConfirmDialog(null, message, "Prepare Challenge", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.CANCEL_OPTION) {
			throw new AcmeException("User cancelled the challenge");
		}
	}

	/**
	 * Presents the user a link to the Terms of Service, and asks for
	 * confirmation. If the user denies confirmation, an exception is thrown.
	 *
	 * @param reg
	 *            {@link Registration} User's registration
	 * @param agreement
	 *            {@link URI} of the Terms of Service
	 */
	public void acceptAgreement(Registration reg, URI agreement) throws AcmeException {
		int option = JOptionPane.showConfirmDialog(null, "Do you accept the Terms of Service?\n\n" + agreement,
				"Accept ToS", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.NO_OPTION) {
			throw new AcmeException("User did not accept Terms of Service");
		}

		// Motify the Registration and accept the agreement
		reg.modify().setAgreement(agreement).commit();
		logger.info("Updated user's ToS");
	}

	private Date getCertificateValidationDate() throws Exception {
		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate) factory
				.generateCertificate(new FileInputStream(DOMAIN_CHAIN_FILE));

		return certificate.getNotAfter();
	}

	/**
	 * Invokes this example.
	 *
	 * @param args
	 *            Domains to get a certificate for
	 */
	public void createSchoolCertificate() {

		String[] domainArray = new String[] {
				"reactlearning.school.royasoftware.com", "abibislearning.school.royasoftware.com",
				"it.school.royasoftware.com", "hanya.school.royasoftware.com", "name1.school.royasoftware.com",
				"name2.school.royasoftware.com", "name3.school.royasoftware.com", "name4.school.royasoftware.com",
				"name5.school.royasoftware.com", "name6.school.royasoftware.com", "name7.school.royasoftware.com",
				"name8.school.royasoftware.com", "name9.school.royasoftware.com" };
		// String[] domainArray = new String[]{"royasoftware.com"};

		logger.info("Create School Certificate. Starting up...");

		Security.addProvider(new BouncyCastleProvider());

		Collection<String> domains = Arrays.asList(domainArray);
		try {
			// LetsencryptMonitor ct = new LetsencryptMonitor();
			fetchCertificate(domains);
		} catch (Exception ex) {
			logger.error("Failed to get a certificate for domains " + domains, ex);
		}
	}

//	@Scheduled(cron = "0 25 12 * * *")
	public void renewCertificate() throws Exception {
		Calendar c = Calendar.getInstance();
		// c.add(field, amount); // getCertificateValidationDate()

		c.add(Calendar.DATE, 17);
		c.after(getCertificateValidationDate());
		logger.info("After x " + c.getTime());
		logger.info("After x days from now will we be over expiry date " + getCertificateValidationDate() + "? "
				+ c.getTime().after(getCertificateValidationDate()));
		if( System.getenv("NODE_ENV")== null || !System.getenv("NODE_ENV").equals("production") )
			logger.info("Wont renew cert because this is dev env");
		else if (c.getTime().after(getCertificateValidationDate())) {
			logger.info("*****Renew (recreate) cert****");
//			KeyPair userKeyPair = loadOrCreateUserKeyPair();
//			Session session = new Session("acme://letsencrypt.org", userKeyPair);
//			PKCS10CertificationRequest csr = CertificateUtils.readCSR(new FileInputStream(DOMAIN_CSR_FILE));
//			Registration reg = findOrRegisterAccount(session);
//			Certificate certificate = reg.requestCertificate(csr.getEncoded());
//			X509Certificate cert = certificate.download();
//			X509Certificate[] chain = certificate.downloadChain();
//			try (FileWriter fw = new FileWriter(DOMAIN_CHAIN_FILE)) {
//				CertificateUtils.writeX509CertificateChain(fw, cert, chain);
//			}
			
			try {
				logger.info("Create School Cert now 3. Restart apache");
				createSchoolCertificate();
				restartApache();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		} else
			logger.info("Wont renew cert");
	}

	public void restartApache() {
		Process process = null;
		try {
//			process = Runtime.getRuntime().exec(APACHE_PATH+"/bin/httpd.exe -k restart");
//			process.waitFor();
			ProcessBuilder pb = new ProcessBuilder(APACHE_PATH+File.separator+"bin"+File.separator+"httpd.exe","-k","restart","-n","apache24");
			pb.redirectOutput(Redirect.INHERIT);
			pb.redirectError(Redirect.INHERIT);
			Process p = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String... args) {
		// if (args.length == 0) {
		// System.err.println("Usage: ClientTest <domain>...");
		// System.exit(1);
		// }
		try {
			LetsencryptMonitor letsencryptMonitor = new LetsencryptMonitor();
			// letsencryptMonitor.getCertificateValidationDate();
			letsencryptMonitor.renewCertificate();
			// logger.info("x509.getNotAfter()="+letsencryptMonitor.getCertificateValidationDate());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
