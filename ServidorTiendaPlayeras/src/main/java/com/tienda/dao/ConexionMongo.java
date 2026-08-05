package com.tienda.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class ConexionMongo {
    private static MongoClient mongoClient = null;
    private static MongoDatabase database = null;

    private ConexionMongo() {}

    public static MongoDatabase getDatabase() {
        if (database == null) {
            String uri = System.getenv("MONGO_URI");
            String dbName = System.getenv("MONGO_DB");

            if (uri == null || uri.isEmpty()) {
                uri = "mongodb://localhost:27017";
            }
            if (dbName == null || dbName.isEmpty()) {
                dbName = "tienda_playeras";
            }

            try {
                mongoClient = MongoClients.create(uri);
                database = mongoClient.getDatabase(dbName);
                System.out.println("Conexión exitosa a MongoDB: " + dbName);
            } catch (Exception e) {
                System.err.println("Error conectando a MongoDB: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return database;
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
        }
    }
}
