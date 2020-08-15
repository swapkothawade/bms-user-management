package com.mybank.api.controller;

import com.mybank.api.domain.UserProfile;
import com.mybank.api.service.ILoginService;
import com.mybank.api.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mybank.api.auth.LoginRequest;
import com.mybank.api.auth.AuthResponse;
import java.util.ArrayList;
import java.util.List;


@RestController()
@RequestMapping("/api")
public class BmsUserController {

    @Autowired
    private ILoginService iLoginService;


    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/public/login")
    public String login(){
        return "Welcome to my website,its public URL";
    }



    @CrossOrigin("*")
    @PostMapping("/public/login")
    @ResponseBody
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = iLoginService.login(loginRequest.getUsername(),loginRequest.getPassword());
        HttpHeaders headers = new HttpHeaders();
        List<String> headerlist = new ArrayList<>();
        List<String> exposeList = new ArrayList<>();
        headerlist.add("Content-Type");
        headerlist.add(" Accept");
        headerlist.add("X-Requested-With");
        headerlist.add("Authorization");
        headers.setAccessControlAllowHeaders(headerlist);
        exposeList.add("Authorization");
        headers.setAccessControlExposeHeaders(exposeList);
        headers.set("Authorization", token);
        return new ResponseEntity<AuthResponse>(new AuthResponse(token), headers, HttpStatus.CREATED);
    }

    @GetMapping("/restricted/detail")
    public String details(){
        return "You are not welcome  to my website,its restricted URL";
    }

    @CrossOrigin("*")
    @PostMapping("/restricted/signout")
    @ResponseBody
    public ResponseEntity<AuthResponse> logout (@RequestHeader("authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token =  token.substring(7);
        }
        HttpHeaders headers = new HttpHeaders();
        if (iLoginService.logout(token)) {
            headers.remove("Authorization");
            return new ResponseEntity<AuthResponse>(new AuthResponse("logged out"), headers, HttpStatus.CREATED);
        }
        return new ResponseEntity<AuthResponse>(new AuthResponse("Logout Failed"), headers, HttpStatus.NOT_MODIFIED);
    }

    @CrossOrigin("*")
    @PostMapping("/public/signin")
    @ResponseBody
    public ResponseEntity<AuthResponse> signin (@RequestBody UserProfile userProfile) {
        HttpHeaders headers = new HttpHeaders();
        try {
            userProfileService.registerUser(userProfile);

            // Post Registration Authorization token can be created. But for now we can ask user to login.
            return new ResponseEntity<AuthResponse>(new AuthResponse("Registration Successful"), headers, HttpStatus.CREATED);
        }catch(Exception  exception){
            return new ResponseEntity<AuthResponse>(new AuthResponse("Registration Failed " + exception.getMessage()), headers, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/restricted/profile/{email}")
    public ResponseEntity<UserProfile> profile(@PathVariable("email") String email){
        UserProfile userProfile = userProfileService.findUserProfile(email);
        return new ResponseEntity<UserProfile>(userProfile,HttpStatus.OK);

    }

    /**
     * Action will be done by logged in user only, So profile will always exist.
     * @param token,  To make it stateless each request will carry token and necessary information can beretrived from DB using token.
     * @param userProfile
     * @return
     */
    @PutMapping("/restricted/profile")
    @CrossOrigin("*")
    @ResponseBody
    public ResponseEntity<AuthResponse> updateProfile(@RequestHeader("authorization") String token,@RequestBody UserProfile userProfile){
        HttpHeaders headers = new HttpHeaders();
        try{
        // We can have additional level of authentication, like again check authorization header is present in session collection.

            boolean status = userProfileService.updateUserProfile(userProfile);
            return new ResponseEntity<AuthResponse>(new AuthResponse("Update Profile Successful"), headers,HttpStatus.OK);
        }catch(Exception exception){
            return new ResponseEntity<AuthResponse>(new AuthResponse("exception"), headers,HttpStatus.NOT_MODIFIED);
        }

    }

}
