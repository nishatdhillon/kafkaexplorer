package com.kafkaexplorer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kafkaexplorer.logger.MyLogger;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

    private String id;
    private String name;
    private String hostname;
    private String protocol;
    private String mechanism;
    private String jaasConfig;
    private String consumerGroup;
    private String trustStoreJKS;
    private String trustStoreJKSPwd;
    private List<FilterTopic> filterTopics = new ArrayList<FilterTopic>();
    private String srUrl;
    private String srUser;
    private String srPwd;
    private ArrayList<String> topicList;
    @JsonIgnore
    private String apiKey;
    @JsonIgnore
    private String apiSecret;

    public Cluster() {
    }

    public Cluster(Cluster cluster) {
        this.id = cluster.getId();
        this.name = cluster.getName();
        this.hostname = cluster.getHostname();
        this.protocol = cluster.getProtocol();
        this.mechanism = cluster.getMechanism();
        this.jaasConfig = cluster.getJaasConfig();
        this.consumerGroup = cluster.getConsumerGroup();
        this.filterTopics = cluster.getFilterTopics();
        this.trustStoreJKS = cluster.getTrustStoreJKS();
        this.trustStoreJKSPwd = cluster.getTrustStoreJKSPwd();
        this.srPwd = cluster.getSrPwd();
        this.srUser = cluster.getSrUser();
        this.srUrl = cluster.getSrUrl();
    }

    public String getSrUrl() {
        if (this.srUrl == null)
            return "";
        else
            return this.srUrl;
    }

    public String getSrUser() {
        return this.srUser;
    }

    public String getSrPwd() {
        return this.srPwd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConsumerGroup() {

        if (consumerGroup == null)
            return "";
        else
            return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getName() {

        if (name == null)
            return "";
        else
        return name;
    }

    public String getProtocol() {

        if (protocol == null)
            return "";
        else
            return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getMechanism() {

        if (mechanism == null)
            return "";
        else
            return mechanism;
    }

    public void setMechanism(String mechanism) {
        this.mechanism = mechanism;
    }

    public String getJaasConfig() {

        if (jaasConfig == null)
            return "";
        else
            return jaasConfig;
    }

    public void setJaasConfig(String apiKey, String apiSecret) {
        this.jaasConfig = "org.apache.kafka.common.security.plain.PlainLoginModule required username='" + apiKey + "' password='" + apiSecret + "';";
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public List<FilterTopic> getFilterTopics() {
        return filterTopics;
    }

    public void setBookMarksTopics(List<FilterTopic> filterTopics) {
        this.filterTopics = filterTopics;
    }

    public String getTrustStoreJKS() {
        if (trustStoreJKS == null)
            trustStoreJKS = "";

        return trustStoreJKS;
    }

    public void setTrustStoreJKS(String trustStoreJKS) {
        this.trustStoreJKS = trustStoreJKS;
    }

    public String getTrustStoreJKSPwd() {

        if (trustStoreJKSPwd == null)
            trustStoreJKSPwd = "";

        return trustStoreJKSPwd;
    }

    public void setTrustStoreJKSPwd(String trustStoreJKSPwd) {
        this.trustStoreJKSPwd = trustStoreJKSPwd;
    }

    public void setFilterTopics(List<FilterTopic> filterTopics) {
        this.filterTopics = filterTopics;
    }

    public void println() {
        MyLogger.logDebug(this.name);
        MyLogger.logDebug(this.hostname);
        MyLogger.logDebug(this.protocol);
        MyLogger.logDebug(this.mechanism);
        MyLogger.logDebug(this.jaasConfig);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSrUrl(String srUrl) {
        this.srUrl = srUrl;
    }

    public void setSrUser(String srUser) {
        this.srUser = srUser;
    }

    public void setSrPwd(String srPwd) {
        this.srPwd = srPwd;
    }

    public void setTopicList(ArrayList<String> topics) {
        this.topicList = topics;
    }

    public ArrayList<String> getTopicList() {
        return this.topicList;
    }

    public String getApiKey() {
        String apiKey = "";
        if (jaasConfig != null && !jaasConfig.isEmpty()){
            apiKey = jaasConfig.substring(75, 91);
        }
        return apiKey;
    }

    public String getApiSecret() {
        String apiSecret = "";
        if (jaasConfig != null && !jaasConfig.isEmpty()){
            apiSecret = jaasConfig.substring(103, 167);
        }

        return apiSecret;

    }
}
