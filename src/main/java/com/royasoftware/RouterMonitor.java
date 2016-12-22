package com.royasoftware;

import java.io.BufferedReader;
import com.royasoftware.tools.SSHClient;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.sound.sampled.*;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.royasoftware.model.Todo;

@lombok.Getter
@lombok.Setter

@Component
public class RouterMonitor {
	final private static short STATUS_CONNECTION_OK = 0;
	final private static short STATUS_CONNECTION_LOST = 1;
	final private static short WAIT_FOR_PING_MAX_ITERATIONS = 14;
	final private static String SOUND_DOWN = "E:\\Samples\\FreeSound\\FX\\Powerdown.wav";
	final private static String SOUND_HEROKU_PING = "E:\\Samples\\Hip Hop 3\\Scratches\\hit me b.wav";

	// private short status = STATUS_CONNECTION_OK;
	private short waitBeforeRebootLoop = 0;
	private short watingForConnectionLoop = 0;

	// private String lastIP = null;

	private static final Logger logger = Logger.getLogger(RouterMonitor.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	String host;

	// String version = null;

	SSHClient sshClient = null;

	@Autowired
	public RouterMonitor(@Value("${app.router.host}") String host, @Value("${app.router.login}") String login,
			@Value("${app.router.passwd}") String passwd) throws Exception {
		setHost(host);
		sshClient = new SSHClient(host, login, passwd);
		logger.info("calling updateDomainToIpMappping one tine on router monitor init");
		updateDomainToIpMappping();
	}

	public void reconnect() {
		try {
			// wanSetIspLogin();
			// Thread.sleep(5000);
			// logger.info("\n\n\n");
			// pppDisconnect();
			// // sshClient.execCmd("ppp config 0.8.35 1 down\n");
			// Thread.sleep(10000);
			// logger.info("\n\n\n");
			// pppConnect();
			// sshClient.execCmd("ppp config 0.8.35 1 up\n");
			// Thread.sleep(10000);
			// logger.info("\n\n\n");
			// wanSetIspLogin();
			// Thread.sleep(2000);
			// logger.info("\n\n\n");

			Process process = Runtime.getRuntime()
					.exec("C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\router\\reboot.au3");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void incrementWatingForConnectionLoop() {
		this.watingForConnectionLoop += 1;
	}

	public void incrementWaitBeforeRebootLoop() {
		this.waitBeforeRebootLoop += 1;
	}

	private String getPublicIp() {
		String ip = null;
		try {
			for (int i = 0; i < 30; i++) {
				try {
					ip = connectUrl("http://www.icanhazip.com/");
					// log.info("Ip = "+ip);
					return ip;
				} catch (Exception e) {
					logger.info("Could not connect to get public ip! number of tries: " + i);
					// e.printStackTrace();
				}
				if (ip != null) {
					break;
				}
				Thread.sleep(100 * i + 100);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ip;
	}

	private static boolean pingHost(String host, int timeout) {
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(host, 80), timeout);
			return true;
		} catch (IOException e) {
			return false; // Either timeout or unreachable or failed DNS lookup.
		}
	}

	private String getActualIp() throws Exception {
		String address = null;
		try {
			// InetAddress inetAddress =
			// java.net.InetAddress.getByName("mama.royasoftware.com");
			// address = inetAddress.getHostAddress();
			// logger.info("address=" + address);

			for (int i = 0; i < 30; i++) {
				try {
					InetAddress inetAddress = java.net.InetAddress.getByName("mama.royasoftware.com");
					address = inetAddress.getHostAddress();
				} catch (Exception e) {
					logger.info("Could not connect to get actual ip! number of tries: " + i);
					// e.printStackTrace();
				}
				if (address != null) {
					break;
				}
				Thread.sleep(100 * i + 100);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return address;
	}

	public String pppConnect() throws Exception {
		return sshClient.execCmd("ppp config 0.8.35 1 up\n");
	}

	public String pppDisconnect() throws Exception {
		return sshClient.execCmd("ppp config 0.8.35 1 down\n");
	}

	public String wanSetIspLogin() throws Exception {
		return sshClient.execCmd("wan config 0.8.35 1 --username f_chahid --password f_chahid\n");
	}

	private String connectUrl(String url) throws Exception {
		String USER_AGENT = "Mozilla/5.0";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		if (responseCode != 200)
			throw new Exception("Could not connect to url: " + url);
		// log.info("\nSending 'GET' request to URL : " + url);
		logger.info("Calling " + url + ". Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		return response.toString();
	}

	private String sendJokerDomainMappingUpdate() throws Exception {
		String out = connectUrl(
				"http://svc.joker.com/nic/update?username=8508eb847ea580bc&password=b592711a66266d6f&hostname=*.royasoftware.com");
		// log.info("Joker returned: " + out);
		return out;
	}

	public void soundClipTest(String soundPath) {
		try {
			// Open an audio input stream.
			File soundFile = new File(soundPath);
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
			// Get a sound clip resource.
			Clip clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	private void updateDomainToIpMappping() throws Exception {
		String ip = getPublicIp();
		logger.info("public ip = " + ip);
		String actualIP = getActualIp();
		if (ip == null || actualIP == null) {
			logger.info("one of the ips is null. no update");
			return;
		}
		if (ip != null && actualIP != null && !actualIP.equals(ip)) {
			logger.info("Different ips, actual ip = " + actualIP + ". Call domain mapping update");
			logger.info("Joker response " + sendJokerDomainMappingUpdate());
		} else if (ip != null && actualIP != null && actualIP.equals(ip)) {
			logger.info("Same ips, no domain mapping update");
		}

	}

	@Scheduled(fixedDelay = 15000)
	public void checkAndFixRouterConnection() {
		try {
			boolean jokerUpdated = true;
			boolean pingOk = pingHost("google.com", 6000);
			short status = STATUS_CONNECTION_OK;
			// if( !pingOk )
			// Thread.sleep(3000);
			System.out.print(".");
			while (!pingOk || !jokerUpdated) {
				Thread.sleep(3000);
				switch (status) {
				case STATUS_CONNECTION_OK:
					for (int i = 0; i < WAIT_FOR_PING_MAX_ITERATIONS; i++) {
						pingOk = pingHost("google.com", 6000);
						if (pingOk) {
							logger.info("Ping is back. Connection still valid");
							updateDomainToIpMappping();
							break;
						}
						soundClipTest(SOUND_DOWN);
						Thread.sleep(3000);
					}
					if (!pingOk) {
						status = STATUS_CONNECTION_LOST;
						jokerUpdated = false;
						reconnect();
					}
					break;
				case STATUS_CONNECTION_LOST:
					pingOk = pingHost("google.com", 6000);
					if (pingOk) {
						status = STATUS_CONNECTION_OK;
						connectUrl("http://abbaslearning.royasoftware.com");
						updateDomainToIpMappping();
						// String ip = getPublicIp();
						// String actualIP = getActualIp();
						// if (actualIP != null && !actualIP.equals(ip)) {
						// logger.info("Different ips, send domain mapping");
						// logger.info("Joker response " +
						// sendJokerDomainMappingUpdate());
						// } else if (actualIP != null && actualIP.equals(ip)) {
						// logger.info("Same ips, no domain mapping update");
						// }
						jokerUpdated = true;
						break;
					}
					break;
				}
			}
			// String reboot = rm.reboot();
			// logger.info("reboot = "+reboot);
			// String ps = rm.ps();
			// logger.info("ps = "+ps);
			// rm.readUrlIntoFile("http://admin:royaZoft@192.168.1.1/rebootinfo.cgi",
			// "logout.html", false);
			// ps = rm.ps();
			// logger.info("ps = "+ps);

			// Process process =
			// Runtime.getRuntime().exec("C:\\Programme\\AutoIt3\\AutoIt3.exe
			// C:\\MyAutoItMacros\\router\\reboot.au3");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Scheduled(fixedDelay = 15000)
	// public void checkAndFixRouterConnectionOld() {
	// System.out.print(".");
	// boolean pingOk = pingHost("google.com", 6000);
	// try {
	// switch (status) {
	// case STATUS_CONNECTION_OK:
	// if (!pingOk) {
	// logger.info(dateFormat.format(new Date()) + ": Connection is ok. No
	// Ping!");
	// boolean pingAgain;
	// for (int i = 0; i < 3; i++) {
	// logger.info("Please test connection before i reboot router. "
	// + (((WAIT_FOR_PING_MAX_ITERATIONS - getWaitBeforeRebootLoop()) * 12) - i
	// * 3)
	// + " seconds until launch.");
	// soundClipTest(SOUND_DOWN);
	// pingAgain = pingHost("google.com", 3000);
	// Thread.sleep(3000);
	// if (pingAgain) {
	// logger.info("Connection is back!");
	// setWaitBeforeRebootLoop((short) 0);
	// return;
	// }
	// }
	// incrementWaitBeforeRebootLoop();
	// if (getWaitBeforeRebootLoop() > WAIT_FOR_PING_MAX_ITERATIONS) {
	// logger.info("Reboot now!");
	// setStatus(STATUS_CONNECTION_LOST);
	// setWaitBeforeRebootLoop((short) 0);
	// setWatingForConnectionLoop((short) 0);
	// reconnect();
	// return;
	// // Runtime.getRuntime()
	// // .exec("C:\\Programme\\AutoIt3\\AutoIt3.exe
	// // C:\\MyAutoItMacros\\router\\reboot.au3");
	// }
	//
	// }
	//
	// break;
	// case STATUS_CONNECTION_LOST:
	// if (pingOk) {
	// logger.info(dateFormat.format(new Date()) + ": Connection is out. Ping
	// ok!");
	// setStatus(STATUS_CONNECTION_OK);
	// // This is something we do to get the node server going as
	// // it seems he needs this to get connected from the net. Not
	// // sure though!
	// connectUrl("http://abbaslearning.royasoftware.com");
	// String ip = getPublicIp();
	// String actualIP = getActualIp();
	// if (actualIP != null && !actualIP.equals(ip)) {
	// logger.info("Different ips, send domain mapping");
	// logger.info("Joker response " + sendJokerDomainMappingUpdate());
	// } else if (actualIP != null && actualIP.equals(ip)) {
	// logger.info("Same ips, no domain mapping update");
	// }
	// // setLastIP(ip);
	// } else {
	// incrementWatingForConnectionLoop();
	// }
	// if (getWatingForConnectionLoop() > 15)
	// setWatingForConnectionLoop((short) 0);
	// setWaitBeforeRebootLoop((short) 0);
	// setStatus(STATUS_CONNECTION_OK);
	// break;
	// }
	//
	// // String reboot = rm.reboot();
	// // logger.info("reboot = "+reboot);
	// // String ps = rm.ps();
	// // logger.info("ps = "+ps);
	// // rm.readUrlIntoFile("http://admin:royaZoft@192.168.1.1/rebootinfo.cgi",
	// // "logout.html", false);
	// // ps = rm.ps();
	// // logger.info("ps = "+ps);
	//
	// // Process process =
	// // Runtime.getRuntime().exec("C:\\Programme\\AutoIt3\\AutoIt3.exe
	// // C:\\MyAutoItMacros\\router\\reboot.au3");
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	@Scheduled(fixedDelay = 1400000, initialDelay = 60000)
	public void pingHerokuConnection() {
		soundClipTest(SOUND_HEROKU_PING);
		Calendar rightNow = Calendar.getInstance();
		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		logger.info("Actual hour: " + hour);
		if (hour < 7 || hour > 22) {
			logger.info("No heroku pinging");
			return;
		}

		for (int i = 0; i < 5; i++) {
			try {
				// boolean ping = false;
				boolean pingOk = pingHost("google.com", 6000);
				Thread.sleep(3000);
				if (!pingOk)
					continue;
				int rdm = new Random().nextInt(60);
				logger.info("Sleeping random seconds before heroku call: " + rdm);
				Thread.sleep(rdm * 1000);
				logger.info("Ping heroku server." + (i + 1) + " iteration");
				// ping = pingHost("rlearn.herokuapp.com", 5000);
				String out = connectUrl("http://rlearn.herokuapp.com/bundle.js");
				if (out != null)
					logger.info("heroku is alive!");
				else
					logger.info("heroku has a problem! i got no output from connection");
				// connectUrl("http://abbaslearning.royasoftware.com");
				Thread.sleep(10000);
				// if( ping ){
				return;
				// }
			} catch (Exception e) {
				logger.error("heroku not alive: " + e.getMessage());
				// e.printStackTrace();
			}
		}
		logger.info("Heroku scheduler: i m out");
	}

	public static void main(String[] args) {
		try {
			DOMConfigurator.configure("log4j.xml");
			RouterMonitor rm = new RouterMonitor("192.168.1.1", "admin", "royaZoft");
			// rm.reconnect();
			// rm.pppDisconnect();
			// Thread.sleep(40000);
			// rm.reboot();
			// Thread.sleep(30000);
			// rm.pppConnect();

			// rm.wanSetIspLogin();
			// Thread.sleep(15000);
			// logger.info("\n\n\n");
			// rm.pppDisconnect();
			// sshClient.execCmd("ppp config 0.8.35 1 down\n");
			// Thread.sleep(10000);
			// logger.info("\n\n\n");
			// rm.pppConnect();
			// Thread.sleep(15000);
			// rm.wanSetIspLogin();

			// String ip = rm.getPublicIp();
			// log.info("ip before: "+ip);
			// rm.reboot();
			// ip = rm.getPublicIp();
			// log.info("ip after: "+ip);

			// rm.soundClipTest(SOUND_HEROKU_PING);
			// rm.connectUrl("http://abbaslearning.royasoftware.com");µµ
			// log.info("heroku response = " +
			// rm.connectUrl("http://rlearn.herokuapp.com/bundle.js"));
			// rm.connectUrl("http://abbaslearning.royasoftware.com");
			// Calendar rightNow = Calendar.getInstance();logger.info("");
			// int hour = rightNow.get(Calendar.HOUR_OF_DAY);
			// log.info("Actual hour: " + hour);

			// rm.sendJokerDomainMappingUpdate();
			// rm.nextIteration();

			// rm.updateDomainToIpMappping();
			// logger.info("rm.getActualIp()="+rm.getActualIp());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}