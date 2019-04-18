package com.project.set_up.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.FilerException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.project.set_up.component.Versions;
import com.project.set_up.config.StartUpInitializer;
import com.project.set_up.constants.Constants;
import com.project.set_up.dto.BuildInfoFromFile;
import com.project.set_up.dto.VersionInfoFromFile;
import com.project.set_up.exception.FileReadException;

@Service
public class FileUtils {

    private static final Logger logger = LogManager.getLogger(StartUpInitializer.class);

    /**
     *
     * Read a build info file and convert it to a DTO (BuildInfoFromFile) by
     * versionId and versionNumber
     * 
     * @param versionId     - Unique Identify version
     * @param versionNumber - Version number for which build is created
     * @return List DTO (BuildInfoFromFile)
     * @throws FilerException -If there is any exception while reading a file 
     */
    public List<BuildInfoFromFile> readFileBuildInfoByVersionNumber( Versions version)
	    throws FileReadException {
	ClassLoader classLoader = getClass().getClassLoader();
	List<BuildInfoFromFile> buildInfoFromFiles = new ArrayList<>();

	// Loading Build info file
	URL resource = classLoader.getResource(Constants.BUILD_INFO_FILE_NAME + version.getVersionNumber() + Constants.CSV);
	if (resource != null) {
	    File file = new File(resource.getFile());

	    // Converting file to List of string
	    List<String> lines = convertFileToString(file);
	    for (String line : lines) {
		BuildInfoFromFile buildInfoFromFile = new BuildInfoFromFile();

		// Use comma as separator
		String[] buildInfo = line.split(",");
		buildInfoFromFile.setBuildNumber(Integer.valueOf(buildInfo[0]));
		buildInfoFromFile.setReleaseDate(Date.valueOf(buildInfo[1]));
		buildInfoFromFile.setNotes(buildInfo[2]);

		buildInfoFromFiles.add(buildInfoFromFile);
	    }
	} else {
	    logger.info(Constants.BUILD_INFO_FILE_NAME + version.getVersionNumber() + Constants.CSV + " file doesn't exist");
	}
	return buildInfoFromFiles;

    }

    /**
     * Read a version info file and convert it to a DTO (VersionInfoFromFile)
     * 
     * @return List DTO (VersionInfoFromFile)
     * @throws FilerException - If there is any exception while reading a file 
     */
    public List<VersionInfoFromFile> readVersionFile() throws FileReadException {
	ClassLoader classLoader = getClass().getClassLoader();
	List<VersionInfoFromFile> versionInfoFromFiles = new ArrayList<>();

	// Loading Version info file
	URL resource = classLoader.getResource(Constants.VERSION_FILE_NAME);
	if (resource != null) {
	    File file = new File(resource.getFile());

	    // Converting file to List of string
	    List<String> lines = convertFileToString(file);
	    for (String line : lines) {
		VersionInfoFromFile versionInfoFromFile = new VersionInfoFromFile();

		// Use comma as separator
		String[] versionInfo = line.split(",");
		versionInfoFromFile.setVersionNumber(versionInfo[0]);
		versionInfoFromFile.setReleaseDate(Date.valueOf(versionInfo[1]));
		versionInfoFromFile.setNotes(versionInfo[2]);
		versionInfoFromFile.setTotalbuilds(Integer.valueOf(versionInfo[3]));

		versionInfoFromFiles.add(versionInfoFromFile);
	    }
	} else {
	    logger.info(Constants.VERSION_FILE_NAME + " file doesn't exist");
	}
	return versionInfoFromFiles;
    }

    @Deprecated
    public List<String> readMySqlFile(String fileName) throws FileReadException {
	ClassLoader classLoader = getClass().getClassLoader();

	// Loading Version info file
	URL resource = classLoader.getResource(fileName);
	List<String> lines = new ArrayList<>();
	if (resource != null) {
	    File file = new File(resource.getFile());

	    // Converting file to List of string
	    lines = convertFileToString(file);
	} else {
	    logger.info("SQL query " + fileName + " file doesn't exist");
	}
	return lines;
    }

    /**
     * Convert file data into List of Strings
     * 
     * @param file - File that is to be read line by line
     * @return - List of String read from file
     * @throws FileReadException - If there is any exception while reading a file 
     */
    private List<String> convertFileToString(File file) throws FileReadException {
	List<String> lines = new ArrayList<>();
	String line = "";
	try (BufferedReader br = new BufferedReader(new FileReader(file))) {
	    while ((line = br.readLine()) != null) {
		lines.add(line);
	    }
	} catch (Exception e) {
	    logger.error("Error while operating on file " + file.getName() + "\n Error message :" + e.getMessage());
	    throw new FileReadException(
		    "Error while operating on file " + file.getName() + "\n Error message :" + e.getMessage());
	}
	return lines;
    }
}
