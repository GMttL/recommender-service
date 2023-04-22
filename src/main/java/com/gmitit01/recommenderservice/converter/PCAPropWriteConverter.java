package com.gmitit01.recommenderservice.converter;


import com.gmitit01.recommenderservice.entity.PCAProperties;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;


@WritingConverter
public class PCAPropWriteConverter implements Converter<PCAProperties, Document> {

    @Override
    public Document convert(PCAProperties pcaProperties) {
        Document doc = new Document();
        doc.put("loadings", pcaProperties.getLoadings().getMatrix().toArray());
        doc.put("mean", pcaProperties.getMean());
        doc.put("eigvalues", pcaProperties.getEigvalues());
        return doc;
    }

}
