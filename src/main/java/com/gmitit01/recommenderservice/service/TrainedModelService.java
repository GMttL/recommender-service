package com.gmitit01.recommenderservice.service;

import com.gmitit01.recommenderservice.entity.TrainedModel;

import java.util.List;
import java.util.UUID;

public interface TrainedModelService {

    // Save Model
    TrainedModel saveModel(TrainedModel model);

    // Read Model
    TrainedModel readModel(UUID uid);

    List<TrainedModel> readModels();

    // Delete Model
    void deleteModel(UUID model);

    TrainedModel getLatestModel();
}
