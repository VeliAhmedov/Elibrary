package az.edu.elibrary.controller;

import az.edu.elibrary.dto.request.ReqToken;
import az.edu.elibrary.dto.request.ReqUser;
import az.edu.elibrary.dto.response.RespUser;
import az.edu.elibrary.dto.response.Response;
import az.edu.elibrary.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    //this is like login
    @PostMapping("/auth")
    public Response<RespUser> auth(@RequestBody ReqUser reqUser){
        return userService.auth(reqUser);
    }

    @PostMapping("/logout")
    public Response logout(@RequestBody ReqToken reqToken){
        return userService.logout(reqToken);

    }
    //this is like signup
    @PostMapping("/register")
    public Response<RespUser> register(@RequestBody ReqUser reqUser){
        return userService.register(reqUser);
    }
}
