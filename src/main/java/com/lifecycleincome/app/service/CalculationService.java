package com.lifecycleincome.app.service;

import com.lifecycleincome.app.controller.MainController;
import com.lifecycleincome.app.entity.CalculationRecord;
import com.lifecycleincome.app.entity.SiteUser;
import com.lifecycleincome.app.repository.CalculationRecordRepository;
import com.lifecycleincome.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculationService {

    private final CalculationRecordRepository recordRepository;

    public CalculationRecord calculationAndSave(SiteUser user, int currentAge, int retirementAge, double currentAssets, double annualIncome, String country) {
        int lifeExpectancy = getLifeExpectancyByCountry(country);
        int workingYears = retirementAge - currentAge;
        int remainingLifeYears = lifeExpectancy - currentAge;

        if (workingYears < 0 || remainingLifeYears <= 0) {
            throw new IllegalArgumentException("Retirement age or current age is longer than expected");
        }
        if (currentAge < 0 || retirementAge < 20) {
            throw new IllegalArgumentException("Retirement age or current age is shorter than expected");
        }

        double totalWealth = currentAssets + (annualIncome * workingYears);
        double annualConsumption = totalWealth / remainingLifeYears;

        CalculationRecord record = new CalculationRecord();
        record.setSiteUser(user);
        record.setCurrentAge(currentAge);
        record.setRetirementAge(retirementAge);
        record.setCurrentAssets(currentAssets);
        record.setAnnualIncome(annualIncome);
        record.setCountry(country);
        record.setWorkingYears(workingYears);
        record.setRemainingLifeYears(remainingLifeYears);
        record.setAnnualConsumption(annualConsumption);

        if (user != null) {
            recordRepository.save(record);
        }

        return record;
    }

    public List<CalculationRecord> getRecordsByUser(SiteUser user) {
        return recordRepository.findBySiteUserOrderByCreatedAtDesc(user);
    }

    public int getLifeExpectancyByCountry(String country) {
        return switch (country) {
            case "Afghanistan" -> 62;
            case "Albania" -> 78;
            case "Algeria" -> 77;
            case "Andorra" -> 83;
            case "Angola" -> 62;
            case "Antigua and Barbuda" -> 77;
            case "Argentina" -> 76;
            case "Armenia" -> 75;
            case "Australia" -> 83;
            case "Austria" -> 81;
            case "Azerbaijan" -> 73;

            case "Bahamas" -> 74;
            case "Bahrain" -> 79;
            case "Bangladesh" -> 73;
            case "Barbados" -> 79;
            case "Belarus" -> 74;
            case "Belgium" -> 81;
            case "Belize" -> 74;
            case "Benin" -> 61;
            case "Bhutan" -> 72;
            case "Bolivia" -> 72;
            case "Bosnia and Herzegovina" -> 77;
            case "Botswana" -> 69;
            case "Brazil" -> 75;
            case "Brunei" -> 75;
            case "Bulgaria" -> 75;
            case "Burkina Faso" -> 61;
            case "Burundi" -> 62;

            case "Cabo Verde" -> 74;
            case "Cambodia" -> 70;
            case "Cameroon" -> 60;
            case "Canada" -> 82;
            case "Central African Republic" -> 54;
            case "Chad" -> 54;
            case "Chile" -> 80;
            case "China" -> 78;
            case "Colombia" -> 77;
            case "Comoros" -> 64;
            case "Congo" -> 64;
            case "Costa Rica" -> 80;
            case "Croatia" -> 78;
            case "Cuba" -> 79;
            case "Cyprus" -> 82;
            case "Czech Republic" -> 79;

            case "Denmark" -> 81;
            case "Djibouti" -> 67;
            case "Dominica" -> 77;
            case "Dominican Republic" -> 74;

            case "Ecuador" -> 77;
            case "Egypt" -> 70;
            case "El Salvador" -> 73;
            case "Equatorial Guinea" -> 60;
            case "Eritrea" -> 66;
            case "Estonia" -> 78;
            case "Eswatini" -> 58;
            case "Ethiopia" -> 65;

            case "Fiji" -> 67;
            case "Finland" -> 82;
            case "France" -> 82;

            case "Gabon" -> 67;
            case "Gambia" -> 64;
            case "Georgia" -> 74;
            case "Germany" -> 81;
            case "Ghana" -> 64;
            case "Greece" -> 81;
            case "Grenada" -> 75;
            case "Guatemala" -> 74;
            case "Guinea" -> 61;
            case "Guinea-Bissau" -> 59;
            case "Guyana" -> 70;

            case "Haiti" -> 64;
            case "Honduras" -> 74;
            case "Hungary" -> 76;

            case "Iceland" -> 83;
            case "India" -> 70;
            case "Indonesia" -> 71;
            case "Iran" -> 76;
            case "Iraq" -> 70;
            case "Ireland" -> 82;
            case "Israel" -> 83;
            case "Italy" -> 83;

            case "Jamaica" -> 74;
            case "Japan" -> 84;
            case "Jordan" -> 75;

            case "Kazakhstan" -> 73;
            case "Kenya" -> 66;
            case "Kiribati" -> 67;
            case "Kuwait" -> 79;
            case "Kyrgyzstan" -> 71;

            case "Laos" -> 68;
            case "Latvia" -> 75;
            case "Lebanon" -> 78;
            case "Lesotho" -> 54;
            case "Liberia" -> 64;
            case "Libya" -> 73;
            case "Liechtenstein" -> 82;
            case "Lithuania" -> 76;
            case "Luxembourg" -> 82;

            case "Madagascar" -> 66;
            case "Malawi" -> 65;
            case "Malaysia" -> 75;
            case "Maldives" -> 79;
            case "Mali" -> 59;
            case "Malta" -> 83;
            case "Marshall Islands" -> 65;
            case "Mauritania" -> 65;
            case "Mauritius" -> 75;
            case "Mexico" -> 75;
            case "Micronesia" -> 68;
            case "Moldova" -> 72;
            case "Monaco" -> 85;
            case "Mongolia" -> 70;
            case "Montenegro" -> 76;
            case "Morocco" -> 75;
            case "Mozambique" -> 61;
            case "Myanmar" -> 67;

            case "Namibia" -> 64;
            case "Nauru" -> 63;
            case "Nepal" -> 70;
            case "Netherlands" -> 82;
            case "New Zealand" -> 82;
            case "Nicaragua" -> 75;
            case "Niger" -> 62;
            case "Nigeria" -> 55;
            case "North Korea" -> 72;
            case "North Macedonia" -> 75;
            case "Norway" -> 83;

            case "Oman" -> 78;

            case "Pakistan" -> 67;
            case "Palau" -> 69;
            case "Panama" -> 78;
            case "Papua New Guinea" -> 65;
            case "Paraguay" -> 74;
            case "Peru" -> 76;
            case "Philippines" -> 71;
            case "Poland" -> 78;
            case "Portugal" -> 82;

            case "Qatar" -> 80;

            case "Romania" -> 75;
            case "Russia" -> 72;
            case "Rwanda" -> 69;

            case "Saint Kitts and Nevis" -> 76;
            case "Saint Lucia" -> 76;
            case "Saint Vincent and the Grenadines" -> 74;
            case "Samoa" -> 73;
            case "San Marino" -> 84;
            case "Sao Tome and Principe" -> 70;
            case "Saudi Arabia" -> 75;
            case "Senegal" -> 67;
            case "Serbia" -> 75;
            case "Seychelles" -> 73;
            case "Sierra Leone" -> 55;
            case "Singapore" -> 84;
            case "Slovakia" -> 77;
            case "Slovenia" -> 81;
            case "Solomon Islands" -> 70;
            case "Somalia" -> 57;
            case "South Africa" -> 64;
            case "South Korea" -> 83;
            case "South Sudan" -> 58;
            case "Spain" -> 83;
            case "Sri Lanka" -> 77;
            case "Sudan" -> 66;
            case "Suriname" -> 71;
            case "Sweden" -> 83;
            case "Switzerland" -> 84;
            case "Syria" -> 72;

            case "Taiwan" -> 80;
            case "Tajikistan" -> 71;
            case "Tanzania" -> 66;
            case "Thailand" -> 77;
            case "Timor-Leste" -> 69;
            case "Togo" -> 61;
            case "Tonga" -> 70;
            case "Trinidad and Tobago" -> 73;
            case "Tunisia" -> 76;
            case "Turkey" -> 77;
            case "Turkmenistan" -> 68;
            case "Tuvalu" -> 67;

            case "Uganda" -> 64;
            case "Ukraine" -> 71;
            case "United Arab Emirates" -> 79;
            case "United Kingdom" -> 81;
            case "United States" -> 77;
            case "Uruguay" -> 78;
            case "Uzbekistan" -> 71;

            case "Vanuatu" -> 70;
            case "Vatican City" -> 84;
            case "Venezuela" -> 72;
            case "Vietnam" -> 75;

            case "Yemen" -> 66;

            case "Zambia" -> 64;
            case "Zimbabwe" -> 62;
            default -> 80;
        };
    }
}
