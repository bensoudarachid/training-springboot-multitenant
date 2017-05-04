package com.royasoftware;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

//import org.apache.log4j.Logger;
//import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.royasoftware.script.ScriptHelper;

//import com.royasoftware.tools.SSHClient;

@lombok.Getter
@lombok.Setter

@Component
public class RouterMonitor {
	final private static short STATUS_CONNECTION_OK = 0;
	final private static short STATUS_CONNECTION_LOST = 1;
	final private static short WAIT_FOR_PING_MAX_ITERATIONS = 14;
//	final private static String SOUND_DOWN = "E:\\Samples\\FreeSound\\FX\\Powerdown2.wav";
	final private static String SOUND_DOWN = "E:\\Samples\\SONY LOOPS & SAMPLES LIBRARIES SCRATCH TACTICS by DJ PUZZLE\\BPM Tactics\\100 BPM\\100 BPM Tactic 093_2.wav";	
	final private static String SOUND_HEROKU_PING = "E:\\Samples\\Hip Hop 3\\Scratches\\hit me b2.wav";

	// private short status = STATUS_CONNECTION_OK;
	private short waitBeforeRebootLoop = 0;
	private short watingForConnectionLoop = 0;
	private boolean updateDomainToIpMapppingOk;
	private int connectionCheckLoop = 0;
	// private String lastIP = null;
//	private static final Logger logger = Logger.getLogger(RouterMonitor.class);
	private static final Logger logger = LoggerFactory.getLogger(RouterMonitor.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	String host;

	// String version = null;

//	SSHClient sshClient = null;

	public RouterMonitor(){}
	@Autowired
	public RouterMonitor(@Value("${app.router.host}") String host, @Value("${app.router.login}") String login,
			@Value("${app.router.passwd}") String passwd) throws Exception {
		setHost(host);
//		sshClient = new SSHClient(host, login, passwd);
		logger.info("calling updateDomainToIpMappping one tine on router monitor init");
		setUpdateDomainToIpMapppingOk(updateDomainToIpMappping());
//    	logger.info("--------> RUN AUTOIT F10");
    	ScriptHelper.run(ScriptHelper.RUN_WEB_APP);
	}

	public void reconnect() {
		logger.info("reconnect()");
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
			logger.info("reconnect() done");
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
		logger.debug("getPublicIp()");
		String ip = null;
		try {
			for (int i = 0; i < 30; i++) {
				try {
					ip = connectUrl("http://www.icanhazip.com/");
					// log.info("Ip = "+ip);
					return ip;
				} catch (Exception e) {
					logger.error("Could not connect to get public ip! number of tries: " + i);
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
		// logger.info("pingHost()");
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(host, 80), timeout);
			return true;
		} catch (IOException e) {
			return false; // Either timeout or unreachable or failed DNS lookup.
		}
	}


	private String getActualIp() throws Exception {
		logger.debug("calling getActualIp()");
		String address = null;
		try {
			// InetAddress inetAddress =
			// java.net.InetAddress.getByName("mama.royasoftware.com");
			// address = inetAddress.getHostAddress();
			// logger.info("address=" + address);

			for (int i = 0; i < 20; i++) {
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

//	public String pppConnect() throws Exception {
//		return sshClient.execCmd("ppp config 0.8.35 1 up\n");
//	}
//
//	public String pppDisconnect() throws Exception {
//		return sshClient.execCmd("ppp config 0.8.35 1 down\n");
//	}
//
//	public String wanSetIspLogin() throws Exception {
//		return sshClient.execCmd("wan config 0.8.35 1 --username f_chahid --password f_chahid\n");
//	}

	private String connectUrl(String url) throws Exception {
		logger.debug("connectUrl "+url);

		ExecutorService executor = Executors.newSingleThreadExecutor();
		@SuppressWarnings("unchecked")
		Future<String> future = executor.submit(new Callable() {

			public String call() throws Exception {
				String USER_AGENT = "Mozilla/5.0";

				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();

				// optional default is GET
				con.setRequestMethod("GET");

				// add request header
				con.setRequestProperty("User-Agent", USER_AGENT);

				int responseCode = con.getResponseCode();
				if( responseCode == 401 )
					return "unauthorized";
				if (responseCode != 200)
					throw new Exception("Could not connect to url: " + url);
				logger.debug("Calling " + url + ". Response Code : " + responseCode);

				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				logger.debug("connectUrl() finished");
				// print result
				return response.toString();
			}
		});
		try {
			// System.out.println(future.get(10, TimeUnit.SECONDS));
			String returned = future.get(10, TimeUnit.SECONDS);
//			logger.info("returned="+returned); 
			return returned;
		} catch (Exception e) {
			System.err.println("connectUrl Exception");
		}
		executor.shutdownNow();
		return null;
	}
	private String connectHttpsUrl(String url) throws Exception {
		logger.debug("connectUrl "+url);

		ExecutorService executor = Executors.newSingleThreadExecutor();
		@SuppressWarnings("unchecked")
		Future<String> future = executor.submit(new Callable() {

			public String call() throws Exception {
				String USER_AGENT = "Mozilla/5.0";

				URL obj = new URL(url);
				HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
//				con.setHostnameVerifier(new HostnameVerifier() {
//				    public boolean verify(String hostname, SSLSession session) {
//				      return true;
//				    }
//				});
				
				// optional default is GET
				con.setRequestMethod("GET");

				// add request header
				con.setRequestProperty("User-Agent", USER_AGENT);

				int responseCode = con.getResponseCode();
				if( responseCode == 401 )
					return "unauthorized";
				if (responseCode != 200)
					throw new Exception("Could not connect to url: " + url);
				logger.debug("Calling " + url + ". Response Code : " + responseCode);

				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				logger.debug("connectUrl() finished");
				// print result
				return response.toString();
			}
		});
		try {
			// System.out.println(future.get(10, TimeUnit.SECONDS));
			String returned = future.get(10, TimeUnit.SECONDS);
//			logger.info("returned="+returned); 
			return returned;
		} catch (Exception e) {
			logger.error("connectUrl Exception");
			e.printStackTrace();
		}
		executor.shutdownNow();
		return null;
	}

	private String sendJokerDomainMappingUpdate() throws Exception {
		String out = connectHttpsUrl(
		"https://svc.joker.com/nic/update?username=8508eb847ea580bc&password=b592711a66266d6f&hostname=*.royasoftware.com");
		Thread.sleep(9000);
		connectHttpsUrl(
		"https://svc.joker.com/nic/update?username=8508eb847ea580bc&password=b592711a66266d6f&hostname=royasoftware.com");
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
		} catch (Exception e) {
			logger.info("error playing sound:"+e.getMessage());
		}
	}

	private boolean updateDomainToIpMappping() {
		try {
			String ip = getPublicIp();
			logger.debug("public ip = " + ip);
			String actualIP = null;
			if( ip != null )
				actualIP = getActualIp();
			if (ip == null || actualIP == null) {
				logger.info("one of the ips is null. no update");
				return false;
			}
			if (!actualIP.equals(ip)) {
				logger.info("Different ips, actual ip = " + actualIP + ". Call domain mapping update");
				logger.debug("Joker response " + sendJokerDomainMappingUpdate());
				return true;
			} else if (actualIP.equals(ip)) {
				logger.info("Same ips, no domain mapping update");
				return true;
			}
		} catch (Exception e) {
			logger.info("No domain mapping update becase of exception:  " + e.getMessage());
			return false;
		}
		return false;
	}

//	private static  String c = "caca";
//	private static  String l = "lala";

//	private static boolean test() {
//		// logger.info("pingHost()");
//		String v = "hahuu";
//		try{
////			logger.info("just a tester assi c. "+c);
//			logger.info("just a tester assi h. "+v);
////			logger.info("just a test assi. ");
//			return true;
//		} catch (Exception e) {
//			return false; // Either timeout or unreachable or failed DNS lookup.
//		}
//	}

	@Scheduled(fixedDelay = 18000)
	public void checkAndFixRouterConnection() {
		try {
			boolean jokerUpdated = true;
			short pingRouter = 0;
			boolean pingOk = pingHost("google.com", 6000);
			short status = STATUS_CONNECTION_OK;
			// if( !pingOk )
			// Thread.sleep(3000);
			
//			System.out.print(".");
//			logger.info("getConnectionCheckLoop()="+getConnectionCheckLoop());
			if(getConnectionCheckLoop() > 20){
				setConnectionCheckLoop(0);
				jokerUpdated = false;		
			}else
				setConnectionCheckLoop(getConnectionCheckLoop()+1);
//			test();
			
			if (pingOk && !isUpdateDomainToIpMapppingOk())
				setUpdateDomainToIpMapppingOk(updateDomainToIpMappping());
			while (!pingOk || !jokerUpdated) {
				Thread.sleep(3000);
				setConnectionCheckLoop(0);
				switch (status) {
				case STATUS_CONNECTION_OK:
						
					for (int i = 0; i < WAIT_FOR_PING_MAX_ITERATIONS; i++) {
						pingOk = pingHost("google.com", 6000);
						if (pingOk) {
							logger.debug("Ping is ok.");
							setUpdateDomainToIpMapppingOk(updateDomainToIpMappping());
							jokerUpdated = true;
							break;
						}
//						logger.info("Iterations until reboot " + (WAIT_FOR_PING_MAX_ITERATIONS - i));
						soundClipTest(SOUND_DOWN);
						Thread.sleep(3000);
					}
					if (!pingOk) {
						status = STATUS_CONNECTION_LOST;
						jokerUpdated = false;
						logger.info("Reboot now deactivated!");
//						reconnect();
					}
					break;
				case STATUS_CONNECTION_LOST:
					System.out.print("-");
					pingOk = pingHost("google.com", 6000);
					pingRouter +=  pingHost("192.168.1.1", 6000)?1:0;

//					logger.info("pingRouter="+pingRouter);

					if ( pingRouter > 25){
						status = STATUS_CONNECTION_OK;
						pingRouter = 0;
						break;
					}
					if (pingOk ) {
						status = STATUS_CONNECTION_OK;
						logger.info("Before call http://abbaslearning.royasoftware.com ");
						connectUrl("http://abbaslearning.royasoftware.com");
						logger.info("After call http://abbaslearning.royasoftware.com ");
						setUpdateDomainToIpMapppingOk(updateDomainToIpMappping());
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


	@Scheduled(fixedDelay = 1400000, initialDelay = 1200000)
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
				int rdm = new Random().nextInt(180);
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
//			DOMConfigurator.configure("log4j.xml");
			
//			RouterMonitor rm = new RouterMonitor("192.168.1.1", "admin", "royaZoft");
			RouterMonitor rm = new RouterMonitor();
//			rm.reconnect();
			rm.updateDomainToIpMappping();
//			logger.info("connect router "+rm.pingHost("192.168.1.1", 6000)); 
			
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
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

