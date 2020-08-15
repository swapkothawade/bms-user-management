package com.mybank.api.service.serviceimpl;


import com.mybank.api.auth.User;
import com.mybank.api.dao.UserDao;
import com.mybank.api.dao.UserSessionDao;
import com.mybank.api.exception.CustomException;
import com.mybank.api.service.JwtTokenProvider;
import com.mybank.api.service.ILoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LoginService implements ILoginService
{
   // @Autowired
    //private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @Autowired
    private UserDao userDao;

    @Autowired
    private UserSessionDao userSessionDao;


    @Override
    public String login(String username, String password) {
        System.out.println(String.format("Request received for user -> %s and password %s",username,password));
        try {
            User user = userDao.getUser(username);
            if (user == null || user.getRole() == null || user.getRole().size() < 1 || !user.match(username,password)) {
                throw new CustomException("Invalid username or password.", HttpStatus.UNAUTHORIZED);
            }
            //NOTE: normally we dont need to add "ROLE_" prefix. Spring does automatically for us.
            //Since we are using custom token using JWT we should add ROLE_ prefix
            String token =  jwtTokenProvider.createToken(username, user.getRole().stream()
                    .map((String role)-> "ROLE_"+role).filter(Objects::nonNull).collect(Collectors.toList()));

            userSessionDao.saveUserSession(username,token);
            return token;

        } catch (Exception e) {
            throw new CustomException("Invalid username or password.", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public User saveUser(User user) {
       // user.setPassword(passwordEncoder.encode(user.getPassword()) );
        return null;
    }

    @Override
    public boolean logout(String token) {
        userSessionDao.deleteUserSession(token);
         return true;
    }

    @Override
    public Boolean isValidToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    @Override
    public String createNewToken(String token) {
        String username = jwtTokenProvider.getUsername(token);
        List<String>roleList = jwtTokenProvider.getRoleList(token);
        String newToken =  jwtTokenProvider.createToken(username,roleList);
        return newToken;
    }
}
