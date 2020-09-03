package com.mybank.api.service;

import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.mongodb.client.MongoClient;
import com.mybank.api.config.MongoDBConfiguration;
import com.mybank.api.domain.AccountDetails;
import com.mybank.api.domain.AccountType;
import com.mybank.api.domain.Address;
import com.mybank.api.domain.UserProfile;

@SpringBootTest(classes = { MongoDBConfiguration.class })
@EnableConfigurationProperties
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class UserProfileServiceTest {

	@Autowired
	MongoClient mongoClient;

	@Value("${spring.mongodb.database}")
	String databaseName;

	@Autowired
	private UserProfileService service;
	@Autowired
	private RestTemplate restTemplate;

	@Before
	public void setup() {
		this.service = new UserProfileService();
	}

	@Test
	public void registerUserTest() {
		AccountDetails accountDetails = getAccountDetailsInJsonFormat(getTempUserProfile());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "appplication/json");

		HttpEntity<AccountDetails> request = new HttpEntity<>(accountDetails, headers);

		ResponseEntity<String> response = restTemplate.exchange("http://localhost:8901/api/restricted/account",
				HttpMethod.POST, request, String.class);
		assertNotNull(response);
		System.out.println("Response " + response.getBody());
	}

	private UserProfile getTempUserProfile() {
		UserProfile profile = new UserProfile();
		profile.setFirstName("UserFirstName");
		profile.setLastName("UserLastName");
		profile.setEmail("user@gmail.com");
		profile.setPassword("Password123");
		profile.setPan("45765987809");
		Set<String> roles = new HashSet<>();
		roles.add("USER");
		profile.setRoles(roles);
		profile.setUserAddress(getUserAddress());
		profile.setAccountType(AccountType.SAVING);
		profile.setContactNo("6786786789");
		profile.setDob(LocalDate.of(2000, 11, 20));
		return profile;
	}

	private Address getUserAddress() {
		Address address = new Address();
		address.setStreet("169 Manhattan Avenue");
		address.setCity("Jersey City");
		address.setState("New Jersey");
		address.setCountry("USA");
		address.setZip(07307);
		return address;
	}

	private AccountDetails getAccountDetailsInJsonFormat(UserProfile profile) {
		AccountDetails accountDetails = new AccountDetails();
		accountDetails.setAccountType(profile.getAccountType());
		accountDetails.setEmail(profile.getEmail());
		return accountDetails;
	}

}
