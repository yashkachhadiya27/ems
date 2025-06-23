package com.backend.ems.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backend.ems.Entity.Experience;
import com.backend.ems.Entity.Register;
import com.backend.ems.Repository.RegisterRepository;
import com.opencsv.CSVWriter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CSVExportService {
    private final RegisterRepository registerRepository;

    public void writeEmployeesToCsv(PrintWriter writer, List<Register> employees) {
        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] csvHeader = { "First Name", "Middle Name", "Last Name", "Email", "Gender",
                    "Department", "Date of Joining", "Date of Birth", "Phone",
                    "Street", "Postal Code", "District", "State", "City", "Country",
                    "Experience" };

            csvWriter.writeNext(csvHeader);

            for (Register employee : employees) {
                if (employee.getRole().equals("ADMIN")) {
                    continue;
                }
                List<Experience> experiences = employee.getExperience();

                String experienceString = experiences.stream()
                        .map(exp -> String.format(
                                "Company: %s, Position: %s, Total Exp: %s, Start Date: %s, End Date: %s",
                                exp.getCompany(), exp.getPosition(), exp.getTotalExp(), exp.getStartDate(),
                                exp.getEndDate()))
                        .collect(Collectors.joining(" | "));

                experienceString = experienceString.isEmpty() ? "" : experienceString;

                String[] rowData = {
                        employee.getFname(), employee.getMname(), employee.getLname(), employee.getEmail(),
                        employee.getGender(), employee.getDepartment(), employee.getDateOfJoining().toString(),
                        employee.getDateOfBirth().toString(), employee.getPhone(),
                        employee.getAddress().getStreet(), employee.getAddress().getPostalcode(),
                        employee.getAddress().getDistrict(), employee.getAddress().getState(),
                        employee.getAddress().getCity(), employee.getAddress().getCountry(),
                        experienceString
                };

                csvWriter.writeNext(rowData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<Register> employeesData() {
        return registerRepository.findAll();
    }
}
