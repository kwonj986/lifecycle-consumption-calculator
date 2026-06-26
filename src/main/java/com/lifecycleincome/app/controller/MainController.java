package com.lifecycleincome.app.controller;

import com.lifecycleincome.app.entity.SiteUser;
import com.lifecycleincome.app.repository.UserRepository;
import com.lifecycleincome.app.service.CalculationService;
import com.lifecycleincome.app.entity.CalculationRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserRepository userRepository;
    private final CalculationService calculationService;

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        // if user comes into "/" (main address), show "index".html.
        // index.html, because of Thymeleaf dependencies, should be under src/main/resources/templates
        // Principal is "current logged in users" by Spring Security
        if (principal != null) {
            String username = principal.getName();
            SiteUser user = userRepository.findByUsername(username).orElse(null);

            if (user != null) {
                model.addAttribute("username", username);
                model.addAttribute("historyList", calculationService.getRecordsByUser(user));
            }
        }
        return "index";
    }

    @PostMapping("/calculate")
    public String calculate(@RequestParam("currentAge") int currentAge,
                            @RequestParam("retirementAge") int retirementAge,
                            @RequestParam("currentAssets") double currentAssets,
                            @RequestParam("annualIncome") double annualIncome,
                            @RequestParam("country") String country,
                            Model model,
                            Principal principal) {

        SiteUser user = null;
        if (principal != null) {
            user = userRepository.findByUsername(principal.getName()).orElse(null);
        }

        try {
            CalculationRecord record = calculationService.calculationAndSave(user, currentAge, retirementAge, currentAssets, annualIncome, country);

            model.addAttribute("result", Math.round(record.getAnnualConsumption()));
            model.addAttribute("workingYears", record.getWorkingYears());
            model.addAttribute("remainingLifeYears", record.getRemainingLifeYears());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("currentAge", currentAge);
        model.addAttribute("retirementAge", retirementAge);
        model.addAttribute("currentAssets", currentAssets);
        model.addAttribute("annualIncome", annualIncome);
        model.addAttribute("country", country);

        return home(model, principal);
    }
}
