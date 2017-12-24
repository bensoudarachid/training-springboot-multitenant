package sample.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.royasoftware.TenantContext;

import akka.actor.Extension;
import akka.actor.Props;

/**
 * An Akka Extension to provide access to Spring managed Actor Beans.
 */
@Component
public class SpringExtension implements Extension {
	private volatile ApplicationContext applicationContext;
	private static Logger logger = LoggerFactory.getLogger(SpringExtension.class);

	/**
	 * Used to initialize the Spring application context for the extension.
	 * 
	 * @param applicationContext
	 */
	public void initialize(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		logger.info("SpringExt init applicationContext=" + applicationContext);
	}

	/**
	 * Create a Props for the specified actorBeanName using the
	 * SpringActorProducer class.
	 *
	 * @param actorBeanName
	 *            The name of the actor bean to create Props for
	 * @return a Props that will create the named actor bean using Spring
	 */
	public Props props(String actorBeanName) {
//		logger.info("**************************************SpringExtension "+ TenantContext.getCurrentTenant());
		return Props.create(SpringActorProducer.class, applicationContext, actorBeanName, TenantContext.getTenantContextThreadLocal().get());
	}
}
