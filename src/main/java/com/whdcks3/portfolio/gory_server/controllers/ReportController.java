package com.whdcks3.portfolio.gory_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whdcks3.portfolio.gory_server.data.requests.ReportRequest;
import com.whdcks3.portfolio.gory_server.service.ReportService;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    ReportService reportService;

    // 유저 신고
    @PostMapping("/reportUser")
    public ResponseEntity<?> reportUser(@RequestParam Long reporterId, @RequestParam Long reportedId,
            ReportRequest req) {
        reportService.reportUser(reporterId, reportedId, req);
        return ResponseEntity.ok().build();
    }
}
