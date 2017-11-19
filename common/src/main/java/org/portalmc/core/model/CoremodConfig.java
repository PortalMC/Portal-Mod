package org.portalmc.core.model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.Properties;

public class CoremodConfig {
    public final String hostname;
    public final String clientId;
    public final String clientSecret;

    private CoremodConfig(String hostname, String clientId, String clientSecret) {
        this.hostname = hostname;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public static CoremodConfig load(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        if (!file.canRead()) {
            throw new AccessDeniedException(file.getAbsolutePath());
        }
        Properties properties = new Properties();
        try (InputStreamReader isr = new InputStreamReader(new BufferedInputStream(new FileInputStream(file)), StandardCharsets.UTF_8)) {
            properties.load(isr);
        }
        return new CoremodConfig(
                properties.getProperty("hostname"),
                properties.getProperty("client_id"),
                properties.getProperty("client_secret")
        );
    }
}
