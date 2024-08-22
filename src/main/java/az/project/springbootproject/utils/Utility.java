package az.project.springbootproject.utils;

import az.project.springbootproject.dto.request.ReqToken;
import az.project.springbootproject.dto.response.RespStatus;
import az.project.springbootproject.entity.User;
import az.project.springbootproject.enums.EnumAvailableStatus;
import az.project.springbootproject.exception.ExceptionConstants;
import az.project.springbootproject.exception.LibraryException;
import az.project.springbootproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class Utility {
    private final UserRepository userRepository;
    public  User checkToken(ReqToken reqToken){
        if (reqToken.getToken()==null|| reqToken.getUserId()==null){
            throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA,"Invalid request data");
        }
        User user = userRepository.findUserByIdAndTokenAndActive(reqToken.getUserId(), reqToken.getToken(), EnumAvailableStatus.ACTIVE.value);
        if (user == null){
            throw new LibraryException(ExceptionConstants.USER_TOKEN_IS_INVALID, "user token is invalid");
        }
        return user;
    }
    public  User checkToken(String token){
        if (token == null){
            throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA,"Invalid request data");
        }
        User user = userRepository.findUserByTokenAndActive(token, EnumAvailableStatus.ACTIVE.value);
        if (user == null){
            throw new LibraryException(ExceptionConstants.USER_TOKEN_IS_INVALID, "user token is invalid");
        }
        return user;
    }
}
