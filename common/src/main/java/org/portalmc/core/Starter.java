package org.portalmc.core;

import org.portalmc.core.model.AccessToken;
import org.portalmc.core.model.CoremodConfig;
import org.portalmc.core.network.APIClient;
import org.portalmc.core.ui.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Starter {
    private final ExecutorService es = Executors.newWorkStealingPool();
    private final CoremodConfig config;
    private final CountDownLatch countDownLatch;
    private final String minecraftVersion;
    private final File modsDir;
    private final APIClient apiClient;

    private Starter(CoremodConfig config, CountDownLatch countDownLatch, String minecraftVersion, File modsDir) {
        this.config = config;
        this.countDownLatch = countDownLatch;
        this.minecraftVersion = minecraftVersion;
        this.modsDir = modsDir;
        apiClient = new APIClient(config);
    }

    static void start(File configFile, CountDownLatch countDownLatch, String minecraftVersion, File modsDir) throws IOException {
        CoremodConfig config = CoremodConfig.load(configFile);
        Starter starter = new Starter(config, countDownLatch, minecraftVersion, modsDir);
        SwingUtilities.invokeLater(starter::showLoginWindow);
    }

    private void showLoginWindow() {
        LoginFrame login = new LoginFrame(config);
        login.setOnLoginClickListener(() -> {
            login.setFreeze(true);
            login.setStatus("Logging in...");
            login.setErrorMessage("");
            CompletableFuture.supplyAsync(() -> apiClient.tryLogin(login.getEmail(), login.getPassword()), es)
                    .whenComplete((token, ex) -> SwingUtilities.invokeLater(() -> {
                        if (ex == null) {
                            login.setVisible(false);
                            login.dispose();
                            showProjectListWindow(token);
                        } else {
                            login.setErrorMessage(ex.toString());
                            login.setFreeze(false);
                        }
                    }));
        });
        login.setOnCancelClickListener(() -> {
            login.setVisible(false);
            login.dispose();
            countDownLatch.countDown();
        });
        login.setVisible(true);
    }

    private void showProjectListWindow(AccessToken token) {
        ProjectListFrame projectList = new ProjectListFrame();
        projectList.setStatusRight("Fetching projects...");
        projectList.setOnConfirmClickListener(() -> {
            projectList.setVisible(false);
            projectList.dispose();
            checkModConflict(token, projectList.getSelectedProjectId());
        });
        projectList.setOnCancelClickListener(() -> {
            projectList.setVisible(false);
            projectList.dispose();
            countDownLatch.countDown();
        });
        projectList.setVisible(true);
        CompletableFuture.supplyAsync(() -> apiClient.tryGetProjects(token, minecraftVersion), es)
                .whenComplete((projects, ex) -> SwingUtilities.invokeLater(() -> {
                    if (ex == null) {
                        projectList.setStatusLeft("Please select the project from above");
                        projectList.setStatusRight("");
                        projectList.addProjects(projects);
                    } else {
                        projectList.setErrorMessage(ex.toString());
                    }
                }));
    }

    private void checkModConflict(AccessToken token, String projectId) {
        if (!modsDir.exists()) {
            boolean result = modsDir.mkdirs();
            if (!result) {
                showError("Cannot create mods dir");
            }
        }

        ArrayList<File> conflictFiles = new ArrayList<>();
        File[] files = modsDir.listFiles();
        if (files == null) {
            showError("Cannot list up mods dir.");
            return;
        }
        for (File file : files) {
            if (!file.getName().equals("portal-" + projectId + ".jar")) {
                conflictFiles.add(file);
            }
        }

        if (conflictFiles.size() == 0) {
            startDownload(token, projectId, true);
        } else {
            ConflictFrame conflictWindow = new ConflictFrame(conflictFiles);
            conflictWindow.setOnKeepClickListener(() -> {
                conflictWindow.setVisible(false);
                conflictWindow.dispose();
                startDownload(token, projectId, false);
            });
            conflictWindow.setOnDeleteClickListener(() -> {
                conflictWindow.setVisible(false);
                conflictWindow.dispose();
                startDownload(token, projectId, true);
            });
            conflictWindow.setOnCancelClickListener(() -> {
                conflictWindow.setVisible(false);
                conflictWindow.dispose();
                countDownLatch.countDown();
            });
            conflictWindow.setVisible(true);
        }
    }

    private void startDownload(AccessToken token, String projectId, boolean beforeDeleteAll) {
        if (beforeDeleteAll) {
            File[] files = modsDir.listFiles();
            if (files == null) {
                showError("Cannot list up mods dir.");
                return;
            }
            for (File file : files) {
                boolean result = file.delete();
                if (!result) {
                    showError("Cannot delete conflicted mod file. " + file.getAbsolutePath());
                    return;
                }
            }
        }
        File target = new File(modsDir, "portal-" + projectId + ".jar");
        if (target.exists()) {
            boolean result = target.delete();
            if (!result) {
                showError("Previous artifact file cannot be removed. " + target.getAbsolutePath());
                return;
            }
        }
        DownloadFrame downloadFrame = new DownloadFrame();
        downloadFrame.setVisible(true);
        CompletableFuture.runAsync(() -> apiClient.tryStoreModJar(token, projectId, target), es)
                .whenComplete((v, ex) -> SwingUtilities.invokeLater(() -> {
                    downloadFrame.setVisible(false);
                    downloadFrame.dispose();
                    if (ex == null) {
                        countDownLatch.countDown();
                    } else {
                        showError(ex.toString());
                    }
                }));
    }

    private void showError(String message) {
        ErrorFrame errorFrame = new ErrorFrame();
        errorFrame.setErrorMessage(message);
        errorFrame.setOnOkClickListener(() -> {
            errorFrame.setVisible(false);
            errorFrame.dispose();
            countDownLatch.countDown();
        });
        errorFrame.setVisible(true);
    }
}
