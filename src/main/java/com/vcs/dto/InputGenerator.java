package com.vcs.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/*
 * This call is used to populate version and its build details, to be used as input while application starts
 */
public class InputGenerator {

	public static List<VersionDTO> populateInputVersions(){

		List<VersionDTO> versions = new ArrayList<>();

		VersionDTO version1 = new VersionDTO();
		version1.setVersionNumber("2.0.3");
		version1.setReleaseNotes("build 1 release");
		
		version1.setBuilds(Arrays.asList(
				createBuilsForVersion(10l, "some note1", false)
				));

		VersionDTO version2 = new VersionDTO();
		version2.setVersionNumber("2.0.4");
		version2.setReleaseNotes("update for version 2.0.1");
		
		version2.setBuilds(Arrays.asList(
				createBuilsForVersion(1l, "some note 16", false),
				createBuilsForVersion(2l, "some note 17", false)
				));
		
		versions.add(version1);
		//versions.add(version2);
		return versions;
	}
	
	private static BuildDTO createBuilsForVersion(Long buildNumber, String releaseNote, Boolean dbUpdated) {
		BuildDTO buildDto = new BuildDTO();
		buildDto.setBuildNumber(buildNumber);
		buildDto.setNotes(releaseNote);
		buildDto.setIsDbUpdated(dbUpdated);
		return buildDto;
	}

}
