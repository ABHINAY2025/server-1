//package com.example.demo.service.viewService;
//
//import org.bson.Document;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Service
//public class DashboardViewService {
//
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
//    public void createDashboardView() {
//        try {
//            // Define the aggregation pipeline stages
//            Document facetStage = new Document("$facet", new Document()
//                    .append("approvedTransactions", Arrays.asList(
//                            new Document("$lookup", new Document()
//                                    .append("from", "approveView")
//                                    .append("pipeline", Arrays.asList(
//                                            new Document("$project", new Document()
//                                                    .append("approvedTransactions", "$approvedTransactions")
//                                                    .append("approvedAmount", "$approvedAmount")
//                                            ))
//                                    ))
//                                    .append("as", "approvedData")
//                    ))
//                    .append("autocorrectedTransactions", Arrays.asList(
//                            new Document("$lookup", new Document()
//                                    .append("from", "autocorrectView")
//                                    .append("pipeline", Arrays.asList(
//                                            new Document("$project", new Document()
//                                                    .append("autocorrectedTransactions", "$autocorrectedTransactions")
//                                                    .append("autocorrectedAmount", "$autocorrectedAmount")
//                                            ))
//                                    ))
//                                    .append("as", "autocorrectedData")
//                    ))
//                    .append("onHoldTransactions", Arrays.asList(
//                            new Document("$lookup", new Document()
//                                    .append("from", "onHoldViews")
//                                    .append("pipeline", Arrays.asList(
//                                            new Document("$project", new Document()
//                                                    .append("onHoldTransactions", "$onHoldTransactions")
//                                                    .append("onHoldAmount", "$onHoldAmount")
//                                                    .append("onHoldPercentage", "$onHoldPercentage")
//                                            ))
//                                    ))
//                                    .append("as", "onHoldData")
//                    ))
//                    .append("releasedTransactions", Arrays.asList(
//                            new Document("$lookup", new Document()
//                                    .append("from", "releasedView")
//                                    .append("pipeline", Arrays.asList(
//                                            new Document("$project", new Document()
//                                                    .append("releasedTransactions", "$releasedTransactions")
//                                                    .append("releasedAmount", "$releasedAmount")
//                                                    .append("releasedPercentage", "$releasedPercentage")
//                                            ))
//                                    ))
//                                    .append("as", "releasedData")
//                    ))
//            );
//
//            // Now we will use $project to format the output
//            Document projectStage = new Document("$project", new Document()
//                    .append("approvedTransactions", new Document("$ifNull", Arrays.asList(
//                            new Document("$arrayElemAt", Arrays.asList("$approvedData.approvedTransactions", 0)), 0
//                    )))
//                    .append("approvedAmount", new Document("$ifNull", Arrays.asList(
//                            new Document("$arrayElemAt", Arrays.asList("$approvedData.approvedAmount", 0)), 0
//                    )))
//                    .append("autocorrectedTransactions", new Document("$ifNull", Arrays.asList(
//                            new Document("$arrayElemAt", Arrays.asList("$autocorrectedData.autocorrectedTransactions", 0)), 0
//                    )))
//                    .append("autocorrectedAmount", new Document("$ifNull", Arrays.asList(
//                            new Document("$arrayElemAt", Arrays.asList("$autocorrectedData.autocorrectedAmount", 0)), 0
//                    )))
//                    .append("onHoldTransactions", new Document("$ifNull", Arrays.asList(
//                            new Document("$arrayElemAt", Arrays.asList("$onHoldData.onHoldTransactions", 0)), 0
//                    )))
//                    .append("onHoldAmount", new Document("$ifNull", Arrays.asList(
//                            new Document("$arrayElemAt", Arrays.asList("$onHoldData.onHoldAmount", 0)), 0
//                    )))
//                    .append("onHoldPercentage", new Document("$ifNull", Arrays.asList(
//                            new Document("$arrayElemAt", Arrays.asList("$onHoldData.onHoldPercentage", 0)), 0
//                    )))
//                    .append("releasedTransactions", new Document("$ifNull", Arrays.asList(
//                            new Document("$arrayElemAt", Arrays.asList("$releasedData.releasedTransactions", 0)), 0
//                    )))
//                    .append("releasedAmount", new Document("$ifNull", Arrays.asList(
//                            new Document("$arrayElemAt", Arrays.asList("$releasedData.releasedAmount", 0)), 0
//                    )))
//                    .append("releasedPercentage", new Document("$ifNull", Arrays.asList(
//                            new Document("$arrayElemAt", Arrays.asList("$releasedData.releasedPercentage", 0)), 0
//                    )))
//            );
//
//            // Create the view using the `create` command
//            List<Document> pipeline = Arrays.asList(facetStage, projectStage);
//            Document createViewCommand = new Document("create", "dashboardView")
//                    .append("viewOn", "paymentFile")  // If this is the source collection
//                    .append("pipeline", pipeline);
//
//            // Execute the command to create the view
//            mongoTemplate.getDb().runCommand(createViewCommand);
//
//            System.out.println("Dashboard view created successfully");
//
//        } catch (Exception e) {
//            System.err.println("Error creating dashboard view: " + e.getMessage());
//        }
//    }
//}

