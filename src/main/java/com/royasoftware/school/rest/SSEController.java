package com.royasoftware.school.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.royasoftware.school.exception.ValidationException;
import com.royasoftware.school.model.MemoryInfo;

@Controller
public class SSEController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

//	private Map<Long, SseEmitter> emitters = new ConcurrentHashMap<Long, SseEmitter>();
	BiMap<Long, SseEmitter> emitters = Maps.synchronizedBiMap(HashBiMap.<Long, SseEmitter>create());
	ConcurrentLinkedQueue<Long> lastKeys = new ConcurrentLinkedQueue<>();

	@GetMapping("/api/memory")
	public SseEmitter handle(@RequestParam("rdm") Long rdm) { // HttpServletResponse
																// response
		logger.info("rdm=" + rdm);
		// response.setHeader("Cache-Control", "no-store");
		if (lastKeys.contains(rdm))
			return null;
		else
			lastKeys.add(rdm);
		for (int i = 0; lastKeys.size() > 10 && i < lastKeys.size() - 10; i++)
			lastKeys.poll();

		if (emitters.get(rdm) != null) {
			emitters.get(rdm).complete();
			emitters.remove(rdm);
		}
		SseEmitter emitter = new SseEmitter();
		emitter.onTimeout(() -> {
			timeout(emitter);
			complete(emitter);
		});
		emitter.onCompletion(() -> complete(emitter));
		logger.info("Add emitter : " + emitter);
		emitters.put(rdm, emitter);
		logger.info("emitters remove key from lastKeys ="+emitters.inverse().get(emitter));
		return emitter;

	}

	private void complete(SseEmitter emitter) {
		logger.info("emitter completed " + emitter);
		emitters.remove(emitter);
//		logger.info("complete. lastKeys size before =" + lastKeys.size());
//		lastKeys.remove(emitters.inverse().get(emitter));
//		logger.info("complete. lastKeys size after=" + lastKeys.size());
	}

	private void timeout(SseEmitter emitter) {
		// logger.info("emitter timeout");
		emitters.remove(emitter);
	}

	@EventListener
	public void onMemoryInfo(MemoryInfo memoryInfo) {
		List<Long> deadEmitters = new ArrayList<>();
		logger.info("List emitter ********** ");
		this.emitters.keySet().forEach(emitterKey -> {
			logger.info("" + this.emitters.get(emitterKey));
			try {
				if (this.emitters.get(emitterKey) == null)
					this.emitters.remove(emitterKey);
				else
					this.emitters.get(emitterKey).send(memoryInfo);
				// close connnection, browser automatically reconnects
				// emitter.complete();

				// SseEventBuilder builder =
				// SseEmitter.event().name("second").data("1");
				// SseEventBuilder builder =
				// SseEmitter.event().reconnectTime(10_000L).data(memoryInfo).id("1");
				// emitter.send(builder);
			} catch (Exception e) {
				logger.info("Add dead emitter " + this.emitters.get(emitterKey));
				deadEmitters.add(emitterKey);
			}
		});
		logger.info("List emitter ********** ");
		if (deadEmitters.size() > 0) {
			logger.info("dead Emitters size=" + deadEmitters.size());
			logger.info("Emitters before removing = " + emitters.size());
			deadEmitters.forEach(key -> this.emitters.remove(key));
			logger.info("Emitters after removing = " + emitters.size());
		}
	}

	@ExceptionHandler({ AsyncRequestTimeoutException.class })
	@ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
	public final @ResponseBody ErrorInfo handleAsyncRequestTimeoutException(HttpServletRequest request, Exception ex) {
		lastKeys = new ConcurrentLinkedQueue<>();
		logger.info("AsyncRequestTimeoutException. lastKeys size=" + lastKeys.size());
//		ex.printStackTrace();
		Throwable e = ex;
		ErrorInfo response = new ErrorInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setErrorDescription(e.getMessage());
		response.setError(e.getClass().getName());
		response.setValidation(((ValidationException) e).getValidationErrorMap());
		return response;
	}
}
