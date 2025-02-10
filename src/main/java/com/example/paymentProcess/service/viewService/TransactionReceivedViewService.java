package com.example.paymentProcess.service.viewService;


import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TransactionReceivedViewService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void createTransactionReceivedView() {
        try {
            // Drop the view if it exists
            mongoTemplate.getDb().getCollection("TransReceivedView").drop();
            System.out.println("View doesn't exist, skipping drop.");

            // Define the aggregation pipeline stages
            Document facetStage = new Document("$facet", new Document()
                    .append("totalTransactions", Arrays.asList(
                            new Document("$count", "total")
                    ))
                    .append("transactionReceivedAmount", Arrays.asList(
                            new Document("$group", new Document("_id", null)
                                    .append("total", new Document("$sum", new Document("$ifNull", Arrays.asList("$amount", 0))))
                            )
                    ))
            );

            Document projectStage = new Document("$project", new Document()
                    .append("totalTransactions", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 0)))
                    .append("transactionReceivedAmount", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$transactionReceivedAmount.total", 0)), 0)))
                    .append("percentage", new Document("$round", Arrays.asList(
                            new Document("$cond", Arrays.asList(
                                    new Document("$gt", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 0)),
                                    100, 0
                            )),
                            2
                    )))
            );

            // Create the view using the `create` command
            List<Document> pipeline = Arrays.asList(facetStage, projectStage);  // Correct usage of List<Document>

            Document createViewCommand = new Document("create", "TransReceivedView")
                    .append("viewOn", "paymentFile")
                    .append("pipeline", pipeline);

            // Execute the command
            mongoTemplate.getDb().runCommand(createViewCommand);

            System.out.println("TransReceivedView created successfully");

        } catch (Exception e) {
            System.err.println("Error creating view: " + e.getMessage());
        }
    }

}

