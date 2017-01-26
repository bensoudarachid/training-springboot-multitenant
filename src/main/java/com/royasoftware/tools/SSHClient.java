package com.royasoftware.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.sshtools.j2ssh.io.IOStreamConnector;
import com.sshtools.j2ssh.io.IOStreamConnectorState;
import com.sshtools.j2ssh.connection.*;
import com.royasoftware.RouterMonitor;
import com.sshtools.j2ssh.SshClient;

import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient;
import com.sshtools.j2ssh.session.SessionChannelClient;

import com.sshtools.j2ssh.configuration.SshConnectionProperties;

import com.sshtools.j2ssh.transport.HostKeyVerification;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile;
import com.sshtools.j2ssh.transport.publickey.SshPublicKey;

public class SSHClient {
	private static final Logger log = LoggerFactory.getLogger(SSHClient.class);
	SshClient ssh = null;

	SshConnectionProperties properties = null;

	SessionChannelClient session = null;

//	public SSHClient(String hostName, String userName, String passwd) {
//
//		try {
//			PublicKeyAuthenticationClient pk = new PublicKeyAuthenticationClient();
//			ssh = new SshClient();
//			pk.setUsername(userName);
//			HostKeyVerification hkv = new HostKeyVerification() {
//				public boolean verifyHost(String name, SshPublicKey key)
//						throws com.sshtools.j2ssh.transport.TransportProtocolException {
//					return true;
//				}
//			};
//			ssh.connect(hostName, hkv);
//			// ssh.connect("81.89.196.100");
//			String keyfile = "D:/GO-Doc/ProductionSystem/HD-IT/SSH/realpact-key/realpactssh";
//			SshPrivateKeyFile file = SshPrivateKeyFile.parse(new File(keyfile));
//			SshPrivateKey key = file.toPrivateKey(passwd);
//
//			// Set the key and authenticate
//			pk.setKey(key);
//			int result = ssh.authenticate(pk);
//			if (result != AuthenticationProtocolState.COMPLETE) {
//				throw new Exception("ssh authentication error");
//			}
//			
//			// Create a password authentication instance
////			PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();
////
////			pwd.setUsername(userName);
////			pwd.setPassword(passwd);
//
//			// Try the authentication
////			int result = ssh.authenticate(pwd);
//
//			// Evaluate the result
////			if (result == AuthenticationProtocolState.COMPLETE) {
////
////				System.out.println("Connection Authenticated");
////			}
//		} catch (Exception e) {
//			System.out.println("Exception : " + e.getMessage());
//		}
//
//	}// end of method.

	public SSHClient(String hostName, String username, String password) {

		try {
			ssh = new SshClient();
			ssh.connect(hostName,new IgnoreHostKeyVerification());
			
			PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();
			pwd.setUsername(username);
			pwd.setPassword(password);
			
			int result = ssh.authenticate(pwd);
			
			if (result != AuthenticationProtocolState.COMPLETE) {
				throw new Exception("ssh authentication error");
			}
			
			// Create a password authentication instance
//			PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();
//
//			pwd.setUsername(userName);
//			pwd.setPassword(passwd);

			// Try the authentication
//			int result = ssh.authenticate(pwd);

			// Evaluate the result
//			if (result == AuthenticationProtocolState.COMPLETE) {
//
//				System.out.println("Connection Authenticated");
//			}
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
		}

	}// end of method.

	public String execCmd(String cmd) {
		StringBuffer theOutput = new StringBuffer();
		try {
			// The connection is authenticated we can now do some real work!
			session = ssh.openSessionChannel();
			session.startShell();
			ChannelOutputStream out = session.getOutputStream();
//			cmd = "cd /usr/local/Mago\n";
//			out.write(cmd.getBytes());
//			cmd = "ls -d */\n";
			out.write(cmd.getBytes());
			// logger.info("Written! get result");
			out.close();

			ChannelInputStream in = session.getInputStream();
			BufferedReader input = new BufferedReader(new InputStreamReader(in));
			String line;
			Vector directoryList = new Vector();
			while ((line = input.readLine()) != null) {
//				logger.info("line="+line);
				theOutput.append(line+"\n");
//				directoryList.add(line.subSequence(0, line.lastIndexOf("/")));
//				directoryList.add(line);
			}
			session.close();
//			ssh.disconnect();
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
		}

		return theOutput.toString();
	}

}