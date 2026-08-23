package com.ritajshakeel.rrs.repository;

import java.util.List;

import com.ritajshakeel.rrs.domain.Resource;

public interface ResourceRepository {
    Resource save(Resource resource);
    List<Resource> findAll();
}