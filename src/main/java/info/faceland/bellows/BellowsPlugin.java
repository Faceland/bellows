/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.faceland.bellows;

import com.tealcube.minecraft.bukkit.config.MasterConfiguration;
import com.tealcube.minecraft.bukkit.config.VersionedConfiguration;
import com.tealcube.minecraft.bukkit.config.VersionedSmartYamlConfiguration;
import com.tealcube.minecraft.bukkit.facecore.plugin.FacePlugin;
import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import com.tealcube.minecraft.bukkit.hilt.HiltItemStack;
import com.tealcube.minecraft.bukkit.kern.apache.commons.lang3.text.WordUtils;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Joiner;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.collect.Sets;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BellowsPlugin extends FacePlugin {

    private MasterConfiguration faceSettings;

    @Override
    public void enable() {
        VersionedSmartYamlConfiguration configYAML = new VersionedSmartYamlConfiguration(new File(getDataFolder(),
                "config.yml"), getResource("config.yml"), VersionedConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
        if (configYAML.update()) {
            getLogger().info("Updating config.yml");
        }
        faceSettings = new MasterConfiguration();
        faceSettings.load(configYAML);
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
            List<String> lore = color(recipes.getStringList(key + ".lore"));
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
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    private List<String> color(List<String> list) {
        List<String> ret = new ArrayList<>();
        for (String s : list) {
            ret.add(TextUtils.color(s));
        }
        return ret;
    }

    class BellowsListener implements Listener {

        @EventHandler(priority = EventPriority.LOWEST)
        public void onCraftItem(PrepareItemCraftEvent event) {
            ItemStack is = event.getInventory().getResult();
            if (is == null || is.getType() == Material.AIR) {
                return;
            }
            String name = faceSettings.getString("config.normal-items." + is.getType().name() + ".name");
            List<String> lore = faceSettings.getStringList("config.normal-items." + is.getType().name() + ".lore");
            HiltItemStack hiltItemStack = new HiltItemStack(is);
            if (ChatColor.stripColor(hiltItemStack.getName()).equals(WordUtils.capitalizeFully(
                    Joiner.on(" ").skipNulls().join(is.getType().name().split("_"))))) {
                hiltItemStack.setName(TextUtils.color(name));
                hiltItemStack.setLore(color(lore));
                if (is.getType() == Material.WOOD_AXE || is.getType() == Material.WOOD_SWORD ||
                    is.getType() == Material.STONE_AXE || is.getType() == Material.STONE_SWORD ||
                    is.getType() == Material.IRON_AXE || is.getType() == Material.IRON_SWORD ||
                    is.getType() == Material.GOLD_AXE || is.getType() == Material.GOLD_SWORD ||
                    is.getType() == Material.DIAMOND_AXE || is.getType() == Material.DIAMOND_SWORD) {
                    hiltItemStack.setItemFlags(Sets.newHashSet(ItemFlag.HIDE_ATTRIBUTES));
                }
                event.getInventory().setResult(hiltItemStack);
            }
        }

    }

}
