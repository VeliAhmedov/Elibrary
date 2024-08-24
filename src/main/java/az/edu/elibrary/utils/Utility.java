package az.edu.elibrary.utils;

import az.edu.elibrary.dto.request.ReqToken;
import az.edu.elibrary.entity.User;
import az.edu.elibrary.enums.EnumAvailableStatus;
import az.edu.elibrary.exception.ExceptionConstants;
import az.edu.elibrary.exception.LibraryException;
import az.edu.elibrary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
