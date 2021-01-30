package com.kafkaexplorer.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.kafkaexplorer.model.Cluster;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ConfigStore {

    public ConfigStore() {
    }

    public HashMap<String, String> validateYamlConfig() {

        HashMap<String, String> errorList = new HashMap<String, String>();

        String path = System.getProperty("user.home") + File.separator + ".kafkaexplorer" + File.separator + "config.yaml";
        File file = new File(path);

        // Instantiating a new ObjectMapper as a YAMLFactory
        ObjectMapper om = new ObjectMapper(new YAMLFactory());

        // Mapping the cluster Array from the YAML file to the Cluster class
        try {
            Cluster[] clusters = null;

            clusters = om.readValue(file, Cluster[].class);

            for (int i = 0; i < clusters.length; i++) {
                //for each cluster found, validate each fields
                //Todo do some basic validation on each field content
                clusters[i].getName();
                clusters[i].getHostname();
                clusters[i].getProtocol();
                clusters[i].getMechanism();
                clusters[i].getJaasConfig();
            }

        } catch (IOException e) {
            errorList.put("config.yaml format error.", e.getMessage());
        }


        return errorList;

    }


     public Cluster[] loadClusters() throws IOException {

         //Load config.yaml file from the user.home/kafkaexplorer/config.yaml
         String path = System.getProperty("user.home") + File.separator + ".kafkaexplorer" + File.separator + "config.yaml";
         File file = new File(path);

         // Instantiating a new ObjectMapper as a YAMLFactory
         ObjectMapper om = new ObjectMapper(new YAMLFactory());

         // Mapping the cluster Array from the YAML file to the Cluster class

         Cluster[] clusters = om.readValue(file, com.kafkaexplorer.model.Cluster[].class);
        return clusters;
     }



    public Cluster getClusterByName(String clusterName) {

        Cluster cluster = new Cluster();

        //Load config.yaml file from the user.home/kafkaexplorer/config.yaml
        String path = System.getProperty("user.home") + File.separator + ".kafkaexplorer" + File.separator + "config.yaml";
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

    public boolean saveCluster(Cluster cluster) {
        System.out.println("Cluster to save: " + cluster.getId());
        //real yaml file
        try {
            Cluster[] clusters = this.loadClusters();

            //locate cluster to update
            for (int i=0; i < clusters.length; i++) {
                System.out.println("found!!!!" + clusters[i].getId() + "  "+ cluster.getId());
                if (clusters[i].getId().equals(cluster.getId())) {
                    //update cluster

                    clusters[i] = cluster;
                }
            }


            //save file
            ObjectMapper om = new ObjectMapper(new YAMLFactory());
            om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            om.writeValue(new File(System.getProperty("user.home") + File.separator + ".kafkaexplorer" + File.separator + "config.yaml"), clusters);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }



    }

    public void deleteCluster(Cluster cluster) {
        System.out.println("Cluster to delete: " + cluster.getId());
        //real yaml file
        //locate cluster to delete
        //delete cluster
        //save file
    }

}

