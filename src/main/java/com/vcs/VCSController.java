package com.vcs;

import java.io.IOException;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vcs.dto.VersionDTO;
import com.vcs.service.VersionService;

@RestController
@RequestMapping("/api")
public class VCSController {

	@Autowired VersionService versionService;
	
	@GetMapping("/version/{versionNumber}")
	public ResponseEntity<VersionDTO> getVersionAndBuild(@PathVariable("versionNumber") @NotBlank String versionNumber,
			@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) throws IOException {
		return ResponseEntity.ok(versionService.getVersion(versionNumber, pageNumber, pageSize));
	}
	
	@GetMapping("/version/latest")
	public ResponseEntity<VersionDTO> getLatestVersionAndBuild(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize) throws IOException {
		return ResponseEntity.ok(versionService.getLatestVersion(pageNumber, pageSize));
	}
}
