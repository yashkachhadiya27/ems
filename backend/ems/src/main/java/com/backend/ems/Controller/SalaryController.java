package com.backend.ems.Controller;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.ems.DTO.AddSalaryDTO;
import com.backend.ems.DTO.CustomResponse;
import com.backend.ems.DTO.EmpDetailsSalaryDTO;
import com.backend.ems.Service.Service_implementation.SalaryServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SalaryController {
    private final SalaryServiceImpl salaryServiceImpl;
    private Date today = new Date();

    @GetMapping("/admin/getEmpDetailsSalary")
    public ResponseEntity<Page<EmpDetailsSalaryDTO>> getEmpDetailsSalary(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize) {
        if (keyword.equals("")) {
            return ResponseEntity.ok()
                    .body(salaryServiceImpl.getEmpDetailsSalary(pageNumber, pageSize));
        }
        return ResponseEntity.ok()
                .body(salaryServiceImpl.getAllSearchedEmpDetailsSalary(keyword, pageNumber, pageSize));
    }

    @GetMapping("/admin/getEmpSalaryPayDetails")
    public ResponseEntity<Page<EmpDetailsSalaryDTO>> getEmpSalaryPayDetails(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "date", required = false, defaultValue = "") String date,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize) {
        String[] parts = date.split("-");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        if (keyword.equals("")) {
            return ResponseEntity.ok()
                    .body(salaryServiceImpl.getAllPaySalary(month, year, pageNumber, pageSize));
        }
        return ResponseEntity.ok()
                .body(salaryServiceImpl.getAllSearchedPaySalary(keyword, month, year, pageNumber, pageSize));
    }

    @PostMapping("/admin/addEmpSalary/{userId}")
    public ResponseEntity<CustomResponse> addEmpSalary(@PathVariable int userId,
            @RequestBody @ModelAttribute AddSalaryDTO addSalaryDTO) {
        try {
            salaryServiceImpl.addSalary(userId, addSalaryDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("success", 200));
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse("fail", 400));
        }
    }

    @PatchMapping("/admin/paySalary/{salaryID}")
    public ResponseEntity<CustomResponse> addEmpSalary(@PathVariable int salaryID) {

        boolean b = salaryServiceImpl.paySalary(salaryID);
        if (b) {
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("success", 200));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse("fail", 400));
        }

    }

    @GetMapping("/employee/getEmpAllSalary/{userId}")
    public ResponseEntity<Page<EmpDetailsSalaryDTO>> getEmpAllSalary(
            @PathVariable("userId") int userId,
            @RequestParam(value = "date", required = false, defaultValue = "") String date,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize) {

        if (date.equals("")) {
            return ResponseEntity.ok()
                    .body(salaryServiceImpl.getEmpAllSalary(userId, pageNumber, pageSize));
        }
        return ResponseEntity.ok()
                .body(salaryServiceImpl.getEmpAllSearchedSalary(date, userId, pageNumber,
                        pageSize));
    }
}
