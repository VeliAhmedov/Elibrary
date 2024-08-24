package az.project.elibrary.service.impl;

import az.project.elibrary.dto.request.ReqDiscount;
import az.project.elibrary.dto.response.RespDiscount;
import az.project.elibrary.dto.response.RespStatus;
import az.project.elibrary.dto.response.Response;
import az.project.elibrary.entity.Discount;
import az.project.elibrary.enums.EnumAvailableStatus;
import az.project.elibrary.exception.ExceptionConstants;
import az.project.elibrary.exception.LibraryException;
import az.project.elibrary.repository.DiscountRepository;
import az.project.elibrary.service.DiscountService;
import az.project.elibrary.utils.Utility;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;
    private final Utility utility;
    private static final Logger LOGGER = LoggerFactory.getLogger(PublisherServiceImpl.class);
    @Override
    public Response<List<RespDiscount>> getDiscountList(String token) {
        Response<List<RespDiscount>> response = new Response<>();
        try {
            LOGGER.info("getDiscountList request initiated with token: " + token);
            utility.checkToken(token);
            List<Discount> discountList = discountRepository.findAllByActive(EnumAvailableStatus.ACTIVE.value);
            if (discountList.isEmpty()) {
                LOGGER.warn("No active discounts found.");
                throw new LibraryException(ExceptionConstants.DISCOUNT_NOT_FOUND, "Discount not found");
            }
            List<RespDiscount> respDiscountList = discountList.stream()
                    .map(this::convert)
                    .collect(Collectors.toList());
            response.setT(respDiscountList);
            LOGGER.info("getDiscountList successfully returned response with discounts: " + respDiscountList);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in getDiscountList: ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in getDiscountList: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }
    @Override
    public Response<RespDiscount> getById(Long discountId,String token) {
        Response<RespDiscount> response = new Response<>();
        try {
            LOGGER.info("getById request initiated with discountId: " + discountId + " and token: " + token);
            utility.checkToken(token);
            if (discountId == null) {
                LOGGER.warn("Invalid request data: discountId is null.");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request data");
            }
            Discount discount = discountRepository.findDiscountByIdAndActive(discountId, EnumAvailableStatus.ACTIVE.value);
            if (discount == null) {
                LOGGER.warn("Discount with id: " + discountId + " not found.");
                throw new LibraryException(ExceptionConstants.DISCOUNT_NOT_FOUND, "Discount not found");
            }
            RespDiscount respDiscount = convert(discount);
            response.setT(respDiscount);
            LOGGER.info("getById successfully returned response with discount: " + respDiscount);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in getById: ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in getById: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }
    @Override
    public Response create(ReqDiscount reqDiscount,String token) {
        Response response = new Response<>();
        try {
            LOGGER.info("create request initiated with token: " + token);
            utility.checkToken(token);
            if (reqDiscount.getName() == null || reqDiscount.getDiscountPercentage() == null ||
                    reqDiscount.getStartDate() == null || reqDiscount.getEndDate() == null) {
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request Data");
            }
            // this will make sure discount time isn't more than 20 says
            long duration = reqDiscount.getEndDate().getTime() - reqDiscount.getStartDate().getTime();
            long days = TimeUnit.MILLISECONDS.toDays(duration); // total 20 days
            if ( days> 20) {
                throw new LibraryException(ExceptionConstants.DISCOUNT_PERIOD_NOT_EXCEED, "Discount period cannot exceed 20 days");
            }
            // Check for overlapping discounts, this will prevent them
            List<Discount> overlappingDiscounts = discountRepository.findByStartDateBeforeAndEndDateAfter(reqDiscount.getEndDate(), reqDiscount.getStartDate());
            if (!overlappingDiscounts.isEmpty()) {
                throw new LibraryException(ExceptionConstants.DISCOUNT_PERIOD_OVERLAPPING, "Discount time interval overlaps with an already existing discount");
            }
            Discount discount = Discount.builder()
                    .name(reqDiscount.getName())
                    .discountPercentage(reqDiscount.getDiscountPercentage())
                    .startDate(reqDiscount.getStartDate())
                    .endDate(reqDiscount.getEndDate())
                    .build();
            discountRepository.save(discount);
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
    @Override
    public Response<RespDiscount> update(Long discountId, ReqDiscount reqDiscount,String token) {
        Response<RespDiscount> response = new Response<>();
        try {
            utility.checkToken(token);
            if (discountId == null || reqDiscount.getName() == null || reqDiscount.getDiscountPercentage() == null ||
                    reqDiscount.getStartDate() == null || reqDiscount.getEndDate() == null) {
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request Data");
            }
            Discount discount = discountRepository.findDiscountByIdAndActive(discountId, EnumAvailableStatus.ACTIVE.value);
            if (discount == null) {
                throw new LibraryException(ExceptionConstants.DISCOUNT_NOT_FOUND, "Discount not found");
            }
            // this will make sure discount time isn't more than 20 says
            long duration = reqDiscount.getEndDate().getTime() - reqDiscount.getStartDate().getTime();
            long days = TimeUnit.MILLISECONDS.toDays(duration); // same here
            if (days > 20) {
                throw new LibraryException(ExceptionConstants.DISCOUNT_PERIOD_NOT_EXCEED, "Discount period cannot exceed 20 days");
            }

            // Check for overlapping discounts, this will prevent them
            List<Discount> overlappingDiscounts = discountRepository.findByStartDateBeforeAndEndDateAfter(reqDiscount.getEndDate(), reqDiscount.getStartDate());
            if (!overlappingDiscounts.isEmpty() && !Long.valueOf(overlappingDiscounts.get(0).getId()).equals(discountId)) {
                throw new LibraryException(ExceptionConstants.DISCOUNT_PERIOD_OVERLAPPING, "Discount Time Interval overlaps with an already existing discount");
            }

            discount.setName(reqDiscount.getName());
            discount.setDiscountPercentage(reqDiscount.getDiscountPercentage());
            discount.setStartDate(reqDiscount.getStartDate());
            discount.setEndDate(reqDiscount.getEndDate());
            discount = discountRepository.save(discount);
            RespDiscount respDiscount = convert(discount);
            response.setT(respDiscount);
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
    @Override
    public Response delete(Long discountId,String token) {
        Response response = new Response<>();
        try {
            utility.checkToken(token);
            if (discountId == null) {
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request Data");
            }
            Discount discount = discountRepository.findDiscountByIdAndActive(discountId, EnumAvailableStatus.ACTIVE.value);
            if (discount == null) {
                throw new LibraryException(ExceptionConstants.DISCOUNT_NOT_FOUND, "Discount not found");
            }
            discount.setActive(EnumAvailableStatus.DEACTIVE.value);
            discountRepository.save(discount);
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
    private RespDiscount convert(Discount discount) {
        return RespDiscount.builder()
                .id(discount.getId())
                .name(discount.getName())
                .discountPercentage(discount.getDiscountPercentage())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .build();
    }
}
