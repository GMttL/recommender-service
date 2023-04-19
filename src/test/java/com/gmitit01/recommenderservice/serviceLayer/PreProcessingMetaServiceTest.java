package com.gmitit01.recommenderservice.serviceLayer;

import com.gmitit01.recommenderservice.entity.PreProcessingMeta;
import com.gmitit01.recommenderservice.repository.PreProcessingMetaRepository;
import com.gmitit01.recommenderservice.service.impl.PreProcessingMetaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreProcessingMetaServiceTest {

    @InjectMocks
    private PreProcessingMetaServiceImpl preProcessingMetaService;

    @Mock
    private PreProcessingMetaRepository preProcessingMetaRepository;

    private PreProcessingMeta fakePreProcessingMeta;

    @BeforeEach
    void setUp() {
        fakePreProcessingMeta = createFakePreProcessingMeta();
    }

    @Test
    void testCreateMeta() {
        when(preProcessingMetaRepository.save(any(PreProcessingMeta.class))).thenReturn(fakePreProcessingMeta);

        PreProcessingMeta createdPreProcessingMeta = preProcessingMetaService.createMeta(fakePreProcessingMeta);

        assertNotNull(createdPreProcessingMeta);
        assertEquals(fakePreProcessingMeta, createdPreProcessingMeta);
    }

    @Test
    void testReadMeta() {
        UUID id = UUID.randomUUID();
        when(preProcessingMetaRepository.findById(id)).thenReturn(Optional.of(fakePreProcessingMeta));

        PreProcessingMeta readPreProcessingMeta = preProcessingMetaService.readMeta(id);

        assertNotNull(readPreProcessingMeta);
        assertEquals(fakePreProcessingMeta, readPreProcessingMeta);
    }

    @Test
    void testReadMetas() {
        when(preProcessingMetaRepository.findAll()).thenReturn(List.of(fakePreProcessingMeta));

        List<PreProcessingMeta> preProcessingMetas = preProcessingMetaService.readMetas();

        assertNotNull(preProcessingMetas);
        assertEquals(1, preProcessingMetas.size());
        assertEquals(fakePreProcessingMeta, preProcessingMetas.get(0));
    }

    @Test
    void testDeleteMeta() {
        UUID id = UUID.randomUUID();
        preProcessingMetaService.deleteMeta(id);

        verify(preProcessingMetaRepository, times(1)).deleteById(id);
    }

    private PreProcessingMeta createFakePreProcessingMeta() {
        PreProcessingMeta preProcessingMeta = new PreProcessingMeta();
        preProcessingMeta.setUniqueGenders(Set.of("Male", "Female", "Other"));
        preProcessingMeta.setUniqueOccupations(Set.of("Engineer", "Doctor", "Teacher"));

        // Set scaler parameters
        preProcessingMeta.setAgeScalerMean(new double[]{35.0});
        preProcessingMeta.setAgeScalerScale(new double[]{10.0});
        preProcessingMeta.setBudgetScalerMean(new double[]{50000.0});
        preProcessingMeta.setBudgetScalerScale(new double[]{10000.0});
        preProcessingMeta.setMinTermScalerMean(new double[]{12.0});
        preProcessingMeta.setMinTermScalerScale(new double[]{3.0});
        preProcessingMeta.setMaxTermScalerMean(new double[]{36.0});
        preProcessingMeta.setMaxTermScalerScale(new double[]{6.0});

        // Set amenities vectorizer vocabulary
        preProcessingMeta.setAmenitiesVectorizerVocabulary(List.of("Gym", "Swimming Pool", "Parking"));

        return preProcessingMeta;
    }
}
