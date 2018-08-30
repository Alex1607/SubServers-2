package net.ME1312.SubServers.Bungee.Host;

import net.ME1312.SubServers.Bungee.Library.Callback;
import net.ME1312.SubServers.Bungee.Library.Config.YAMLSection;
import net.ME1312.SubServers.Bungee.Library.Exception.InvalidTemplateException;
import net.ME1312.SubServers.Bungee.Library.Util;
import net.ME1312.SubServers.Bungee.Library.Version.Version;
import net.ME1312.SubServers.Bungee.SubAPI;

import java.io.File;
import java.util.*;

/**
 * SubCreator Layout Class
 */
public abstract class SubCreator {
    public static class ServerTemplate {
        private String name;
        private String nick = null;
        private boolean enabled;
        private String icon;
        private File directory;
        private ServerType type;
        private YAMLSection build;
        private YAMLSection options;

        /**
         * Create a SubCreator Template
         *
         * @param name Template Name
         * @param directory Template Directory
         * @param build Build Options
         * @param options Configuration Options
         */
        public ServerTemplate(String name, boolean enabled, String icon, File directory, YAMLSection build, YAMLSection options) {
            if (Util.isNull(name, enabled, directory, build, options)) throw new NullPointerException();
            if (name.contains(" ")) throw new InvalidTemplateException("Template names cannot have spaces: " + name);
            this.name = name;
            this.enabled = enabled;
            this.icon = icon;
            this.directory = directory;
            this.type = (build.contains("Server-Type"))?ServerType.valueOf(build.getRawString("Server-Type").toUpperCase()):ServerType.CUSTOM;
            this.build = build;
            this.options = options;
        }

        /**
         * Get the Name of this Template
         *
         * @return Template Name
         */
        public String getName() {
            return name;
        }

        /**
         * Get the Display Name of this Template
         *
         * @return Display Name
         */
        public String getDisplayName() {
            return (nick == null)?getName():nick;
        }

        /**
         * Sets the Display Name for this Template
         *
         * @param value Value (or null to reset)
         */
        public void setDisplayName(String value) {
            if (value == null || value.length() == 0 || getName().equals(value)) {
                this.nick = null;
            } else {
                this.nick = value;
            }
        }

        /**
         * Get the Enabled Status of this Template
         *
         * @return Enabled Status
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Set the Enabled Status of this Template
         *
         * @param value Value
         */
        public void setEnabled(boolean value) {
            enabled = value;
        }

        /**
         * Get the Item Icon for this Template
         *
         * @return Item Icon Name/ID
         */
        public String getIcon() {
            return icon;
        }

        /**
         * Set the Item Icon for this Template
         *
         * @param value Value
         */
        public void setIcon(String value) {
            icon = value;
        }

        /**
         * Get the Directory for this Template
         *
         * @return Directory
         */
        public File getDirectory() {
            return directory;
        }

        /**
         * Get the Type of this Template
         *
         * @return Template Type
         */
        public ServerType getType() {
            return type;
        }

        /**
         * Get the Build Options for this Template
         *
         * @return Build Options
         */
        public YAMLSection getBuildOptions() {
            return build;
        }

        /**
         * Get the Configuration Options for this Template
         *
         * @return Configuration Options
         */
        public YAMLSection getConfigOptions() {
            return options;
        }

        @Override
        public String toString() {
            YAMLSection tinfo = new YAMLSection();
            tinfo.set("enabled", isEnabled());
            tinfo.set("name", getName());
            tinfo.set("display", getDisplayName());
            tinfo.set("icon", getIcon());
            tinfo.set("type", getType().toString());
            return tinfo.toJSON();
        }
    }
    public enum ServerType {
        SPIGOT,
        VANILLA,
        FORGE,
        SPONGE,
        CUSTOM;

        @Override
        public String toString() {
            return super.toString().substring(0, 1).toUpperCase()+super.toString().substring(1).toLowerCase();
        }
    }

    /**
     * Create a SubServer
     *
     * @param player Player Creating
     * @param name Server Name
     * @param template Server Template
     * @param version Server Version
     * @param port Server Port Number
     * @return Success Status
     */
    public abstract boolean create(UUID player, String name, ServerTemplate template, Version version, int port, Callback<SubServer> callback);

    /**
     * Create a SubServer
     *
     * @param player Player Creating
     * @param name Server Name
     * @param template Server Template
     * @param version Server Version
     * @param port Server Port Number
     * @return Success Status
     */
    public boolean create(UUID player, String name, ServerTemplate template, Version version, int port) {
        return create(player, name, template, version, port, null);
    }

    /**
     * Create a SubServer
     *
     * @param name Server Name
     * @param template Server Template
     * @param version Server Version
     * @param port Server Port Number
     * @return Success Status
     */
    public boolean create(String name, ServerTemplate template, Version version, int port, Callback<SubServer> callback) {
        return create(null, name, template, version, port, callback);
    }

    /**
     * Create a SubServer
     *
     * @param name Server Name
     * @param template Server Template
     * @param version Server Version
     * @param port Server Port Number
     * @return Success Status
     */
    public boolean create(String name, ServerTemplate template, Version version, int port) {
        return create(null, name, template, version, port);
    }

    /**
     * Terminate All SubCreator Instances on this host
     */
    public abstract void terminate();

    /**
     * Terminate a SubCreator Instance
     *
     * @param name Name of current creating server
     */
    public abstract void terminate(String name);

    /**
     * Wait for All SubCreator Instances to Finish
     *
     * @throws InterruptedException
     */
    public abstract void waitFor() throws InterruptedException;

    /**
     * Wait for SubCreator to Finish
     *
     * @param name Name of current creating server
     * @throws InterruptedException
     */
    public abstract void waitFor(String name) throws InterruptedException;

    /**
     * Gets the host this creator belongs to
     *
     * @return Host
     */
    public abstract Host getHost();

    /**
     * Gets the Git Bash install directory
     *
     * @return Git Bash Directory
     */
    public abstract String getBashDirectory();

    /**
     * Gets all loggers for All SubCreator Instances
     *
     * @return SubCreator Loggers
     */
    public abstract List<SubLogger> getLogger();

    /**
     * Gets the Logger for a SubCreator Instance
     *
     * @param thread Thread ID
     * @return SubCreator Logger
     */
    public abstract SubLogger getLogger(String thread);


    /**
     * Get a list of currently reserved Server names
     *
     * @return Reserved Names
     */
    public abstract List<String> getReservedNames();

    /**
     * Check if a name has been reserved
     *
     * @param name Name to check
     * @return Reserved Status
     */
    public static boolean isReserved(String name) {
        boolean reserved = false;
        for (List<String> list : getAllReservedNames().values()) for (String reserve : list) {
            if (reserve.equalsIgnoreCase(name)) reserved = true;
        }
        return reserved;
    }

    /**
     * Get a list of all currently reserved Server names across all hosts
     *
     * @return All Reserved Names
     */
    public static Map<Host, List<String>> getAllReservedNames() {
        HashMap<Host, List<String>> names = new HashMap<Host, List<String>>();
        for (Host host : SubAPI.getInstance().getHosts().values()) names.put(host, host.getCreator().getReservedNames());
        return names;
    }

    /**
     * Gets the Templates that can be used in this SubCreator instance
     *
     * @return Template Map
     */
    public abstract Map<String, ServerTemplate> getTemplates();

    /**
     * Gets a SubCreator Template by name
     *
     * @param name Template Name
     * @return Template
     */
    public abstract ServerTemplate getTemplate(String name);

    /**
     * Reload SubCreator
     */
    public abstract void reload();
}
