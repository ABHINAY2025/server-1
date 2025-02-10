package com.example.paymentProcess.service.viewService;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ReleasedViewService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void createReleasedView() {
        try {
            // Define the aggregation pipeline stages
            Document facetStage = new Document("$facet", new Document()
                    .append("totalTransactions", Arrays.asList(new Document("$count", "total")))
                    .append("releasedTransactions", Arrays.asList(
                            // Updated to include multiple statuses
                            new Document("$match", new Document("fileStatus", new Document("$in", Arrays.asList("txnReleased", "Approved", "Auto Corrected", "STP")))),
                            new Document("$count", "releasedTotal")
                    ))
                    .append("releasedAmount", Arrays.asList(
                            new Document("$match", new Document("fileStatus", new Document("$in", Arrays.asList("txnReleased", "Approve", "Auto Corrected", "STP")))),
                            new Document("$match", new Document("amount", new Document("$ne", null))),
                            new Document("$group", new Document("_id", null)
                                    .append("totalAmount", new Document("$sum", "$amount"))
                            )
                    ))
            );

            Document projectStage = new Document("$project", new Document()
                    .append("totalTransactions", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 0
                    )))
                    .append("releasedTransactions", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$releasedTransactions.releasedTotal", 0)), 0
                    )))
                    .append("releasedAmount", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$releasedAmount.totalAmount", 0)), 0
                    )))
                    .append("releasedPercentage", new Document("$cond", Arrays.asList(
                            new Document("$gt", Arrays.asList(
                                    new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 0)),
                                    0
                            )),

                            // Calculate percentage based on releasedTransactions and totalTransactions
                            new Document("$multiply", Arrays.asList(
                                    new Document("$divide", Arrays.asList(
                                            new Document("$ifNull", Arrays.asList(
                                                    new Document("$arrayElemAt", Arrays.asList("$releasedTransactions.releasedTotal", 0)), 0
                                            )),
                                            new Document("$ifNull", Arrays.asList(
                                                    new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 1
                                            ))
                                    )),
                                    100
                            )),
                            0 // If condition is not met, return 0
                    ))));

            // Create the view using the `create` command
            List<Document> pipeline = Arrays.asList(facetStage, projectStage);
            Document createViewCommand = new Document("create", "releasedView")
                    .append("viewOn", "paymentFile")
                    .append("pipeline", pipeline);

            // Execute the command to create the view
            mongoTemplate.getDb().runCommand(createViewCommand);

            System.out.println("Released view created successfully");

        } catch (Exception e) {
            System.err.println("Error creating Released view: " + e.getMessage());
        }
    }
}
