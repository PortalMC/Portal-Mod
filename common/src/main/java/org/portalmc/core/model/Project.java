package org.portalmc.core.model;

public class Project {

    public final String id;
    public final String name;
    public final String minecraftVersion;
    public final String forgeVersion;

    public Project(String id, String name, String minecraftVersion, String forgeVersion) {
        this.id = id;
        this.name = name;
        this.minecraftVersion = minecraftVersion;
        this.forgeVersion = forgeVersion;
    }
}
