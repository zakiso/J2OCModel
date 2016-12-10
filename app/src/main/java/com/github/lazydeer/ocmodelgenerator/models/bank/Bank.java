package com.github.lazydeer.ocmodelgenerator.models.bank;


import java.io.Serializable;

/**
 * Created by dzq on 2016/11/7.
 */

public class Bank implements Serializable {


    private String id;

    private String channel;

    private String bankType;

    private String bankTypeName;

    private String bankBranch;

    private String cardType;



    /**
     * 银行资料ID
     */

    public String getId() {
        return this.id;
    }

    /**
     * 银行资料ID
     */

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 支付渠道
     */

    public String getChannel() {
        return this.channel;
    }

    /**
     * 支付渠道
     */

    public void setChannel(String channel) {
        this.channel = channel;
    }

    /**
     * 银行类型，例如ICBC
     */

    public String getBankType() {
        return this.bankType;
    }

    /**
     * 银行类型，例如ICBC
     */

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    /**
     * 银行类型名称，例如中国银行
     */

    public String getBankTypeName() {
        return this.bankTypeName;
    }

    /**
     * 银行类型名称，例如中国银行
     */

    public void setBankTypeName(String bankTypeName) {
        this.bankTypeName = bankTypeName;
    }

    /**
     * 分行地址
     */

    public String getBankBranch() {
        return this.bankBranch;
    }

    /**
     * 分行地址
     */

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    /**
     * 卡类型，取值：借记卡，信用卡
     */

    public String getCardType() {
        return this.cardType;
    }

    /**
     * 卡类型，取值：借记卡，信用卡
     */

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

}
