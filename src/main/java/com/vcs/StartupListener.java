package com.vcs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.vcs.service.VersionService;
/*
 * This class acts as a listener
 * */
@Component
public class StartupListener {

	@Autowired VersionService versionService;

	//This is the listener which captures the event when application is ready to use
	@EventListener(ApplicationReadyEvent.class)
	public void afterStartUp() {
		System.out.println("I am PK CHAND");
		versionService.saveVersionAndBuild(); //This method is called to manage the version and build of the application
	}

}
