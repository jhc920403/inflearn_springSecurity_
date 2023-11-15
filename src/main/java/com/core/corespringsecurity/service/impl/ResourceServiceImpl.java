package com.core.corespringsecurity.service.impl;

import com.core.corespringsecurity.domain.entity.Resource;
import com.core.corespringsecurity.repository.ResourceRepository;
import com.core.corespringsecurity.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    @Override
    public Resource getResource(Long id) {
        return resourceRepository.findById(id).orElseGet(Resource::new);
    }

    @Override
    public List<Resource> getResource() {
        return resourceRepository.findAll(Sort.by(Sort.Order.asc("orderNum")));
    }

    @Override
    @Transactional(readOnly = false)
    public void createResources(Resource Resource) {
        resourceRepository.save(Resource);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteResources(Long id) {
        resourceRepository.deleteById(id);
    }
}
