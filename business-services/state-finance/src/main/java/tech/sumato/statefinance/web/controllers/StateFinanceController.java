package tech.sumato.statefinance.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import tech.sumato.statefinance.web.models.BudgetRegisterDTO;
import tech.sumato.statefinance.web.service.StateFinanceService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/views")
public class StateFinanceController {

    @Autowired
    private StateFinanceService stateFinanceService;

    @GetMapping("/budgets")
    public String budgetsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Page<BudgetRegisterDTO> budgetPage =
                stateFinanceService.findBudgets(PageRequest.of(page, size));

        model.addAttribute("budgets", budgetPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", budgetPage.getTotalPages());
        model.addAttribute("pageSize", size);

        return "budgets/budgetregisters-view"; // budgets.jsp
    }

    @GetMapping("/budgetsall")
    public String budgetsPage() {
        return "budgets/budgetregisters-paginated"; // resolves to budgets.jsp
    }


    @GetMapping(value = "/budgets/datatables", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> showBudgetsDataTable(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length
    ) {

        int page = start / length;

        Page<BudgetRegisterDTO> budgetPage =
                stateFinanceService.findBudgets(PageRequest.of(page, length));

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", budgetPage.getTotalElements());
        response.put("recordsFiltered", budgetPage.getTotalElements());
        response.put("data", budgetPage.getContent());

        return response;
    }



}
