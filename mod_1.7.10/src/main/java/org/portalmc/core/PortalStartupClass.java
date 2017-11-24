package org.portalmc.core;


import cpw.mods.fml.relauncher.IFMLCallHook;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@SuppressWarnings("unused")
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
        System.out.println("Portal coremod for Minecraft " + PortalCoremod.MC_VERSION + " has been launched.");
        File configFile = new File(new File(mcLocation, "config"), "portal.properties");
        Starter.start(configFile, countDownLatch, PortalCoremod.MC_VERSION, PortalCoremod.VERSION, new File(mcLocation, "mods"));
        countDownLatch.await();
        return null;
    }
}
