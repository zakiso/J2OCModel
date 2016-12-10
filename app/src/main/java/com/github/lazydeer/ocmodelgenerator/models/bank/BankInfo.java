package com.github.lazydeer.ocmodelgenerator.models.bank;


import java.io.Serializable;

/**
 * Created by dzq on 2016/11/7.
 */

public class BankInfo extends Bank implements Serializable {
    //给界面展示使用
    public String bank_name_and_number;

    private int cardId;

    private String cardHolderId;

    private String cardHolderName;

    private String cardHolderMobile;

    private String cvv2;

    private boolean expired;

    public String getBank_name_and_number() {
        return bank_name_and_number;
    }

    public void setBank_name_and_number(String bank_name_and_number) {
        this.bank_name_and_number = bank_name_and_number;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getCardHolderId() {
        return cardHolderId;
    }

    public void setCardHolderId(String cardHolderId) {
        this.cardHolderId = cardHolderId;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardHolderMobile() {
        return cardHolderMobile;
    }

    public void setCardHolderMobile(String cardHolderMobile) {
        this.cardHolderMobile = cardHolderMobile;
    }

    public String getCvv2() {
        return cvv2;
    }

    public void setCvv2(String cvv2) {
        this.cvv2 = cvv2;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
