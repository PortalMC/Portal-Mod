package org.portalmc.core;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

@SuppressWarnings("unused")
public class PortalCoremodContainer extends DummyModContainer {
    public PortalCoremodContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.version = PortalCoremod.VERSION;
        meta.modId = Constants.METADATA_MODID;
        meta.name = Constants.METADATA_NAME;
        meta.authorList = Constants.METADATA_AUTHOR_LIST;
        meta.description = Constants.METADATA_DESCRIPTION;
        meta.url = Constants.METADATA_URL;
        meta.credits = Constants.METADATA_CREDITS;
        setEnabledState(true);
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }
}