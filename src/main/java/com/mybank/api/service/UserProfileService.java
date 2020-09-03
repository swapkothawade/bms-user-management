
package com.mybank.api.service;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mybank.api.dao.UserProfileDao;
import com.mybank.api.domain.AccountDetails;
import com.mybank.api.domain.UserProfile;
import com.mybank.api.exception.CustomException;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

@Service
public class UserProfileService {

	@Autowired
	private UserProfileDao userProfileDao;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private EurekaClient eurekaClient;

	private String accountserviceid = "account-service";
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
/**
 * When User Registers, create account for user. Call Account service for that purpose.
 * @param userProfile
 * @return
 */
	public ResponseEntity<String> registerUser(UserProfile userProfile) {
		try {

			Application application = eurekaClient.getApplication(accountserviceid);
			InstanceInfo instanceInfo = application.getInstances().get(0);
			String url = "http://" + instanceInfo.getIPAddr() + ":" + instanceInfo.getPort() + "/api"
					+ "/restricted/account";
			LOGGER.info("URL {} " , url);
			System.out.println("URL" + url);
			userProfileDao.addUser(userProfile);
			AccountDetails accountDetails = getAccountDetailsInJsonFormat(userProfile);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
			HttpEntity<AccountDetails> request = new HttpEntity<>(accountDetails,headers);
			LOGGER.info("Sending request to account service  ");
			
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
			LOGGER.info("Response  {} " , response.getBody());
			return response;

		} catch (Exception ex) {
			throw new CustomException(ex.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}

	private AccountDetails getAccountDetailsInJsonFormat(UserProfile profile) {
		LOGGER.info("Transforming profile to Account details for user {} " , profile.getFirstName());
		AccountDetails accountDetails = new AccountDetails();
		accountDetails.setAccountType(profile.getAccountType());
		accountDetails.setEmail(profile.getEmail());
		accountDetails.setOpeningDate(LocalDate.now());
		LOGGER.info("Transformation done for user {} " , profile.getFirstName());
		return accountDetails;
	}

	public UserProfile findUserProfile(String email) {

		return userProfileDao.getUserProfileByEmail(email);
	}

	public boolean updateUserProfile(UserProfile userProfile) {
		return userProfileDao.updateProfile(userProfile);
	}
}