package com.example.paymentProcess.service.viewService;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DashboardViewService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void createDashboardView() {
        try {
            // Define the aggregation pipeline stages

            // $facet Stage to perform multiple lookups at once
            Document facetStage = new Document("$facet", new Document()
                    .append("approvedTransactions", Arrays.asList(
                            new Document("$lookup", new Document()
                                    .append("from", "approvedView")
                                    .append("pipeline", Arrays.asList(
                                            new Document("$project", new Document()
                                                    .append("approvedTransactions", "$approvedTransactions")
                                                    .append("approvedAmount", "$approvedAmount")
                                                    .append("approvedPercentage", "$approvedPercentage")
                                            ))
                                    ))
                                    .append("as", "approvedData")
                    ))
                    .append("autocorrectedTransactions", Arrays.asList(
                            new Document("$lookup", new Document()
                                    .append("from", "autocorrectView")
                                    .append("pipeline", Arrays.asList(
                                            new Document("$project", new Document()
                                                    .append("autocorrectedTransactions", "$autoCorrectedTransactions")
                                                    .append("autocorrectedAmount", "$autoCorrectedAmount")
                                                    .append("autocorrectedPercentage", "$autoCorrectedPercentage")
                                            ))
                                    ))
                                    .append("as", "autocorrectedData")
                    ))
                    .append("toBeRepairedTransactions", Arrays.asList(
                            new Document("$lookup", new Document()
                                    .append("from", "repairedView")
                                    .append("pipeline", Arrays.asList(
                                            new Document("$project", new Document()
                                                    .append("toBeRepairedTransactions", "$toBeRepairedTransactions")
                                                    .append("toBeRepairedAmount", "$toBeRepairedAmount")
                                                    .append("toBeRepairedPercentage", "$toBeRepairedPercentage")
                                            ))
                                    ))
                                    .append("as", "repairedData")
                    ))
                    .append("stpTransactions", Arrays.asList(
                            new Document("$lookup", new Document()
                                    .append("from", "stpViews")
                                    .append("pipeline", Arrays.asList(
                                            new Document("$project", new Document()
                                                    .append("stpTransactions", "$stpTransactions")
                                                    .append("stpAmount", "$stpAmount")
                                                    .append("stpPercentage", "$stpPercentage")
                                            ))
                                    ))
                                    .append("as", "stpData")
                    ))
                    .append("transactionReceivedTransactions", Arrays.asList(
                            new Document("$lookup", new Document()
                                    .append("from", "TransReceivedView")
                                    .append("pipeline", Arrays.asList(
                                            new Document("$project", new Document()
                                                    .append("transactionReceivedTransactions", "$transactionReceivedTransactions")
                                                    .append("transactionReceivedAmount", "$transactionReceivedAmount")
                                                    .append("transactionReceivedPercentage", "$percentage")
                                            ))
                                    ))
                                    .append("as", "transReceivedData")
                    ))
                    .append("transactionValueTransactions", Arrays.asList(
                            new Document("$lookup", new Document()
                                    .append("from", "transactionValueViews")
                                    .append("pipeline", Arrays.asList(
                                            new Document("$project", new Document()
                                                    .append("tier1Transactions", "$tier1Transactions")
                                                    .append("tier1Amount", "$tier1Amount")
                                                    .append("tier1Percentage", "$tier1Percentage")
                                                    .append("tier2Transactions", "$tier2Transactions")
                                                    .append("tier2Amount", "$tier2Amount")
                                                    .append("tier2Percentage", "$tier2Percentage")
                                                    .append("tier3Transactions", "$tier3Transactions")
                                                    .append("tier3Amount", "$tier3Amount")
                                                    .append("tier3Percentage", "$tier3Percentage")
                                            ))
                                    ))
                                    .append("as", "transValueData")
                    ))
            );

            // $project Stage to format the output, ensuring nulls are handled
            Document projectStage = new Document("$project", new Document()
                    .append("approvedTransactions", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$approvedData.approvedTransactions", 0)), 0
                    )))
                    .append("approvedAmount", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$approvedData.approvedAmount", 0)), 0
                    )))
                    .append("approvedPercentage", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$approvedData.approvedPercentage", 0)), 0
                    )))
                    .append("autocorrectedTransactions", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$autocorrectedData.autocorrectedTransactions", 0)), 0
                    )))
                    .append("autocorrectedAmount", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$autocorrectedData.autocorrectedAmount", 0)), 0
                    )))
                    .append("autocorrectedPercentage", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$autocorrectedData.autocorrectedPercentage", 0)), 0
                    )))
                    .append("toBeRepairedTransactions", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$repairedData.toBeRepairedTransactions", 0)), 0
                    )))
                    .append("toBeRepairedAmount", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$repairedData.toBeRepairedAmount", 0)), 0
                    )))
                    .append("toBeRepairedPercentage", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$repairedData.toBeRepairedPercentage", 0)), 0
                    )))
                    .append("stpTransactions", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$stpData.stpTransactions", 0)), 0
                    )))
                    .append("stpAmount", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$stpData.stpAmount", 0)), 0
                    )))
                    .append("stpPercentage", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$stpData.stpPercentage", 0)), 0
                    )))
                    .append("transactionReceivedTransactions", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$transReceivedData.transactionReceivedTransactions", 0)), 0
                    )))
                    .append("transactionReceivedAmount", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$transReceivedData.transactionReceivedAmount", 0)), 0
                    )))
                    .append("transactionReceivedPercentage", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$transReceivedData.transactionReceivedPercentage", 0)), 0
                    )))
                    .append("tier1Transactions", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$transValueData.tier1Transactions", 0)), 0
                    )))
                    .append("tier1Amount", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$transValueData.tier1Amount", 0)), 0
                    )))
                    .append("tier1Percentage", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$transValueData.tier1Percentage", 0)), 0
                    )))
                    .append("tier2Transactions", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$transValueData.tier2Transactions", 0)), 0
                    )))
                    .append("tier2Amount", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$transValueData.tier2Amount", 0)), 0
                    )))
                    .append("tier2Percentage", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$transValueData.tier2Percentage", 0)), 0
                    )))
                    .append("tier3Transactions", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$transValueData.tier3Transactions", 0)), 0
                    )))
                    .append("tier3Amount", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$transValueData.tier3Amount", 0)), 0
                    )))
                    .append("tier3Percentage", new Document("$ifNull", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList("$transValueData.tier3Percentage", 0)), 0
                    )))
            );

            // Combine the stages into an aggregation pipeline
            List<Document> pipeline = Arrays.asList(facetStage, projectStage);
            Document createViewCommand = new Document("create", "dashboardView")
                    .append("viewOn", "paymentFile")  // Source collection name
                    .append("pipeline", pipeline);

            // Execute the command to create the view
            mongoTemplate.getDb().runCommand(createViewCommand);

            System.out.println("Dashboard view created successfully");

        } catch (Exception e) {
            System.err.println("Error creating dashboard view: " + e.getMessage());
        }
    }
}


