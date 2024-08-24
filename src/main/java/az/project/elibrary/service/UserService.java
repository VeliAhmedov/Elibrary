package az.project.elibrary.service;

import az.project.elibrary.dto.request.ReqToken;
import az.project.elibrary.dto.request.ReqUser;
import az.project.elibrary.dto.response.RespUser;
import az.project.elibrary.dto.response.Response;

public interface UserService {
    Response<RespUser> auth(ReqUser reqUser);

    Response logout(ReqToken reqToken);

    Response<RespUser> register(ReqUser reqUser);
}
