package sample.cluster.stats;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import akka.actor.AbstractActor;

public class StatsWorker extends AbstractActor {
  static Logger logger = LogManager.getLogger(StatsWorker.class.getName());


  Map<String, Integer> cache = new HashMap<String, Integer>();

  public StatsWorker() {
	  logger.info("###########################################StatsWorker constructor "+new Random().nextInt());
	}
  
  @Override
  public Receive createReceive() {
    return receiveBuilder()
      .match(String.class, word -> {
        Integer length = cache.get(word);
        if (length == null) {
          length = word.length();
          cache.put(word, length);
        }
        logger.info("Routee "+this.getSelf().path()+", length of "+word+" is "+length);
//        System.out.println("length 2 = "+length);
        sender().tell(length, self());
      })
      .build();
  }
}
