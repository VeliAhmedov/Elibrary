package az.project.springbootproject.controller;

import az.project.springbootproject.dto.request.ReqToken;
import az.project.springbootproject.dto.request.ReqUser;
import az.project.springbootproject.dto.response.RespToken;
import az.project.springbootproject.dto.response.RespUser;
import az.project.springbootproject.dto.response.Response;
import az.project.springbootproject.service.UserService;
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
