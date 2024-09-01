package az.edu.elibrary.service.impl;

import az.edu.elibrary.dto.request.ReqToken;
import az.edu.elibrary.dto.request.ReqUser;
import az.edu.elibrary.dto.response.RespStatus;
import az.edu.elibrary.dto.response.RespToken;
import az.edu.elibrary.dto.response.RespUser;
import az.edu.elibrary.dto.response.Response;
import az.edu.elibrary.entity.Customer;
import az.edu.elibrary.entity.User;
import az.edu.elibrary.enums.EnumAvailableStatus;
import az.edu.elibrary.exception.ExceptionConstants;
import az.edu.elibrary.exception.LibraryException;
import az.edu.elibrary.repository.CustomerRepository;
import az.edu.elibrary.repository.UserRepository;
import az.edu.elibrary.service.UserService;
import az.edu.elibrary.utils.Utility;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
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
            String email = reqUser.getEmail();
            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty() || role == null || role.trim().isEmpty() || email == null || email.trim().isEmpty()) {
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
                    .email(email)
                    .role(role)
                    .token(token)
                    .dataDate(new Date())
                    //when first created, it would be deactivated because there is no customer that is linked in
                    // and that can cause problem when somebody make same username
                    .active(EnumAvailableStatus.DEACTIVE.value)
                    .build();
            userRepository.save(user);
            // Automatically create customer if role is 'customer'
            if (role.trim().equalsIgnoreCase("customer")) {
                Customer customer = Customer.builder()
                        .user(user)
                        .name(reqUser.getName()) // Add other required fields
                        .surname(reqUser.getSurname())
                        .dob(reqUser.getDob())
                        .address(reqUser.getAddress())
                        .libraryCardNumber(reqUser.getLibraryCardNumber())
                        .phone(reqUser.getPhone())
                        .pin(reqUser.getPin())
                        .balance(0.0)
                        .build();
                customerRepository.save(customer);

                // Update user to active status after customer is created
                user.setActive(EnumAvailableStatus.ACTIVE.value);
                userRepository.save(user);
            }
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
                .respToken(respToken)
                .Role(user.getRole())
                .build();
    }
}
