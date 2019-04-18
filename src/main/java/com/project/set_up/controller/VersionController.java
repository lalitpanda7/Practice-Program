
package com.project.set_up.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.set_up.dto.LatestVersionResponse;
import com.project.set_up.service.VersionsService;

@RestController
@RequestMapping(value = "/")
public class VersionController {

    @Autowired
    VersionsService versionsService;

    /**
     * Method to get details of latest version as well as the build details
     * 
     * @return
     */
    @GetMapping(value = "latest")
    public LatestVersionResponse getLatestVersion() {
	return versionsService.getLatestVersion();
    }

}
