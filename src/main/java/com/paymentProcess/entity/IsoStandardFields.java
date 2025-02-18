package com.paymentProcess.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "isostandardfields")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IsoStandardFields {

    private String _id;
    private String field;
    private String label;

}
