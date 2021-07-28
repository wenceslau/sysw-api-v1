package com.suite.app.service;

import org.springframework.stereotype.Service;

import com.suite.security.cryptography.RSATransporter;
import com.suite.security.cryptography.SHACryptography;

/**
 */
@Service
public class EncodeService {

	private RSATransporter rsa;

	private SHACryptography sha;

	public RSATransporter getRsa() {

		if (rsa == null)
			rsa = new RSATransporter();

		return rsa;

	}

	public SHACryptography getSha() {

		if (sha == null)
			sha = new SHACryptography();

		return sha;

	}

}