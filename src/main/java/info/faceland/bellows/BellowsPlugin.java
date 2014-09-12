package info.faceland.bellows;

import info.faceland.api.FacePlugin;
import info.faceland.facecore.shade.nun.ivory.config.VersionedIvoryConfiguration;
import info.faceland.facecore.shade.nun.ivory.config.VersionedIvoryYamlConfiguration;
import info.faceland.facecore.shade.nun.ivory.config.settings.IvorySettings;
import info.faceland.hilt.HiltItemStack;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BellowsPlugin extends FacePlugin {

    private VersionedIvoryYamlConfiguration configYAML;
    private IvorySettings ivorySettings;

    @Override
    public void preEnable() {
        configYAML =
                new VersionedIvoryYamlConfiguration(new File(getDataFolder(), "config.yml"), getResource("config.yml"),
                                                    VersionedIvoryConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
        if (configYAML.update()) {
            getLogger().info("Updating config.yml");
        }
        ivorySettings = IvorySettings.loadFromFiles(configYAML);
    }

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(new BellowsListener(), this);
    }

    @Override
    public void postEnable() {

    }

    @Override
    public void preDisable() {

    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void postDisable() {

    }

    public VersionedIvoryYamlConfiguration getConfigYAML() {
        return configYAML;
    }

    class BellowsListener implements Listener {

        @EventHandler(priority = EventPriority.LOWEST)
        public void onCraftItem(CraftItemEvent event) {
            String name = ivorySettings.getString("config." + event.getCurrentItem().getType().name() + ".name", "");
            List<String> lore = ivorySettings.getStringList(
                    "config." + event.getCurrentItem().getType().name() + ".lore", new ArrayList<String>());
            HiltItemStack hiltItemStack = new HiltItemStack(event.getCurrentItem());
            if (hiltItemStack.getName().equals("")) {
                hiltItemStack.setName(name);
                hiltItemStack.setLore(lore);
                event.setCurrentItem(hiltItemStack);
            }
        }

    }

}
