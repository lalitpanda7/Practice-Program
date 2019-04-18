package com.vcs.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class ResourceReader {

	@Autowired
	private ResourceLoader resourceLoader;
	private static final Logger LOGGER = LogManager.getLogger(ResourceReader.class);
	
	/*
	 * This method gives the data in a file present in resources/sql
	 * Typically file names are represented by versionNumber-buildNumber (ex: 2.0.0-build1.sql)
	 * @Param : String fileName
	 * */
	public String getData(String directory, String fileName) throws IOException {
		StringBuffer buff = new StringBuffer();
		try {
			String[] files = fileName.split("-");
			//files[0] is the folder name and files[1] is the file name
			Resource resource = resourceLoader.getResource("classpath:\\" + directory + "\\" + files[0] + "\\" + files[1]);
			
			InputStream in = resource.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				buff.append(line);
			}
			reader.close();
		
		}catch(Exception e) {
			System.out.println(e.getMessage());
			LOGGER.error(e.getMessage());
			throw e;
		}
		return buff.toString();
	}
	
	/*
	 * This method gives list of all files present in a particular folder in resources/sql
	 * Folders are typically named with the version number
	 * 
	 * @Param: String versionNumber 
	 * */
	
	public Set<String> listAllFiles(String directory, String versionNumber) {
		try {
			Resource resource = resourceLoader.getResource("classpath:\\" + directory + "\\" + versionNumber);
			return Stream.of(resource.getFile().list()).collect(Collectors.toSet());
		}catch(Exception e) {
			
		}
		return new HashSet<>();
	}
}
