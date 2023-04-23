package com.gmitit01.recommenderservice.converter;

import com.gmitit01.recommenderservice.entity.PCAProperties;
import com.gmitit01.recommenderservice.entity.utils.MatrixWrapper;
import org.springframework.core.convert.converter.Converter;
import org.bson.Document;
import org.springframework.data.convert.ReadingConverter;
import smile.math.matrix.Matrix;

import java.util.List;

@ReadingConverter
public class PCAPropReadConverter implements Converter<Document, PCAProperties> {

    @Override
    public PCAProperties convert(Document document) {
        Document loadingsDoc = document.get("loadings", Document.class);
        Document matrix = loadingsDoc.get("matrix", Document.class);

        int m = matrix.get("m", Integer.class);
        int n = matrix.get("n", Integer.class);
        int ld = matrix.get("ld", Integer.class);

        List A = matrix.get("A", List.class);
        double[] loadingsArray = A.stream().mapToDouble(d -> (double)d).toArray();

        MatrixWrapper loadings = new MatrixWrapper(new Matrix(m, n, ld, loadingsArray));

        List mean = document.get("mean", List.class);
        List eigvalues = document.get("eigvalues", List.class);

        PCAProperties pcaProperties = new PCAProperties();
        pcaProperties.setLoadings(loadings);
        pcaProperties.setMean(mean.stream().mapToDouble(d -> (double)d).toArray());
        pcaProperties.setEigvalues(eigvalues.stream().mapToDouble(d -> (double)d).toArray());
        return pcaProperties;
    }

}
