package az.edu.elibrary.service.impl;

import az.edu.elibrary.dto.request.ReqCustomer;
import az.edu.elibrary.dto.request.ReqCustomerBalance;
import az.edu.elibrary.dto.response.RespCustomer;
import az.edu.elibrary.dto.response.RespStatus;
import az.edu.elibrary.dto.response.Response;
import az.edu.elibrary.entity.Customer;
import az.edu.elibrary.entity.User;
import az.edu.elibrary.enums.EnumAvailableStatus;
import az.edu.elibrary.exception.ExceptionConstants;
import az.edu.elibrary.exception.LibraryException;
import az.edu.elibrary.repository.CustomerRepository;
import az.edu.elibrary.repository.UserRepository;
import az.edu.elibrary.service.CustomerService;
import az.edu.elibrary.utils.CurrencyConverter;
import az.edu.elibrary.utils.Utility;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final Utility utility;
    private final UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Override
    public Response<List<RespCustomer>> getCustomerList(String token) {
        Response<List<RespCustomer>> response = new Response<>();
        try{
            LOGGER.info("getCustomerList request has been done with token: "+token);
            utility.checkToken(token);
            List<Customer> customerList = customerRepository.findAllByActive(EnumAvailableStatus.ACTIVE.value);
            if (customerList.isEmpty()){
                LOGGER.warn("there is no active customer in CustomerList ");
                throw new LibraryException(ExceptionConstants.CUSTOMER_NOT_FOUND,"Customer not found");
            }
            List<RespCustomer> respCustomerList = customerList.stream().map(this::convert).collect(Collectors.toList());
            response.setT(respCustomerList);
            LOGGER.info("getCustomerList has been successfully returned response :"+ respCustomerList);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex){
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in getCustomerList : ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        }catch (Exception ex){
           ex.printStackTrace();
            LOGGER.error("Exception occurred in getCustomerList: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }

        return response;
    }

    @Override
    public Response<RespCustomer> getById(Long customerId, String token) {
        Response<RespCustomer> response = new Response<>();
        try {
            LOGGER.info("getById in customer request has been done with token: "+token);
            utility.checkToken(token);
            if (customerId == null){
                LOGGER.warn("there has been send invalid request for CustomerId in getById with customerId of "+ customerId);
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA,"Invalid request data");
            }
            Customer customer = customerRepository.findCustomerByIdAndActive(customerId, EnumAvailableStatus.ACTIVE.value);
            if (customer == null){
                LOGGER.warn("customerId of " +customerId+" isn't active");
                throw new LibraryException(ExceptionConstants.CUSTOMER_NOT_FOUND,"Customer not found");
            }
            RespCustomer respCustomer = convert(customer);
            response.setT(respCustomer);
            LOGGER.info("getById in customer has been successfully returned response "+ respCustomer);
            response.setStatus(RespStatus.getSuccessMessage());
        }catch (LibraryException ex){
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in getById in Customer : ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        }catch (Exception ex){
            ex.printStackTrace();
            LOGGER.error("Exception occurred in getById in Customer: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }

//    @Override
//    public Response create(ReqCustomer reqCustomer, String token) {
//        Response response = new Response();
//        try {
//            LOGGER.info("Create in customer request has been done with token: "+token);
//            utility.checkToken(token);
//            String name = reqCustomer.getName();
//            String surname =reqCustomer.getSurname();
//            Long userId = reqCustomer.getUserId();
//            if (userId==null){
//                LOGGER.warn("userId has been ignored");
//                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA,"invalid request data");
//            }
//            if (name == null || surname == null){
//                LOGGER.warn("customer name or surname has been ignored");
//                throw new LibraryException(ExceptionConstants.CUSTOMER_NAME_OR_SURNAME_NOT_FOUND,"customer surname or name not found");
//            }
//            User user = userRepository.findUserByIdAndActive(userId, EnumAvailableStatus.DEACTIVE.value);
//            if (user == null){
//                LOGGER.warn("user not found");
//                throw new LibraryException(ExceptionConstants.USER_NOT_FOUND,"user not found");
//            }
//            if (customerRepository.findCustomerByUserAndActive(user, EnumAvailableStatus.ACTIVE.value) != null) {
//                LOGGER.warn("User already has a customer");
//                throw new LibraryException(ExceptionConstants.USER_ALREADY_HAVE_CUSTOMER, "User already has a customer");
//            }
//            Customer customer = Customer.builder()
//                    .name(name)
//                    .surname(surname)
//                    .address(reqCustomer.getAddress())
//                    .dob(reqCustomer.getDob())
//                    .phone(reqCustomer.getPhone())
//                    .pin(reqCustomer.getPin())
//                    .libraryCardNumber(reqCustomer.getLibraryCardNumber())
//                    .user(user)
//                    .build();
//            customerRepository.save(customer);
//            // after customer for userid created, user will be activated
//            user.setActive(EnumAvailableStatus.ACTIVE.value);
//            userRepository.save(user);
//            LOGGER.info("Customer has  successfully created data: "+ customer);
//            response.setStatus(RespStatus.getSuccessMessage());
//        }catch (LibraryException ex){
//            ex.printStackTrace();
//            LOGGER.error("LibraryException occurred in create in Customer : ", ex);
//            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
//        }catch (Exception ex){
//            ex.printStackTrace();
//            LOGGER.error("Exception occurred in create in Customer : ", ex);
//            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
//        }
//        return response;
//    }

    @Override
    public Response<RespCustomer> update(ReqCustomer reqCustomer,String token) {
        Response<RespCustomer> response = new Response<>();
        try{
            LOGGER.info("update in customer request has been done with token: "+token);
            utility.checkToken(token);
            Long customerId = reqCustomer.getCustomerId();
            String name = reqCustomer.getName();
            String surname =reqCustomer.getSurname();
            if (customerId == null || name == null || surname == null){
                LOGGER.warn("Either customer name or surname has been ignored or CustomerId of "+customerId+" doesn't exist");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA,"Invalid request data");
            }
            Customer customer = customerRepository.findCustomerByIdAndActive(customerId, EnumAvailableStatus.ACTIVE.value);
            if (customer == null){
                LOGGER.warn("CustomerId of "+ customerId+" isn't active");
                throw new LibraryException(ExceptionConstants.CUSTOMER_NOT_FOUND,"Customer not found");
            }
            customer.setName(name);
            customer.setSurname(surname);
            customer.setAddress(reqCustomer.getAddress());
            customer.setDob(reqCustomer.getDob());
            customer.setPhone(reqCustomer.getPhone());
            customer =customerRepository.save(customer);
            RespCustomer respCustomer = convert(customer);
            response.setT(respCustomer);
            LOGGER.info("customerId of"+ customerId+" has successfully been updated to"+ respCustomer);
            response.setStatus(RespStatus.getSuccessMessage());
        }catch (LibraryException ex){
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in update in Customer : ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        }catch (Exception ex){
            ex.printStackTrace();
            LOGGER.error("Exception occurred in update in Customer : ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }

    @Override
    public Response delete(Long customerId,String token) {
        Response response = new Response();
        try {
            LOGGER.info("delete in customer request has been done with token: "+token);
            utility.checkToken(token);
            if (customerId == null){
                LOGGER.warn("CustomerId of "+ customerId+" doesn't exist");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA,"Invalid request data");
            }
            Customer customer = customerRepository.findCustomerByIdAndActive(customerId, EnumAvailableStatus.ACTIVE.value);
            if (customer == null){
                LOGGER.warn("CustomerId of "+ customerId+"  is already inactive");
                throw new LibraryException(ExceptionConstants.CUSTOMER_NOT_FOUND,"Customer not found");
            }
            customer.setActive(EnumAvailableStatus.DEACTIVE.value);
            customerRepository.save(customer);
            // after customer deleted, user that it is connected will be too
            User user = customer.getUser();
            if (user != null) {
                user.setActive(EnumAvailableStatus.DEACTIVE.value);
                userRepository.save(user);
            }
            LOGGER.info("customerId of "+ customerId+" has been successfully deactivated");
            response.setStatus(RespStatus.getSuccessMessage());
        }catch (LibraryException ex){
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in delete in Customer : ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        }catch (Exception ex){
            ex.printStackTrace();
            LOGGER.error("Exception occurred in delete in Customer : ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }

    @Override
    @Transactional
    public Response<RespCustomer> increaseBalance(ReqCustomerBalance reqCustomerBalance, String token) {
        Response<RespCustomer> response = new Response<>();
        try {
            LOGGER.info("IncreaseBalance request initiated for customerId: " + reqCustomerBalance.getCustomerId() + " with token: " + token);
            utility.checkToken(token);
            Customer customer = customerRepository.findCustomerByIdAndActive(reqCustomerBalance.getCustomerId(), EnumAvailableStatus.ACTIVE.value);
            if (customer == null) {
                LOGGER.warn("CustomerId of " + reqCustomerBalance.getCustomerId() + " is already inactive or not found");
                throw new LibraryException(ExceptionConstants.CUSTOMER_NOT_FOUND, "Customer not found");
            }
            //when customer created it considered as null, this will make null be seen as 0
            double currentBalance = (customer.getBalance() != null) ? customer.getBalance() : 0.0;
            double amountInAZN = CurrencyConverter.convertToAZN(reqCustomerBalance.getAmount(), reqCustomerBalance.getCurrency());
            customer.setBalance(currentBalance + amountInAZN);
            customerRepository.save(customer);
            response.setT(convert(customer));
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in increaseBalance: ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in increaseBalance: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }

    private RespCustomer convert(Customer customer){
        return RespCustomer.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surname(customer.getSurname())
                .address(customer.getAddress())
                .dob(customer.getDob())
                .phone(customer.getPhone())
                .pin(customer.getPin())
                .libraryCardNumber(customer.getLibraryCardNumber())
                .userName(customer.getUser().getUsername())
                .balance(customer.getBalance())
                .build();
    }
}
