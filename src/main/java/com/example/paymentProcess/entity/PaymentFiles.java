//package com.example.demo.entity;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.util.Date;
//import java.util.List;
//@Document(collection = "paymentfiles")
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class PaymentFiles {
//
//
//    private String _id;
//    private String sequenceNumber;
//    private boolean isAutoCorrect;
//    private boolean isStp;
//    private boolean isApproved;
//    private List<String> notes;
//    private boolean toBeEdited;
//    private boolean active;
//    private String originalXml;
//    private Object debtorData;
//    private Object creditorData;
//    private String msgId;
//    private double ctrlSum;
//    private Date createdAt;
//    private Date updatedAt;
//    private CdtrMlSuggestion cdtrMlSuggestion;
//    private DbtrMlSuggestion dbtrMlSuggestion;
//    private String updatedXml;
//    private String fileStatus;
//    private String statusID;
//    private String substatusID;
//
//
//    // Inner class for CdtrMlSuggestion
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class CdtrMlSuggestion {
//        private double MLScore;
//        private String MLSuggestion;
//    }
//
//    // Inner class for DbtrMlSuggestion
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class DbtrMlSuggestion {
//        private double MLScore;
//        private String MLSuggestion;
//    }
//}
