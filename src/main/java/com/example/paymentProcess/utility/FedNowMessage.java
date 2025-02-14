package com.example.paymentProcess.utility;


import lombok.Data;
import lombok.Getter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@XmlRootElement(name = "Document")
@Data
public class FedNowMessage {

    private MessageIdentification messageIdentification;
    private StatusReasonInformation statusReasonInformation;

    @XmlElement(name = "Admi.002.001.01")
    public void setMessageIdentification(MessageIdentification messageIdentification) {
        this.messageIdentification = messageIdentification;
    }

    @XmlElement(name = "StatusReasonInformation")
    public void setStatusReasonInformation(StatusReasonInformation statusReasonInformation) {
        this.statusReasonInformation = statusReasonInformation;
    }


    @Getter
    public static class MessageIdentification {
        private String messageID;
        private String creationDateTime;

        @XmlElement(name = "MessageID")
        public void setMessageID(String messageID) {
            this.messageID = messageID;
        }

        @XmlElement(name = "CreationDateTime")
        public void setCreationDateTime(String creationDateTime) {
            this.creationDateTime = creationDateTime;
        }

    }

    @Getter
    public static class StatusReasonInformation {
        private String code;
        private String additionalInformation;

        @XmlElement(name = "Reason")
        public void setCode(String code) {
            this.code = code;
        }

        @XmlElement(name = "AdditionalInformation")
        public void setAdditionalInformation(String additionalInformation) {
            this.additionalInformation = additionalInformation;
        }

    }
}



