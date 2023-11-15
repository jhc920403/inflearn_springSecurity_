package com.core.corespringsecurity.service;

import com.core.corespringsecurity.domain.entity.Resource;

import java.util.List;

public interface ResourceService {

    public Resource getResource(Long id);
    public List<Resource> getResource();
    public void createResources(Resource Resources);
    public void deleteResources(Long id);
}
