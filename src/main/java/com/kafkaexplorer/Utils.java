package com.kafkaexplorer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.kafkaexplorer.model.Cluster;

import java.io.File;
import java.io.IOException;

public class Utils {

    public Utils() {
    }

    Cluster getClusterByName(String clusterName) {

        Cluster cluster = new Cluster();

        //Load config.yaml file from the user.home/kafkaexplorer/config.yaml
        String path = System.getProperty("user.home") + File.separator + "kafkaexplorer" + File.separator + "config.yaml";
        File file = new File(path);

        // Instantiating a new ObjectMapper as a YAMLFactory
        ObjectMapper om = new ObjectMapper(new YAMLFactory());

        // Mapping the cluster Array from the YAML file to the Cluster class
        try {
            Cluster[] clusters = null;

            clusters = om.readValue(file, Cluster[].class);

                for (int i = 0; i < clusters.length; i++) {
                    if (clusters[i].getName().equals(clusterName))
                        cluster = clusters[i];
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        //Debug
        cluster.println();


        return cluster;
    }

}

