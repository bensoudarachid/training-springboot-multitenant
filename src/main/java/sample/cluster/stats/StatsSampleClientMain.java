package sample.cluster.stats;

import akka.actor.ActorSystem;
import akka.actor.Props;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class StatsSampleClientMain {

  public static void main(String[] args) {
	  
	  
//      Config config = ConfigFactory
//              .parseString("akka.remote.netty.tcp.port=" + 2551)
//              .withFallback(
//                  ConfigFactory.parseString("akka.cluster.roles = [client]"))
//              .withFallback(ConfigFactory.load("stats1"));
//
//          ActorSystem system = ActorSystem.create("ClusterSystem", config);

//In order to get MemberUp Messages, it has to be a seed or a member of a seed? 	  
      Config config = ConfigFactory.parseString(
              "akka.remote.netty.tcp.port=" + args[0]).withFallback(
              ConfigFactory.load());
      ActorSystem system = ActorSystem.create("ClusterSystem", config);

//    ActorSystem system = ActorSystem.create("ClusterSystem",ConfigFactory.load());

    // note that client is not a compute node, role not defined
//    ActorSystem system = ActorSystem.create("ClusterSystem",ConfigFactory.load("stats1"));
    system.actorOf(Props.create(StatsSampleClient.class, "/user/statsService"),
        "client");
  }
}
