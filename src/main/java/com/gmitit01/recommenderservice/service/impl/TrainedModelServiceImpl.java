package com.gmitit01.recommenderservice.service.impl;

import com.gmitit01.recommenderservice.entity.TrainedModel;
import com.gmitit01.recommenderservice.repository.TrainedModelRepository;
import com.gmitit01.recommenderservice.service.TrainedModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TrainedModelServiceImpl implements TrainedModelService {

    private final TrainedModelRepository repository;

    @Override
    public TrainedModel saveModel(TrainedModel model) {
        return repository.save(model);
    }

    @Override
    public TrainedModel readModel(UUID uid) {
        return repository.findById(uid).orElse(null);
    }

    @Override
    public List<TrainedModel> readModels() {
        Sort sort = Sort.by(Sort.Direction.DESC, "date");
        return repository.findAll(sort);
    }

    @Override
    public TrainedModel getLatestModel() {
        return readModels().get(0);
    }

    @Override
    public void deleteModel(UUID uid) {
        repository.deleteById(uid);
    }
}
