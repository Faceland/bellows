package info.faceland.bellows;

import info.faceland.api.FacePlugin;
import info.faceland.facecore.shade.nun.ivory.config.VersionedIvoryConfiguration;
import info.faceland.facecore.shade.nun.ivory.config.VersionedIvoryYamlConfiguration;
import info.faceland.facecore.shade.nun.ivory.config.settings.IvorySettings;
import info.faceland.hilt.HiltItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        for (String key : configYAML.getConfigurationSection("recipes").getKeys(false)) {
            ConfigurationSection recipes = configYAML.getConfigurationSection("recipes");
            if (!recipes.isConfigurationSection(key)) {
                continue;
            }
            Material material = Material.getMaterial(key);
            if (material == null || material == Material.AIR) {
                continue;
            }
            String name = TextUtils.color(recipes.getString(key + ".name", ""));
            List<String> lore = TextUtils.color(recipes.getStringList(key + ".lore"));
            List<String> recipeList = recipes.getStringList(key + ".recipe");
            String[] recipeArray = recipeList.toArray(new String[recipeList.size()]);
            Map<String, Material> materialMap = new HashMap<>();
            ConfigurationSection cs = recipes.getConfigurationSection(key + ".ingredients");
            for (String mKey : cs.getKeys(false)) {
                Material m = Material.getMaterial(cs.getString(mKey));
                if (m == null || m == Material.AIR) {
                    continue;
                }
                materialMap.put(mKey, m);
            }
            HiltItemStack hiltItemStack = new HiltItemStack(material);
            hiltItemStack.setName(name);
            hiltItemStack.setLore(lore);
            ShapedRecipe recipe = new ShapedRecipe(hiltItemStack);
            recipe.shape(recipeArray);
            for (Map.Entry<String, Material> e : materialMap.entrySet()) {
                recipe.setIngredient(e.getKey().charAt(0), e.getValue());
            }
            Bukkit.addRecipe(recipe);
        }
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
