package com.royasoftware;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.sound.sampled.*;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.royasoftware.model.Todo;

@lombok.Getter
@lombok.Setter

@Component
public class RouterMonitor {
	final private static short STATUS_CONNECTION_OK = 0;
	final private static short STATUS_CONNECTION_LOST = 1;

	final private static String SOUND_DOWN = "E:\\Samples\\FreeSound\\FX\\Powerdown.wav";
	final private static String SOUND_HEROKU_PING = "E:\\Samples\\Hip Hop 3\\Scratches\\hit me b.wav";

	private short status = STATUS_CONNECTION_OK;
	private short waitBeforeRebootLoop = 0;
	private short watingForConnectionLoop = 0;
	 
	private String lastIP = null;
	
	private static final Logger log = LoggerFactory.getLogger(RouterMonitor.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

//	public short getStatus() {
//		return status;
//	}
//
//	public void setStatus(short status) {
//		this.status = status;
//	}
//
//	public short getWaitBeforeRebootLoop() {
//		return waitBeforeRebootLoop;
//	}
//
//	public void setWaitBeforeRebootLoop(short waitBeforeRebootLoop) {
//		this.waitBeforeRebootLoop = waitBeforeRebootLoop;
//	}
//
//	public short getWatingForConnectionLoop() {
//		return watingForConnectionLoop;
//	}
//
//	public void setWatingForConnectionLoop(short watingForConnectionLoop) {
//		this.watingForConnectionLoop = watingForConnectionLoop;
//	}
//
//
//	public String getLastIP() {
//		return lastIP;
//	}
//
//	public void setLastIP(String lastIP) {
//		this.lastIP = lastIP;
//	}
	public void incrementWatingForConnectionLoop() {
		this.watingForConnectionLoop += 1;
	}
	public void incrementWaitBeforeRebootLoop() {
		this.waitBeforeRebootLoop += 1;
	}

	private static boolean pingHost(String host, int timeout) {
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(host, 80), timeout);
			return true;
		} catch (IOException e) {
			return false; // Either timeout or unreachable or failed DNS lookup.
		}
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
		log.info("Response Code : " + responseCode);

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
//		log.info("Joker returned: " + out);
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

	@Scheduled(fixedRate = 12000)
	public void checkAndFixRouterConnection() {
		System.out.print(".");
		boolean pingOk = pingHost("google.com", 6000);
		try {
			switch (status) {
			case STATUS_CONNECTION_OK:
				if (!pingOk) {
					log.info(dateFormat.format(new Date()) + ": Connection is ok. No Ping!");
					boolean pingAgain;
					for (int i = 0; i < 3; i++) {
						log.info("Please test connection before i reboot router. " + (((6 - getWaitBeforeRebootLoop()) * 12) - (3-i)*3)
								+ " seconds until launch.");
						soundClipTest(SOUND_DOWN);
						pingAgain = pingHost("google.com", 3000);
						Thread.sleep(3000);
						if (pingAgain) {
							log.info("Connection is back!");
							setWaitBeforeRebootLoop((short)0);
							return;
						}
					}
					incrementWaitBeforeRebootLoop();
					if( getWaitBeforeRebootLoop()>6 ){
						log.info("Reboot now!");
						Runtime.getRuntime()
								.exec("C:\\Programme\\AutoIt3\\AutoIt3.exe C:\\MyAutoItMacros\\router\\reboot.au3");
						setStatus(STATUS_CONNECTION_LOST);
						setWaitBeforeRebootLoop((short)0);
						setWatingForConnectionLoop((short)0);
					}
					
				}
				
				break;
			case STATUS_CONNECTION_LOST:
				if (pingOk) {
					status = STATUS_CONNECTION_OK;
					log.info(dateFormat.format(new Date()) + ": Connection is out. Ping ok!");
					status = STATUS_CONNECTION_OK;
					String ip = null;
			// This is something we do to get the node server going as it seems he needs this to get connected from the net. Not sure though!
					connectUrl("http://abbaslearning.royasoftware.com");
					for (int i = 0; i < 9; i++) {
						try {
							ip = connectUrl("http://www.icanhazip.com/");
							log.info("Ip = "+ip);
						} catch (Exception e) {
							log.info("Could not connect to get public ip! number of tries: "+i);
//							e.printStackTrace();
						}
						if( ip != null){
							break;
						}
						Thread.sleep(1000);
					}
					if (getLastIP() != null && !getLastIP().equals(ip)) {
						log.info("Different ips, send domain mapping");
						log.info("Joker response " + sendJokerDomainMappingUpdate());
					} else if (getLastIP() != null && getLastIP().equals(ip)) {
						log.info("Same ips, no domain mapping update");
					}
					setLastIP(ip);					
				}else{
					incrementWatingForConnectionLoop();
				}
				if(getWatingForConnectionLoop()>15)
					setWatingForConnectionLoop((short)0);
					setWaitBeforeRebootLoop((short)0);
					setStatus(STATUS_CONNECTION_OK);
				break;
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

	@Scheduled(fixedRate = 1400000)
	public void pingHerokuConnection() {
		soundClipTest(SOUND_HEROKU_PING);
		Calendar rightNow = Calendar.getInstance();
		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		log.info("Actual hour: " + hour);
		if (hour < 7 || hour > 22){
			log.info("No heroku pinging");
			return;
		}
			
		for (int i = 0; i < 5; i++) {
			try {
				// boolean ping = false;
				int rdm = new Random().nextInt(60);
				log.info("Sleeping random seconds before heroku call: " + rdm);
				Thread.sleep(rdm * 1000);
				log.info("Ping heroku server." + (i+1) + " iteration");
				// ping = pingHost("rlearn.herokuapp.com", 5000);
				String out = connectUrl("http://rlearn.herokuapp.com/bundle.js");
				if( out != null )
					log.info("heroku is alive!");
				else
					log.info("heroku has a problem! i got no output from connection");
				// connectUrl("http://abbaslearning.royasoftware.com");
				Thread.sleep(10000);
				// if( ping ){
				return;
				// }
			} catch (Exception e) {
				log.error("heroku not alive: "+e.getMessage());
//				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			DOMConfigurator.configure("log4j.xml");
			RouterMonitor rm = new RouterMonitor();
			// rm.soundClipTest(SOUND_HEROKU_PING);
			// rm.connectUrl("http://abbaslearning.royasoftware.com");
//			log.info("heroku response = " + rm.connectUrl("http://rlearn.herokuapp.com/bundle.js"));
//			rm.connectUrl("http://abbaslearning.royasoftware.com");
			Calendar rightNow = Calendar.getInstance();
			int hour = rightNow.get(Calendar.HOUR_OF_DAY);
			log.info("Actual hour: " + hour);

			// Thread.sleep(3000);
			// rm.sendJokerDomainMappingUpdate();
			// rm.nextIteration();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}