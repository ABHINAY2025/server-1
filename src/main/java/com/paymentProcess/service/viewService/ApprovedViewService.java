package com.paymentProcess.service.viewService;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ApprovedViewService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void createApprovedView() {
        try {
            // Define the aggregation pipeline stages
            Document facetStage = new Document("$facet", new Document()
                    .append("totalTransactions", Arrays.asList(new Document("$count", "total")))
                    .append("totalAmount", Arrays.asList(new Document("$group", new Document("_id", null)
                            .append("totalAmount", new Document("$sum", "$amount")))))
                    .append("approvedTransactions", Arrays.asList(
                            new Document("$match", new Document("fileStatus", "Approved")),
                            new Document("$count", "approvedTotal")
                    ))
                    .append("approvedAmount", Arrays.asList(
                            new Document("$match", new Document("fileStatus", "Approved")),
                            new Document("$group", new Document("_id", null)
                                    .append("totalAmount", new Document("$sum", "$amount"))
                            )
                    ))
            );

            Document projectStage = new Document("$project", new Document()
                    .append("totalTransactions", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 0)))
                    .append("totalAmount", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalAmount.totalAmount", 0)), 0)))
                    .append("approvedTransactions", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$approvedTransactions.approvedTotal", 0)), 0)))
                    .append("approvedAmount", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$approvedAmount.totalAmount", 0)), 0)))
                    .append("approvedPercentage", new Document("$cond", Arrays.asList(
                            new Document("$gt", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 0)),
                            new Document("$multiply", Arrays.asList(
                                    new Document("$divide", Arrays.asList(
                                            new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$approvedTransactions.approvedTotal", 0)), 0)),
                                            new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 1))
                                    )),
                                    100
                            )),
                            0
                    ))));

            // Create the view using the `create` command
            List<Document> pipeline = Arrays.asList(facetStage, projectStage);
            Document createViewCommand = new Document("create", "approveView")
                    .append("viewOn", "paymentFile")
                    .append("pipeline", pipeline);

            // Execute the command to create the view
            mongoTemplate.getDb().runCommand(createViewCommand);

            System.out.println("Approved view created successfully");

        } catch (Exception e) {
            System.err.println("Error creating approved view: " + e.getMessage());
        }
    }
}

