/*
 * This file is part of Bellows, licensed under the ISC License.
 *
 * Copyright (c) 2014 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package info.faceland.bellows;

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
import org.nunnerycode.facecore.configuration.MasterConfiguration;
import org.nunnerycode.facecore.configuration.VersionedSmartConfiguration.VersionUpdateType;
import org.nunnerycode.facecore.configuration.VersionedSmartYamlConfiguration;
import org.nunnerycode.facecore.hilt.HiltItemStack;
import org.nunnerycode.facecore.plugin.FacePlugin;
import org.nunnerycode.facecore.utilities.TextUtils;
import org.nunnerycode.kern.apache.commons.lang3.text.WordUtils;
import org.nunnerycode.kern.shade.google.common.base.Joiner;

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
                "config.yml"), getResource("config.yml"), VersionUpdateType.BACKUP_AND_UPDATE);
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
                event.getInventory().setResult(hiltItemStack);
            }
        }

    }

}
