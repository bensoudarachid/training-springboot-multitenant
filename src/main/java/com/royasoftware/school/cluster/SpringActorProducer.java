package com.royasoftware.school.cluster;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.actor.IndirectActorProducer;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.royasoftware.school.TenantContext;

/**
 * An actor producer that lets Spring create the Actor instances.
 */
public class SpringActorProducer implements IndirectActorProducer {
	private static Logger logger = LoggerFactory.getLogger(SpringActorProducer.class);
  final ApplicationContext applicationContext;
  final String actorBeanName;
  final HashMap<String, Object> origThreadHashMap;

  public SpringActorProducer(ApplicationContext applicationContext, String actorBeanName, HashMap<String, Object> originalThreadHashMap) {
    this.applicationContext = applicationContext;
    this.actorBeanName = actorBeanName;
    origThreadHashMap = originalThreadHashMap;
  }

  @Override
  public Actor produce() {
//	  Actor ac =  (Actor) applicationContext.getBean(actorBeanName);
//	  ThreadLocal<HashMap<String, Object>> th = new ThreadLocal<HashMap<String, Object>>() {
//	        @Override
//	        protected HashMap<String, Object> initialValue() {
//	            return new HashMap<>();
//	        }
//	    };	
//	  th.get().putAll(origThreadHashMap);
	   
	  TenantContext.setTenantContextThreadLocalMap(origThreadHashMap);
	  Actor ac =  (Actor) applicationContext.getBean(actorBeanName);
	  
	  
//	  logger.debug("SpringActorProducer " + actorBeanName);
	  return ac;
  }

  @Override
  public Class<? extends Actor> actorClass() {
    return (Class<? extends Actor>) applicationContext.getType(actorBeanName);
  }
}
