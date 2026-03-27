package tech.sumato.statefinance.web.controllers.api;


import org.apache.http.auth.AuthenticationException;
import org.apache.kafka.common.protocol.types.Field;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.sumato.statefinance.utils.MicroserviceUtils;
import tech.sumato.statefinance.web.entity.BudgetRegister;
import tech.sumato.statefinance.web.models.*;
import tech.sumato.statefinance.web.service.StateFinanceService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class StateFinanceApiController {

    private Logger LOGGER = LoggerFactory.getLogger(StateFinanceApiController.class);


    @Autowired
    private StateFinanceService stateFinanceService;


    @Autowired
    private MicroserviceUtils microserviceUtils;



    @PostMapping(value = "/budgets/submit",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> postForApproval(
            @RequestBody @Valid GenericRequest<BudgetRegisterDTO> requestBody) {


        Map<String, Object> response = new HashMap<>();


        BudgetRegisterDTO budgetRegisterDTO = requestBody.getData();

        response.put("body", budgetRegisterDTO);


        BudgetRegister budgetRegister = stateFinanceService.findBudgetRegisterByTenantAndBudgetRegisterId(budgetRegisterDTO.getTenantId(), budgetRegisterDTO.getBudgetRegisterId());

        if (null != budgetRegister) {
            response.put("ResponseInfo", ResponseInfo.builder().status(String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY.value())).build());
            response.put("Message", "Budget already exists !");
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(response);
        }

        BudgetRegister savedBudgetRegister =  stateFinanceService.saveBudgetRegister(budgetRegisterDTO.mapToEntity());


        ResponseInfo responseInfo = ResponseInfo.builder()
                .status(String.valueOf(HttpStatus.OK.value()))
                .build();

        response.put("ResponseInfo", responseInfo);
        response.put("Data", savedBudgetRegister);


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }



//    @GetMapping(value = "/budgets",
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public @ResponseBody Map<String, Object> showBudgets() throws AuthenticationException {
//
//        List<BudgetRegisterDTO> budgetRegisters =  stateFinanceService.findAllBudgets();
//
////        throw new AuthenticationException();
//
//        Map<String, Object> response = new HashMap<>();
//
////        response.put("message", "Budget registers !");
////        response.put("status", "success");
////        response.put("data", budgetRegisters);
//
//        ResponseInfo responseInfo =  ResponseInfo.builder().status(String.valueOf(HttpStatus.OK.value())).build();
//
//        response.put("ResponseInfo", responseInfo);
//        response.put("Budgets", budgetRegisters);
//
//        return response;
//
//    }


    @GetMapping(
            value = "/budgets",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody Map<String, Object> showBudgetsPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws AuthenticationException {

        Page<BudgetRegisterDTO> budgetPage =
                stateFinanceService.findBudgets(PageRequest.of(page, size));

        ResponseInfo responseInfo = ResponseInfo.builder()
                .status(String.valueOf(HttpStatus.OK.value()))
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("ResponseInfo", responseInfo);
        response.put("Budgets", budgetPage.getContent());

        // Pagination metadata
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("page", budgetPage.getNumber());
        pagination.put("size", budgetPage.getSize());
        pagination.put("totalElements", budgetPage.getTotalElements());
        pagination.put("totalPages", budgetPage.getTotalPages());

        response.put("Pagination", pagination);

        return response;
    }



    @PostMapping(
            value = "/budgets/handleaction",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody Map<String, Object> handleAction(@RequestBody @Valid GenericRequest<StateActionDTO> requestBody) throws Exception {

        Map<String, Object> response = new HashMap<>();

        // check if provided budget register exists

//        if (true) {
//
//            response.put("data", requestBody);
//
//            return response;
//        }

        BudgetRegister budgetRegister = stateFinanceService.findByTenantIdAndBudgetRegisterId(requestBody.getTenantId(), requestBody.getData().getBudgetRegisterId());

        if (null == budgetRegister) {
            response.put("message", "Provided budget register does not exists !");
            response.put("ResponseInfo", ResponseInfo.builder().status(String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY)).build());
            return response;
        }

        BudgetStateActionDTO budgetStateActionDTO = new BudgetStateActionDTO();

        BudgetStateActionDTO.BudgetRegisterAction action = BudgetStateActionDTO.BudgetRegisterAction.REJECT;
        if (requestBody.getData().getAction().equalsIgnoreCase("approve")) {
            action = BudgetStateActionDTO.BudgetRegisterAction.APPROVE;
        }

        budgetStateActionDTO.action = action;
        budgetStateActionDTO.budgetRegister = budgetRegister.toDTO();
        budgetStateActionDTO.tenantId = budgetRegister.getTenantId();
        budgetStateActionDTO.setRequestInfo(requestBody.getRequestInfo());


        microserviceUtils.submitBudgetAction(budgetStateActionDTO, requestBody.getTenantId());


        response.put("message", "Budget action handled successfully !");
        response.put("ResponseInfo", ResponseInfo.builder().status(String.valueOf(HttpStatus.OK)).build());

        return response;


    }


    @GetMapping(
            value = "/budgets/test",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody Map<String, Object> budgetTest() {
        String fUrl = microserviceUtils.getFinanceHost("pg.gmc");
        Map<String, Object> response = new HashMap<>();
        response.put("url", fUrl);
        return response;

    }



}
