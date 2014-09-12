package info.faceland.bellows;

import info.faceland.api.FacePlugin;
import info.faceland.facecore.shade.nun.ivory.config.VersionedIvoryConfiguration;
import info.faceland.facecore.shade.nun.ivory.config.VersionedIvoryYamlConfiguration;
import info.faceland.facecore.shade.nun.ivory.config.settings.IvorySettings;
import info.faceland.hilt.HiltItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

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
        public void onCraftItem(PrepareItemCraftEvent event) {
            ItemStack is = event.getInventory().getResult();
            if (is == null || is.getType() == Material.AIR) {
                return;
            }
            String name = ivorySettings.getString("config.normal-items." + is.getType().name() + ".name", "");
            List<String> lore = ivorySettings.getStringList(
                    "config.normal-items." + is.getType().name() + ".lore", new ArrayList<String>());
            HiltItemStack hiltItemStack = new HiltItemStack(is);
            if (ChatColor.stripColor(hiltItemStack.getName()).equals("")) {
                hiltItemStack.setName(TextUtils.color(name));
                hiltItemStack.setLore(TextUtils.color(lore));
                event.getInventory().setResult(hiltItemStack);
            }
        }

    }

}
