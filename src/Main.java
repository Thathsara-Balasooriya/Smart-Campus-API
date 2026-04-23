package com.smartcampus;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

public final class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private Main() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        URI baseUri = URI.create("http://0.0.0.0:8080/");
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, new SmartCampusApplication(), false);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
        server.start();
        logger.log(Level.INFO, "Smart Campus API running at {0}api/v1", baseUri);
        Thread.currentThread().join();
    }
}
