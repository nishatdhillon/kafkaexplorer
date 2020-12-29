package com.kafkaexplorer.model;

public class Cluster {
    public Cluster() {}

    public Cluster(Cluster cluster) {
        this.name = cluster.name;
        this.hostname = cluster.hostname;
        this.protocol = cluster.protocol;
        this.mechanism = cluster.mechanism;
        this.jaasConfig = cluster.jaasConfig;
    }
    private String name;
    private String hostname;
    private String protocol;
    private String mechanism;
    private String jaasConfig;

    public String getName() {
        return name;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getMechanism() {
        return mechanism;
    }

    public void setMechanism(String mechanism) {
        this.mechanism = mechanism;
    }

    public String getJaasConfig() {
        return jaasConfig;
    }

    public void setJaasConfig(String jaasConfig) {
        this.jaasConfig = jaasConfig;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
