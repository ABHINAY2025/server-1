package com.example.paymentProcess.service.viewService;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TransactionValueViewService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void createTransactionValueView() {
        try {
            // Define the aggregation pipeline stages
            Document facetStage = new Document("$facet", new Document()
                    .append("totalTransactions", Arrays.asList(new Document("$count", "total")))
                    .append("totalAmount", Arrays.asList(new Document("$group", new Document("_id", null)
                            .append("totalAmount", new Document("$sum", "$amount")))))
                    .append("tier1Transactions", Arrays.asList(
                            new Document("$match", new Document("amount", new Document("$gt", 100000000))),
                            new Document("$count", "tier1Total")
                    ))
                    .append("tier1Amount", Arrays.asList(
                            new Document("$match", new Document("amount", new Document("$gt", 100000000))),
                            new Document("$group", new Document("_id", null)
                                    .append("totalAmount", new Document("$sum", "$amount"))
                            )
                    ))
                    .append("tier2Transactions", Arrays.asList(
                            new Document("$match", new Document("amount", new Document("$gt", 10000000).append("$lte", 100000000))),
                            new Document("$count", "tier2Total")
                    ))
                    .append("tier2Amount", Arrays.asList(
                            new Document("$match", new Document("amount", new Document("$gt", 10000000).append("$lte", 100000000))),
                            new Document("$group", new Document("_id", null)
                                    .append("totalAmount", new Document("$sum", "$amount"))
                            )
                    ))
                    .append("tier3Transactions", Arrays.asList(
                            new Document("$match", new Document("amount", new Document("$lte", 10000000))),
                            new Document("$count", "tier3Total")
                    ))
                    .append("tier3Amount", Arrays.asList(
                            new Document("$match", new Document("amount", new Document("$lte", 10000000))),
                            new Document("$group", new Document("_id", null)
                                    .append("totalAmount", new Document("$sum", "$amount"))
                            )
                    ))
            );

            Document projectStage = new Document("$project", new Document()
                    .append("totalTransactions", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 0)))
                    .append("totalAmount", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalAmount.totalAmount", 0)), 0)))
                    .append("tier1Transactions", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$tier1Transactions.tier1Total", 0)), 0)))
                    .append("tier1Amount", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$tier1Amount.totalAmount", 0)), 0)))
                    .append("tier1Percentage", new Document("$cond", Arrays.asList(
                            new Document("$gt", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 0)),
                            new Document("$multiply", Arrays.asList(
                                    new Document("$divide", Arrays.asList(
                                            new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$tier1Transactions.tier1Total", 0)), 0)),
                                            new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 1))
                                    )),
                                    100
                            )),
                            0
                    )))
                    .append("tier2Transactions", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$tier2Transactions.tier2Total", 0)), 0)))
                    .append("tier2Amount", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$tier2Amount.totalAmount", 0)), 0)))
                    .append("tier2Percentage", new Document("$cond", Arrays.asList(
                            new Document("$gt", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 0)),
                            new Document("$multiply", Arrays.asList(
                                    new Document("$divide", Arrays.asList(
                                            new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$tier2Transactions.tier2Total", 0)), 0)),
                                            new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 1))
                                    )),
                                    100
                            )),
                            0
                    )))
                    .append("tier3Transactions", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$tier3Transactions.tier3Total", 0)), 0)))
                    .append("tier3Amount", new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$tier3Amount.totalAmount", 0)), 0)))
                    .append("tier3Percentage", new Document("$cond", Arrays.asList(
                            new Document("$gt", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 0)),
                            new Document("$multiply", Arrays.asList(
                                    new Document("$divide", Arrays.asList(
                                            new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$tier3Transactions.tier3Total", 0)), 0)),
                                            new Document("$ifNull", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$totalTransactions.total", 0)), 1))
                                    )),
                                    100
                            )),
                            0
                    ))));

            // Create the view using the `create` command
            List<Document> pipeline = Arrays.asList(facetStage, projectStage);
            Document createViewCommand = new Document("create", "transactionValueViews")
                    .append("viewOn", "paymentFile")
                    .append("pipeline", pipeline);

            // Execute the command to create the view
            mongoTemplate.getDb().runCommand(createViewCommand);

            System.out.println("TransactionValue view created successfully");

        } catch (Exception e) {
            System.err.println("Error creating transactionValue view: " + e.getMessage());
        }
    }
}

