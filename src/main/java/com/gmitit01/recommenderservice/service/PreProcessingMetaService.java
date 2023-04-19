package com.gmitit01.recommenderservice.service;

import com.gmitit01.recommenderservice.entity.PreProcessingMeta;

import java.util.List;
import java.util.UUID;

public interface PreProcessingMetaService {

    // SAVE Meta
    PreProcessingMeta createMeta(PreProcessingMeta profile);

    // READ Meta
    PreProcessingMeta readMeta(UUID uid);

    List<PreProcessingMeta> readMetas();

    // DELETE Meta
    void deleteMeta(UUID uid);
}
