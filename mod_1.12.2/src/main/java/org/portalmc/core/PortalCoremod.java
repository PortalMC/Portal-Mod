package org.portalmc.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions({Constants.PACKAGE_NAME})
public class PortalCoremod implements IFMLLoadingPlugin {
    static final String VERSION = "1.12.2-1";
    static final String MC_VERSION = "1.12.2";

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{
        };
    }

    @Override
    public String getModContainerClass() {
        return PortalCoremodContainer.class.getName();
    }

    @Override
    public String getSetupClass() {
        return "org.portalmc.core.PortalStartupClass";
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }
}
