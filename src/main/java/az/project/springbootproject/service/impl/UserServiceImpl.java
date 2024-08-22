package az.project.springbootproject.service.impl;

import az.project.springbootproject.dto.request.ReqToken;
import az.project.springbootproject.dto.request.ReqUser;
import az.project.springbootproject.dto.response.RespStatus;
import az.project.springbootproject.dto.response.RespToken;
import az.project.springbootproject.dto.response.RespUser;
import az.project.springbootproject.dto.response.Response;
import az.project.springbootproject.entity.User;
import az.project.springbootproject.enums.EnumAvailableStatus;
import az.project.springbootproject.exception.ExceptionConstants;
import az.project.springbootproject.exception.LibraryException;
import az.project.springbootproject.repository.UserRepository;
import az.project.springbootproject.service.UserService;
import az.project.springbootproject.utils.Utility;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.ldap.PagedResultsControl;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Utility utility;
    @Override
    public Response<RespUser> auth(ReqUser reqUser) {
        Response<RespUser> response = new Response<>();
        try{
            String username = reqUser.getUsername();
            String password = reqUser.getPassword();
            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()){
                throw new LibraryException(ExceptionConstants.USERNAME_OR_PASSWORD_IS_EMPTY, "username or password is empty");
            }
            User user = userRepository.findUserByUsernameAndPasswordAndActive(username,password, EnumAvailableStatus.ACTIVE.value);
            if (user == null){
                throw new LibraryException(ExceptionConstants.USER_NOT_FOUND, "user not found");
            }
            if (user.getToken()!= null){
                throw new LibraryException(ExceptionConstants.SESSION_ALREADY_FULL, "session is already full");
            }
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setDataDate(new Date());
            userRepository.save(user);
            RespUser respUser = convertToRespUser(user);
            response.setT(respUser);
            response.setStatus(RespStatus.getSuccessMessage());
        }catch (LibraryException ex) {
            ex.printStackTrace();
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }

    @Override
    public Response logout(ReqToken reqToken) {
        Response response = new Response();
        try {
            Long userId = reqToken.getUserId();
            String token = reqToken.getToken();
            if (userId==null||token==null){
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request data");
            }
            User user = userRepository.findUserByIdAndTokenAndActive(userId, token, EnumAvailableStatus.ACTIVE.value);
            if (user == null){
                throw new LibraryException(ExceptionConstants.USER_TOKEN_IS_INVALID, "User token is invalid");
            }
            user.setToken(null);
            userRepository.save(user);
            response.setStatus(RespStatus.getSuccessMessage());
        }catch (LibraryException ex) {
            ex.printStackTrace();
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }
    //added newly here
    @Override
    public Response<RespUser> register(ReqUser reqUser) {
        Response<RespUser> response = new Response<>();
        try {
            String username = reqUser.getUsername();
            String password = reqUser.getPassword();
            String role = reqUser.getRole();
            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty() || role == null || role.trim().isEmpty()) {
                throw new LibraryException(ExceptionConstants.USERNAME_OR_PASSWORD_IS_EMPTY, "Username, password, or role is empty");
            }
            if (!role.trim().equalsIgnoreCase("admin") && !role.trim().equalsIgnoreCase("customer")) {
                throw new LibraryException(ExceptionConstants.INVALID_ROLE, "Role must be either 'admin' or 'customer'");
            }
            User existingUser = userRepository.findUserByUsernameAndActive(username, EnumAvailableStatus.ACTIVE.value);
            if (existingUser != null) {
                throw new LibraryException(ExceptionConstants.USERNAME_ALREADY_EXISTS, "Username already exists");
            }
            String token = UUID.randomUUID().toString();
            User user = User.builder()
                    .username(username)
                    .password(password)
                    .fullName(reqUser.getFullName())
                    .role(role)
                    .token(token)
                    .dataDate(new Date())
                    //when first created, it would be deactivated because there is no customer that is linked in
                    // and that can cause problem when somebody make same username
                    .active(EnumAvailableStatus.DEACTIVE.value)
                    .build();
            userRepository.save(user);
            response.setT(convertToRespUser(user));
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }

    private RespUser convertToRespUser(User user){
        RespToken respToken = RespToken.builder()
                .userId(user.getId())
                .token(user.getToken())
                .build();
        return RespUser.builder()
                .username(user.getUsername())
                .fullName(user.getFullName())
                .respToken(respToken)
                .Role(user.getRole())
                .build();
    }
}
