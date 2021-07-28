package com.suite.core.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suite.core.model.UserRequest;
import com.suite.core.repository.UserRquestRepository;

@Service
public class UserRequestService extends com.suite.app.service.UserRequestService {

	@Autowired
	private UserRquestRepository userRquestRepository;

	@Override
	public void saveRequest(LocalDateTime time, String addr, String host, String userAgent, String path, String url, String verb,
			String userRequest) {
		UserRequest uRequest = new UserRequest();

		uRequest.setDateRequest(time);
		uRequest.setAddress(addr);
		uRequest.setHost(host);
		uRequest.setUserAgent(userAgent);
		uRequest.setPath(path);
		uRequest.setUrl(url);
		uRequest.setVerb(verb);
		uRequest.setAppAgent(userRequest);

		userRquestRepository.save(uRequest);

	}

	@Override
	protected String formatTranslate(String key, Object... args) {
		return "";
	}

	public List<UserRequest> findAllByDateRequestGreaterThanEqualAndDateRequestLessThanEqual(LocalDateTime startDateRequest,
			LocalDateTime endDateRequest) {
		return userRquestRepository.findAllByDateRequestGreaterThanEqualAndDateRequestLessThanEqual(startDateRequest, endDateRequest);
	}

	public long countByDateRequestGreaterThanEqualAndDateRequestLessThanEqual(LocalDateTime startDateRequest, LocalDateTime endDateRequest) {
		return userRquestRepository.countByDateRequestGreaterThanEqualAndDateRequestLessThanEqual(startDateRequest, endDateRequest);
	}

}
