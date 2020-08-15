package com.mybank.api.service;

import com.mybank.api.dao.UserProfileDao;
import com.mybank.api.domain.UserProfile;
import com.mybank.api.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public UserProfile registerUser(UserProfile userProfile){
        try {
            return userProfileDao.addUser(userProfile) ? userProfile : null;
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    public UserProfile findUserProfile(String email) {

        return userProfileDao.getUserProfileByEmail(email);
    }

    public boolean updateUserProfile(UserProfile userProfile) {
            return userProfileDao.updateProfile(userProfile);
    }
}
