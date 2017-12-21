package sample.http;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;

import java.util.*;

//import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.royasoftware.repository.TrainingDAO;
import com.royasoftware.repository.TrainingRepository;
import com.royasoftware.service.TrainingService;

//@Named("UserRegistryActor")
@Component("UserRegistryActor")
@Scope("prototype")
public class UserRegistryActor extends AbstractActor {
	private static Logger logger = LoggerFactory.getLogger(UserRegistryActor.class);

//	LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	// #user-case-classes
	public static class User {
		private final String name;
		private final int age;
		private final String countryOfResidence;

		public User() {
			this.name = "";
			this.countryOfResidence = "";
			this.age = 1;
		}

		public User(String name, int age, String countryOfResidence) {
			this.name = name;
			this.age = age;
			this.countryOfResidence = countryOfResidence;
		}

		public String getName() {
			return name;
		}

		public int getAge() {
			return age;
		}

		public String getCountryOfResidence() {
			return countryOfResidence;
		}
	}

	public static class Users {
		private final List<User> users;

		public Users() {
			this.users = new ArrayList<>();
		}

		public Users(List<User> users) {
			this.users = users;
		}

		public List<User> getUsers() {
			return users;
		}
	}
	// #user-case-classes

	static Props props() {
		return Props.create(UserRegistryActor.class);
	}

	private final List<User> users = new ArrayList<>();

//	@Autowired(required = false)
//	private TrainingDAO trainingDao;
	@Autowired(required = false)
	private TrainingService trainingSvc;
	
//	@Autowired(required = false)
//	private ApplicationContext ctx;
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(UserRegistryMessages.GetUsers.class, getUsers -> {
//					logger.info("ctx="+ctx); 
//					TrainingService trainingService = ctx.getBean(TrainingService.class); // ctx.getBean(TrainingService.class);
//					logger.info("trainingService="+trainingService); 
//					logger.info("trainingService.sayHello()="+trainingService.sayHello());
					logger.info("trainingService.sayHello()="+trainingSvc.findById(1l));
					getSender().tell(new Users(users), getSelf());
					}
						)
				.match(UserRegistryMessages.CreateUser.class, createUser -> {
					users.add(createUser.getUser());
					getSender().tell(new UserRegistryMessages.ActionPerformed(
							String.format("User %s created.", createUser.getUser().getName())), getSelf());
				}).match(UserRegistryMessages.GetUser.class, getUser -> {
					getSender().tell(
							users.stream().filter(user -> user.getName().equals(getUser.getName())).findFirst(),
							getSelf());
				}).match(UserRegistryMessages.DeleteUser.class, deleteUser -> {
					users.removeIf(user -> user.getName().equals(deleteUser.getName()));
					getSender().tell(new UserRegistryMessages.ActionPerformed(
							String.format("User %s deleted.", deleteUser.getName())), getSelf());

				}).matchAny(o -> logger.info("received unknown message")).build();
	}
}
