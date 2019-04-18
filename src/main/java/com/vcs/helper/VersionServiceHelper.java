package com.vcs.helper;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vcs.dto.BuildDTO;
import com.vcs.dto.VersionDTO;
import com.vcs.entity.BuildEntity;
import com.vcs.entity.VersionEntity;
import com.vcs.util.DBUtil;
import com.vcs.util.ResourceReader;

@Component
public class VersionServiceHelper {
	@Autowired DBUtil dbUtil;
	@Autowired ResourceReader reader;

	public void executeSqlFilesForBuild(Set<String> sqlFiles, BuildDTO buildDto, List<BuildEntity> buildsToBeSaved, 
			List<Long> buildNumbersForVersion, VersionEntity version, Boolean update) {
		
		if(!sqlFiles.isEmpty()) {
			
			sqlFiles.stream().sorted().forEach(sqlFile -> {
				if(update) {
					try {
						dbUtil.executeSqlFile(sqlFile);
					}catch(Exception e) {
						
						//update = false;
						
						Long currentBuild = getCurrentBuild(sqlFile);
						Long previousBuild = Long.valueOf(currentBuild.intValue()-1);
						//if check
						if(!buildsToBeSaved.stream().filter(build -> build.getBuildNumber().equals(previousBuild)).findFirst().isPresent()
								&& !buildNumbersForVersion.contains(previousBuild)) {
							BuildEntity buildEntity = new BuildEntity();
							buildEntity.setBuildNumber(previousBuild); //previous build number should be given
							buildEntity.setDbUpdateDate(Instant.now());
							buildEntity.setNotes(buildDto.getNotes());
							buildEntity.setReleaseDate(Instant.now());
							buildEntity.setUpdatedDate(Instant.now());
							buildEntity.setVersionId(version);
							
							buildsToBeSaved.add(buildEntity);
						}
					}
				}
			});
		}
	}
	
	private Long getCurrentBuild(String fileName) {
		String[] ss = fileName.split("-");
		String buildNumber = ss[1];
		String[] sss = buildNumber.split("\\.");
		return Long.parseLong(sss[0]);
	}
	
	public Set<String> populateSqlFileNamesForTheBuild(String versionNumber, Long buildNumber, List<Long> buildNumbersForVersion) {
		Set<String> sqlfileNames = new HashSet<String>();
		Long maxBuild = buildNumbersForVersion
				.stream()
				.max((c1, c2) -> c1.compareTo(c2))
				.orElseGet(() -> 0l); //getting the maximum build number, for a version currently persisted

		Long maxBuildInDto = buildNumber;//getting the maximum build number, for a version given in input
		
		//fetching all the sql files present for the corresponding version directory
		Set<String> allFilesPresentForVersion = reader.listAllFiles("sql", versionNumber);
		
		/*handling skipped builds.
		 * It starts checking from the LAST BUILD, if there is any sql file for the skipped builds
		 * DEFAULT build number is populated and matched with the file names present in the directory
		 * if any file names match it should be considered for execution
		 */
		for(int i = maxBuild.intValue() + 1; i <= maxBuildInDto.intValue(); i++) {
			String fileName =  i + ".sql";
			if(allFilesPresentForVersion.contains(fileName)) {
				sqlfileNames.add(versionNumber + "-" /* + "build" */ + i + ".sql");
			}
		}
		
		return sqlfileNames;
	}
	
	/*
	 * This method populates all the builds to be saved and populates sql files to be executed
	 * @Param: versionDTO - Input version and build details by developer
	 * @Param: buildNumbersForVersion - build numbers that are currently persisted
	 * @Param: sqlFileNames - list of sql files to be executed are populated here. FILE_NAME_FORMAT - versionNumber-buildNumber
	 * */
	public void populateSqlFileNamesForTheBuild(VersionDTO versionDTO, List<Long> buildNumbersForVersion, Set<String> sqlfileNames) {
		
		Long maxBuild = buildNumbersForVersion
				.stream()
				.max((c1, c2) -> c1.compareTo(c2))
				.orElseGet(() -> 0l); //getting the maximum build number, for a version currently persisted

		Long maxBuildInDto = versionDTO.getBuilds()
				.stream()
				.map(BuildDTO :: getBuildNumber)
				.max((c1, c2) -> c1.compareTo(c2))
				.orElseGet(() -> 0l); //getting the maximum build number, for a version given in input
		
		//fetching all the sql files present for the corresponding version directory
		Set<String> allFilesPresentForVersion = reader.listAllFiles("sql", versionDTO.getVersionNumber());
		
		/*handling skipped builds.
		 * It starts checking from the LAST BUILD, if there is any sql file for the skipped builds
		 * DEFAULT build number is populated and matched with the file names present in the directory
		 * if any file names match it should be considered for execution
		 */
		for(int i = maxBuild.intValue() + 1; i <= maxBuildInDto.intValue(); i++) {
			String fileName = "build" + i + ".sql";
			if(allFilesPresentForVersion.contains(fileName)) {
				sqlfileNames.add(versionDTO.getVersionNumber() + "-" + "build" + i + ".sql");
			}
		}
	}

}
