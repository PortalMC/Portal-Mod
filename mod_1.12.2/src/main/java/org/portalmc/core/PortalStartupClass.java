package org.portalmc.core;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLCallHook;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class PortalStartupClass implements IFMLCallHook {
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private File mcLocation;

    @Override
    public void injectData(Map<String, Object> data) {
        if (data.containsKey("mcLocation")) {
            mcLocation = (File) data.get("mcLocation");
        }
    }

    @Override
    public Void call() throws Exception {
        String minecraftVersion = ((String) ((HashMap) Launch.blackboard.get("launchArgs")).get("--version"));
        System.out.println("Portal coremod for Minecraft " + minecraftVersion + " has been launched.");
        File configFile = new File(new File(mcLocation, "config"), "portal.properties");
        Starter.start(configFile, countDownLatch, minecraftVersion, new File(mcLocation, "mods"));
        countDownLatch.await();
        return null;
    }
}
