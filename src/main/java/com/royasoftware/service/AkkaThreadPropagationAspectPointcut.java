package com.royasoftware.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
@Aspect("perthis(within(com.royasoftware.service.*))")
public class AkkaThreadPropagationAspectPointcut {

//	@Before("execution(* com.royasoftware.service..find*(*)) && args(id)") //&& args(msg) && args(yourString,..)
//	public void findArgAdvice(Long id){ //AkkaAppMsg msg
//            System.out.println("*************************************Finder Argument : "+id);
//	}
	@Before("execution(* com.royasoftware.service..find*(..))") //&& args(msg) && args(yourString,..)
	public void findAdvice(){ //AkkaAppMsg msg
            System.out.println("************************************* general Finder ");
	}

		@Before("execution(* com.royasoftware.service..*Process(..))") //&& args(msg) && args(yourString,..)
		public void processorAdvice(){ //AkkaAppMsg msg
	            System.out.println("*************************************Actor call : ");
		}
		
}
