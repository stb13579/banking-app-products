package com.bankingapp.products.service;

import com.bankingapp.products.entity.Application;
import com.bankingapp.products.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationsService {

    @Autowired
    private ApplicationRepository applicationRepository;

    public Application create(Application application) {
        return applicationRepository.save(application);
    }

    public List<Application> findByUser(String userId) {
        return applicationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Optional<Application> findById(UUID id) {
        return applicationRepository.findById(id);
    }
}
