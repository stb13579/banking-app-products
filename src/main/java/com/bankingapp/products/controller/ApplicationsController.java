package com.bankingapp.products.controller;

import com.bankingapp.products.entity.Application;
import com.bankingapp.products.service.ApplicationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/applications")
@Tag(name = "Applications")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationsController {

    @Autowired
    private ApplicationsService applicationsService;

    @GetMapping
    @Operation(summary = "List your applications")
    public List<Application> listMine(@AuthenticationPrincipal String userId) {
        return applicationsService.findByUser(userId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application by ID")
    public ResponseEntity<Application> getOne(@PathVariable UUID id) {
        return applicationsService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
