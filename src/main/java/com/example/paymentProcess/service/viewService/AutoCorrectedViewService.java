package com.example.paymentProcess.service.viewService;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AutoCorrectedViewService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void createAutoCorrectedView() {
        try {
            // Define the aggregation pipeline stages
            Document facetStage = new Document("$facet", new Document()
                    .append("totalTransactions", Arrays.asList(
                            new Document("$count", "total")  // This is correct, just a simple string for the count field
                    ))
                    .append("autocorrectedTransactions", Arrays.asList(
                            new Document("$match", new Document("autoCorrected.creditorAutoCorrected", true)
                                    .append("autoCorrected.debtorAutoCorrected", true)
                            ),
                            new Document("$count", "autocorrectedTotal")  // Correct usage, a simple string for the count field
                    ))
                    .append("autocorrectedAmount", Arrays.asList(
                            new Document("$match", new Document("autoCorrected.creditorAutoCorrected", true)
                                    .append("autoCorrected.debtorAutoCorrected", true)),
                            new Document("$group", new Document("_id", null)
                                    .append("total", new Document("$sum", new Document("$ifNull", Arrays.asList("$amount", 0)))) // Summing up `amount`
                            )
                    ))
            );


            Document projectStage = new Document("$project", new Document()
                    .append("totalTransactions", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 0)))
                    .append("autocorrectedTransactions", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$autocorrectedTransactions.autocorrectedTotal", 0)), 0)))
                    .append("autocorrectedAmount", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$autocorrectedAmount.total", 0)), 0)))
                    .append("autocorrectedPercentage", new Document("$cond", Arrays.asList(
                            new Document("$gt", Arrays.asList(  // Check if totalTransactions > 0
                                    new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 0
                            )),
                            new Document("$multiply", Arrays.asList(  // If true, calculate (autocorrectedTransactions / totalTransactions) * 100
                                    new Document("$divide", Arrays.asList(
                                            new Document("$ifNull", Arrays.asList(
                                                    new Document("$arrayElemAt", Arrays.asList("$autocorrectedTransactions.autocorrectedTotal", 0)), 0
                                            )),
                                            new Document("$ifNull", Arrays.asList(
                                                    new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 1
                                            ))
                                    )),
                                    100
                            )),
                            0  // If false (i.e., no total transactions), return 0
                    ))));


            // Create the view using the `create` command
            List<Document> pipeline = Arrays.asList(facetStage, projectStage);

            Document createViewCommand = new Document("create", "autocorrectView")
                    .append("viewOn", "paymentFile")
                    .append("pipeline", pipeline);

            // Execute the command to create the view
            mongoTemplate.getDb().runCommand(createViewCommand);

            System.out.println("Autocorrect view created successfully");

        } catch (Exception e) {
            System.err.println("Error creating view: " + e.getMessage());
        }
    }
}

