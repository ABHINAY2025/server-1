package com.example.paymentProcess.service.viewService;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class RepairViewService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void createRepairView() {
        try {
            // Define the aggregation pipeline stages
            Document facetStage = new Document("$facet", new Document()
                    .append("totalTransactions", Arrays.asList(new Document("$count", "total")))
                    .append("totalAmount", Arrays.asList(new Document("$group", new Document("_id", null)
                            .append("totalAmount", new Document("$sum", "$amount")))))
                    .append("toBeRepairedTransactions", Arrays.asList(
                            new Document("$match", new Document("fileStatus", "To Be Repaired")),
                            new Document("$count", "toBeRepairedTotal")
                    ))
                    .append("toBeRepairedAmount", Arrays.asList(
                            new Document("$match", new Document("fileStatus", "To Be Repaired")),
                            new Document("$group", new Document("_id", null)
                                    .append("totalAmount", new Document("$sum", "$amount"))
                            )
                    ))
                    .append("autoCorrectedTransactions", Arrays.asList(
                            new Document("$match", new Document("fileStatus", "Auto Corrected")),
                            new Document("$count", "autoCorrectedTotal")
                    ))
                    .append("autoCorrectedAmount", Arrays.asList(
                            new Document("$match", new Document("fileStatus", "Auto Corrected")),
                            new Document("$group", new Document("_id", null)
                                    .append("totalAmount", new Document("$sum", "$amount"))
                            )
                    ))
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
                    .append("toBeRepairedTransactions", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$toBeRepairedTransactions.toBeRepairedTotal", 0)), 0)))
                    .append("toBeRepairedAmount", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$toBeRepairedAmount.totalAmount", 0)), 0)))
                    .append("toBeRepairedPercentage", new Document("$cond", Arrays.asList(
                            new Document("$gt", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 0)),
                            new Document("$multiply", Arrays.asList(
                                    new Document("$divide", Arrays.asList(
                                            new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$toBeRepairedTransactions.toBeRepairedTotal", 0)), 0)),
                                            new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 1))
                                    )),
                                    100
                            )),
                            0
                    )))
                    .append("autoCorrectedTransactions", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$autoCorrectedTransactions.autoCorrectedTotal", 0)), 0)))
                    .append("autoCorrectedAmount", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$autoCorrectedAmount.totalAmount", 0)), 0)))
                    .append("autoCorrectedPercentage", new Document("$cond", Arrays.asList(
                            new Document("$gt", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 0)),
                            new Document("$multiply", Arrays.asList(
                                    new Document("$divide", Arrays.asList(
                                            new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$autoCorrectedTransactions.autoCorrectedTotal", 0)), 0)),
                                            new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 1))
                                    )),
                                    100
                            )),
                            0
                    )))
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
            Document createViewCommand = new Document("create", "repairedView")
                    .append("viewOn", "paymentFile")
                    .append("pipeline", pipeline);

            // Execute the command to create the view
            mongoTemplate.getDb().runCommand(createViewCommand);

            System.out.println("Repair view created successfully");

        } catch (Exception e) {
            System.err.println("Error creating repair view: " + e.getMessage());
        }
    }
}

