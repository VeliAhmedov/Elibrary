package az.project.springbootproject.service;

import az.project.springbootproject.dto.request.ReqToken;
import az.project.springbootproject.dto.request.ReqUser;
import az.project.springbootproject.dto.response.RespUser;
import az.project.springbootproject.dto.response.Response;
import org.springframework.stereotype.Service;

public interface UserService {
    Response<RespUser> auth(ReqUser reqUser);

    Response logout(ReqToken reqToken);

    Response<RespUser> register(ReqUser reqUser);
}
