package com.suite.core;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/core")
public class CoreResource {

	@GetMapping
	public ResponseEntity<?> test() {
		return new ResponseEntity<>("OK!", HttpStatus.OK);
	}

}