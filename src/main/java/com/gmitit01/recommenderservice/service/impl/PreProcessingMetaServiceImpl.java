package com.gmitit01.recommenderservice.service.impl;

import com.gmitit01.recommenderservice.entity.PreProcessingMeta;
import com.gmitit01.recommenderservice.repository.PreProcessingMetaRepository;
import com.gmitit01.recommenderservice.service.PreProcessingMetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PreProcessingMetaServiceImpl implements PreProcessingMetaService {

    private final PreProcessingMetaRepository repository;

    @Override
    public PreProcessingMeta createMeta(PreProcessingMeta profile) {
        return repository.save(profile);
    }

    @Override
    public PreProcessingMeta readMeta(UUID uid) {
        return repository.findById(uid).orElse(null);
    }

    @Override
    public List<PreProcessingMeta> readMetas() {
        return repository.findAll();
    }

    @Override
    public void deleteMeta(UUID uid) {
        repository.deleteById(uid);
    }
}
