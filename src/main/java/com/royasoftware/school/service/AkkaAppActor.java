package com.royasoftware.school.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

//import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.royasoftware.school.TenantContext;
import com.royasoftware.school.model.Training;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.AbstractActor.Receive;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.routing.FromConfig;

//@Named("UserRegistryActor")
public class AkkaAppActor extends AbstractActor {
	private static Logger logger = LoggerFactory.getLogger(AkkaAppActor.class);

//	ThreadLocal th = new ThreadLocal<>();

	public AkkaAppActor() {
	}

//	static Props props() {
//		return Props.create(AkkaAppActor.class);
//	}

	protected void setTenantContext(AkkaAppMsg msg) {
		HashMap<String, Object> tenantTL = msg.getOriginalThreadHashMap();
//		logger.info("Propagate Tenant context {} to remote actor", tenantTL.get("tenant"));
		TenantContext.setTenantContextThreadLocalMap(msg.getOriginalThreadHashMap());
	}
	@Override
	public Receive createReceive() {
		
		return null;
	}
	public static class Message implements AkkaAppMsg{
		private final String message;

		public Message(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}
}
