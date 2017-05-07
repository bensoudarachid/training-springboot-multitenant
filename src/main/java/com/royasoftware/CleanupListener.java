package com.royasoftware;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.batik.util.CleanerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

public class CleanupListener implements ServletContextListener{
	private static Logger logger = LoggerFactory.getLogger(CleanupListener.class);

	@Autowired
    private ApplicationContext appContext;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("!!!!!----going down baby-----!!!!!");
//		forceThreadStop(CleanerThread.currentThread());
		initiateShutdown(0);
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

    public void initiateShutdown(int returnCode){
        SpringApplication.exit(appContext, () -> returnCode);
    }
    
	public static void forceThreadStop(Thread thread) {

		  try {
		    thread.join(30000); // Give the Thread 2 seconds to finish executing
		  } catch (InterruptedException e) {
		    // join failed
		  }
		  logger.info("!!!!!----going down now! baby-----!!!!!");
		  thread.interrupt(); // Make Thread stop waiting in sleep(), wait() or join()
		  try {
			    thread.join(10000); // Give the Thread 2 seconds to finish executing
			  } catch (InterruptedException e) {
			    // join failed
			  }

		  // If still not done, kill it
		  if (thread.isAlive())
		    thread.stop();
	}
	
//	private static void removeShutdownHook(Class clazz, String field) {
//		  // Note that loading the class may add the hook if not yet present...
//		  try {
//		    // Get the hook
//		    final Field cleanupThreadField = clazz.getDeclaredField(field);
//		    cleanupThreadField.setAccessible(true);
//		    Thread cleanupThread = (Thread) cleanupThreadField.get(null);
//		 
//		    if(cleanupThread != null) {
//		      // Remove hook to avoid PermGen leak
//		      System.out.println("  Removing " + cleanupThreadField + " shutdown hook");
//		      Runtime.getRuntime().removeShutdownHook(cleanupThread);
//		       
//		      // Run cleanup immediately
//		      System.out.println("  Running " + cleanupThreadField + " shutdown hook");
//		      cleanupThread.start();
//		      cleanupThread.join(60 * 1000); // Wait up to 1 minute for thread to run
//		      if(cleanupThread.isAlive())
//		        System.out.println("STILL RUNNING!!!");
//		      else
//		        System.out.println("Done");
//		    }
//		    else
//		      System.out.println("  No " + cleanupThreadField + " shutdown hook");
//		     
//		  }
//		  catch (NoSuchFieldException ex) {
//		    System.err.println("*** " + clazz.getName() + '.' + field +
//		      " not found; has JAR been updated??? ***");
//		    ex.printStackTrace();
//		  }
//		  catch(Exception ex) {
//		    System.err.println("Unable to unregister " + clazz.getName() + '.' + field);
//		    ex.printStackTrace();
//		  }   
//		}
}
