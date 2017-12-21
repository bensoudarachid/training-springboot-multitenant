package sample.cluster.stats;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class StatsSampleOneMasterClientMain {

  public static void main(String[] args) {
    // note that client is not a compute node, role not defined
      Config config = ConfigFactory.parseString(
              "akka.remote.netty.tcp.port=" + args[0]).withFallback(
              ConfigFactory.load());
      ActorSystem system = ActorSystem.create("ClusterSystem", config);

//      ActorSystem system = ActorSystem.create("ClusterSystem",
//        ConfigFactory.load("stats2"));
    system.actorOf(Props.create(StatsSampleClient.class, "/user/statsServiceProxy"),
        "client");

  }

}
