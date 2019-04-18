package com.project.set_up.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.project.set_up.component.Builds;
import com.project.set_up.component.Versions;
import com.project.set_up.config.StartUpInitializer;
import com.project.set_up.dto.LatestVersionResponse;
import com.project.set_up.repository.BuildsRepository;
import com.project.set_up.repository.VersionsRepository;

@Service
public class VersionsService {

    private static final Logger logger = LogManager.getLogger(VersionsService.class);

    @Autowired
    VersionsRepository versionsRepository;
    @Autowired
    BuildsRepository buildsRepository;
    @Autowired
    StartUpInitializer startUpInitializer;

    /**
     * Method to get Latest version details
     * 
     * @return version details
     */
    public LatestVersionResponse getLatestVersion() {
	LatestVersionResponse latestVersionResponse = new LatestVersionResponse();
//	List<Versions> version = versionsRepository.findAll(Sort.by(Direction.DESC, "versionNumber"));
//	if (CollectionUtils.isEmpty(version)) {
//	   logger.info("There is no version released yet!!!");
//	    return null;}
//	Versions versions = version.get(0);
//	latestVersionResponse.setVersionNumber(versions.getVersionNumber());
//	latestVersionResponse.setReleaseDate(versions.getReleaseDate());
//	latestVersionResponse.setUpdatedDate(versions.getUpdatedDate());
//	latestVersionResponse.setVersionNote(versions.getNotes());
//	List<String> buildInfo = new ArrayList<>();
//	List<Builds> builds = buildsRepository.findAllByVersionNumber(versions.getVersionNumber(),
//		Sort.by(Direction.DESC, "version.buildNumber"));
//
//	builds.forEach(action -> {
//	    if(!StringUtils.isEmpty(action.getNotes())) {
//	    buildInfo.add(action.getNotes());}
//	    });
//	latestVersionResponse.setBuildInfo(buildInfo);
	return latestVersionResponse;
    }

    /**
     * have to find better Startup method that needs to run after all the bean
     * initialize One more method is required to read files from csv an to execute
     * the query
     */
    @EventListener(ApplicationReadyEvent.class)
    public void handleContextRefresh() {
	    startUpInitializer.versionInitialize();
    }
}
