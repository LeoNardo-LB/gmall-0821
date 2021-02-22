package dto;

import java.io.Serializable;

/**
 * Author: Administrator
 * Create: 2021/2/21
 **/
public class ShortMessageDto implements Serializable {

    private Integer messageType;

    private String phoneNumber;

    public ShortMessageDto() {
    }

    public ShortMessageDto(Integer messageType, String phoneNumber) {
        this.messageType = messageType;
        this.phoneNumber = phoneNumber;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                       "messageType=" + messageType +
                       ", phoneNumber='" + phoneNumber + '\'' +
                       '}';
    }

}
