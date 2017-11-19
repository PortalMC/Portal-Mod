package org.portalmc.core.network;

import mjson.Json;
import org.portalmc.core.model.AccessToken;
import org.portalmc.core.model.CoremodConfig;
import org.portalmc.core.model.Project;
import org.portalmc.core.util.Utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class APIClient {

    private final CoremodConfig config;

    public APIClient(CoremodConfig config) {
        this.config = config;
    }

    public AccessToken tryLogin(String email, String password) {
        System.out.println("Start authenticate");
        byte[] body = Utils.convertToFormUrlEncoded(new String[][]{
                {"grant_type", "password"},
                {"username", email},
                {"password", password},
                {"client_id", config.clientId},
                {"client_secret", config.clientSecret}
        }).getBytes(StandardCharsets.UTF_8);
        try {
            URL url = new URL(config.hostname + "/connect/token");
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("charset", "utf-8");
                connection.setUseCaches(false);
                try (DataOutputStream dos = new DataOutputStream(connection.getOutputStream())) {
                    dos.write(body);
                }
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String jsonRaw = Utils.readAll(connection.getInputStream());
                    Json json = Json.read(jsonRaw);
                    return new AccessToken(
                            json.at("resource").asString(),
                            json.at("token_type").asString(),
                            json.at("access_token").asString(),
                            json.at("expires_in").asInteger());
                } else {
                    throw new RuntimeException("Response: " + connection.getResponseCode());
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Project> tryGetProjects(AccessToken accessToken, String minecraftVersion) {
        System.out.println("Start project list");
        try {
            URL url = new URL(config.hostname + "/api/v1/projects?minecraft=" + minecraftVersion);
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", accessToken.tokenType + " " + accessToken.accessToken);
                connection.setRequestProperty("charset", "utf-8");
                connection.setUseCaches(false);
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String jsonRaw = Utils.readAll(connection.getInputStream());
                    Json json = Json.read(jsonRaw);
                    if (json.at("success").asBoolean()) {
                        return json.at("data").asJsonList().stream()
                                .map(j -> new Project(
                                        j.at("id").asString(),
                                        j.at("name").asString(),
                                        j.at("minecraftVersion").asString(),
                                        j.at("forgeVersion").asString()
                                ))
                                .collect(Collectors.toList());
                    } else {
                        throw new RuntimeException("Failed: " + json.at("message").asString());
                    }
                } else {
                    throw new RuntimeException("Response: " + connection.getResponseCode());
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void tryStoreModJar(AccessToken accessToken, String projectId, File target) {
        System.out.println("Start download mod");
        try {
            URL url = new URL(config.hostname + "/api/v1/projects/" + projectId + "/artifact");
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", accessToken.tokenType + " " + accessToken.accessToken);
                connection.setRequestProperty("charset", "utf-8");
                connection.setUseCaches(false);
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try (InputStream inputStream = connection.getInputStream()) {
                        Files.copy(inputStream, target.toPath());
                    }
                } else {
                    throw new RuntimeException("Response: " + connection.getResponseCode());
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
