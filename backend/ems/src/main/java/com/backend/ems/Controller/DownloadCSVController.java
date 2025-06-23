package com.backend.ems.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.ems.Entity.Register;
import com.backend.ems.Service.CSVExportService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class DownloadCSVController {
    private final CSVExportService downloadService;

    @GetMapping("/download/employees")
    public void downloadCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"employees.csv\"");

        List<Register> employees = downloadService.employeesData();
        downloadService.writeEmployeesToCsv(response.getWriter(), employees);
    }
}
