package info.faceland.bellows;

import com.google.common.base.Joiner;
import info.faceland.api.FacePlugin;
import info.faceland.config.VersionedFaceConfiguration;
import info.faceland.config.VersionedFaceYamlConfiguration;
import info.faceland.config.settings.FaceSettings;
import info.faceland.hilt.HiltItemStack;
import info.faceland.utils.TextUtils;
import org.apache.commons.lang.WordUtils;
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
import org.bukkit.inventory.ShapelessRecipe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BellowsPlugin extends FacePlugin {

    private VersionedFaceYamlConfiguration configYAML;
    private FaceSettings faceSettings;

    @Override
    public void preEnable() {
        configYAML =
                new VersionedFaceYamlConfiguration(new File(getDataFolder(), "config.yml"), getResource("config.yml"),
                                                    VersionedFaceConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
        if (configYAML.update()) {
            getLogger().info("Updating config.yml");
        }
        faceSettings = FaceSettings.loadFromFiles(configYAML);
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
            HiltItemStack hiltItemStack = new HiltItemStack(material);
            hiltItemStack.setName(name);
            hiltItemStack.setLore(lore);
            if (recipes.isSet(key + ".shaped-recipe")) {
                List<String> recipeList = recipes.getStringList(key + ".shaped-recipe");
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
                ShapedRecipe recipe = new ShapedRecipe(hiltItemStack);
                recipe.shape(recipeArray);
                for (Map.Entry<String, Material> e : materialMap.entrySet()) {
                    recipe.setIngredient(e.getKey().charAt(0), e.getValue());
                }
                Bukkit.addRecipe(recipe);
            } else {
                ConfigurationSection shapeless = recipes.getConfigurationSection(key + ".shapeless-recipe");
                if (shapeless == null) {
                    continue;
                }
                ShapelessRecipe recipe = new ShapelessRecipe(hiltItemStack);
                for (String ing : shapeless.getKeys(false)) {
                    Material m = Material.getMaterial(ing);
                    if (m == null || m == Material.AIR) {
                        continue;
                    }
                    recipe.addIngredient(shapeless.getInt(ing), m);
                }
                Bukkit.addRecipe(recipe);
            }
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

    class BellowsListener implements Listener {

        @EventHandler(priority = EventPriority.LOWEST)
        public void onCraftItem(PrepareItemCraftEvent event) {
            ItemStack is = event.getInventory().getResult();
            if (is == null || is.getType() == Material.AIR) {
                return;
            }
            String name = faceSettings.getString("config.normal-items." + is.getType().name() + ".name", "");
            List<String> lore = faceSettings.getStringList(
                    "config.normal-items." + is.getType().name() + ".lore", new ArrayList<String>());
            HiltItemStack hiltItemStack = new HiltItemStack(is);
            if (ChatColor.stripColor(hiltItemStack.getName()).equals(WordUtils.capitalizeFully(
                    Joiner.on(" ").skipNulls().join(is.getType().name().split("_"))))) {
                hiltItemStack.setName(TextUtils.color(name));
                hiltItemStack.setLore(TextUtils.color(lore));
                event.getInventory().setResult(hiltItemStack);
            }
        }

    }

}
