package az.edu.elibrary.service;

import az.edu.elibrary.dto.request.ReqToken;
import az.edu.elibrary.dto.request.ReqUser;
import az.edu.elibrary.dto.response.RespUser;
import az.edu.elibrary.dto.response.Response;

public interface UserService {
    Response<RespUser> auth(ReqUser reqUser);

    Response logout(ReqToken reqToken);

    Response<RespUser> register(ReqUser reqUser);
}
