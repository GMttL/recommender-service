package com.gmitit01.recommenderservice.serviceLayer;

import com.gmitit01.recommenderservice.entity.TrainedModel;
import com.gmitit01.recommenderservice.repository.TrainedModelRepository;
import com.gmitit01.recommenderservice.service.impl.TrainedModelServiceImpl;
import com.gmitit01.recommenderservice.utils.CosineDistance;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import smile.clustering.CLARANS;
import smile.feature.extraction.PCA;

import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class TrainedModelServiceTest {
    /***
     *  This test class is used to test the TrainedModelService class.
     *
     *  SMILE has some troubles optimising the code through the MLK library.
     *  It uses as default OpenBLAS or the Java impl. Either way, fine for now.
     */

    // CONSTANTS
    private static final int RANDOM_DATA_ROWS = 100;
    private static final int RANDOM_DATA_COLUMNS = 8;
    private static final int PCA_COMPONENTS = 4;

    @Mock
    private TrainedModelRepository trainedModelRepository;

    @InjectMocks
    private TrainedModelServiceImpl trainedModelService;

    private TrainedModel trainedModel;

    private TrainedModel createFakeTrainedModel() {
        // Generate random data
        Random random = new Random();
        double[][] data = Stream.generate(() ->
                        DoubleStream.generate(random::nextDouble)
                                .limit(RANDOM_DATA_COLUMNS)
                                .toArray())
                .limit(RANDOM_DATA_ROWS)
                .toArray(double[][]::new);

        // Apply PCA
        PCA pca = PCA.fit(data);
        PCA reducedPCA = pca.getProjection(PCA_COMPONENTS);
        double[][] reducedData = reducedPCA.apply(data);

        // Convert reducedData from double[][] to Double[][]
        Double[][] convertedData = Arrays.stream(reducedData)
                .map(doubleArray -> Arrays.stream(doubleArray)
                        .boxed()
                        .toArray(Double[]::new))
                .toArray(Double[][]::new);

        // Create CLARANS
        CosineDistance cosineDistance = new CosineDistance();
        CLARANS<Double[]> clarans = CLARANS.fit(convertedData, cosineDistance, 3, 20);

        // Create and return the fake trained model
        return new TrainedModel(pca, clarans);
    }

    @BeforeEach
    public void setUp() {
        trainedModel = createFakeTrainedModel();
        trainedModel.setId(UUID.randomUUID());
    }

    @Test
    public void saveModelTest() {
        when(trainedModelRepository.save(any(TrainedModel.class))).thenReturn(trainedModel);

        TrainedModel createdModel = trainedModelService.saveModel(trainedModel);

        assertEquals(trainedModel, createdModel);
        verify(trainedModelRepository, times(1)).save(trainedModel);
    }

    @Test
    public void readModelTest() {
        when(trainedModelRepository.findById(trainedModel.getId())).thenReturn(Optional.of(trainedModel));

        TrainedModel foundModel = trainedModelService.readModel(trainedModel.getId());

        assertEquals(trainedModel, foundModel);
        verify(trainedModelRepository, times(1)).findById(trainedModel.getId());
    }

    @Test
    public void readModelNotFoundTest() {
        when(trainedModelRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        TrainedModel foundModel = trainedModelService.readModel(UUID.randomUUID());

        assertNull(foundModel);
        verify(trainedModelRepository, times(1)).findById(any(UUID.class));
    }


    @Test
    public void readModelsTest() {
        TrainedModel trainedModel2 = createFakeTrainedModel();
        trainedModel2.setId(UUID.randomUUID());

        List<TrainedModel> trainedModels = Arrays.asList(trainedModel, trainedModel2);

        when(trainedModelRepository.findAll()).thenReturn(trainedModels);

        List<TrainedModel> foundModels = trainedModelService.readModels();

        assertEquals(trainedModels, foundModels);
        verify(trainedModelRepository, times(1)).findAll();
    }

    @Test
    public void deleteModelTest() {
        doNothing().when(trainedModelRepository).deleteById(trainedModel.getId());

        trainedModelService.deleteModel(trainedModel.getId());

        verify(trainedModelRepository, times(1)).deleteById(trainedModel.getId());
    }
}
