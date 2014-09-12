package info.faceland.bellows;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextUtils {

    private static final Map<String, ChatColor> COLOR_MAP = new HashMap<>();

    static {
        COLOR_MAP.put("<black>", ChatColor.BLACK);
        COLOR_MAP.put("<african-american>", ChatColor.BLACK);
        COLOR_MAP.put("<dark blue>", ChatColor.DARK_BLUE);
        COLOR_MAP.put("<dblue>", ChatColor.DARK_BLUE);
        COLOR_MAP.put("<d blue>", ChatColor.DARK_BLUE);
        COLOR_MAP.put("<darkblue>", ChatColor.DARK_BLUE);
        COLOR_MAP.put("<dark green>", ChatColor.DARK_GREEN);
        COLOR_MAP.put("<dgreen>", ChatColor.DARK_GREEN);
        COLOR_MAP.put("<d green>", ChatColor.DARK_GREEN);
        COLOR_MAP.put("<darkgreen>", ChatColor.DARK_GREEN);
        COLOR_MAP.put("<dark aqua>", ChatColor.DARK_AQUA);
        COLOR_MAP.put("<daqua>", ChatColor.DARK_AQUA);
        COLOR_MAP.put("<d aqua>", ChatColor.DARK_AQUA);
        COLOR_MAP.put("<darkaqua>", ChatColor.DARK_AQUA);
        COLOR_MAP.put("<dark red>", ChatColor.DARK_RED);
        COLOR_MAP.put("<dred>", ChatColor.DARK_RED);
        COLOR_MAP.put("<d red>", ChatColor.DARK_RED);
        COLOR_MAP.put("<darkred>", ChatColor.DARK_RED);
        COLOR_MAP.put("<dark purple>", ChatColor.DARK_PURPLE);
        COLOR_MAP.put("<dpurple>", ChatColor.DARK_PURPLE);
        COLOR_MAP.put("<d purple>", ChatColor.DARK_PURPLE);
        COLOR_MAP.put("<darkpurple>", ChatColor.DARK_PURPLE);
        COLOR_MAP.put("<gold>", ChatColor.GOLD);
        COLOR_MAP.put("<gray>", ChatColor.GRAY);
        COLOR_MAP.put("<grey>", ChatColor.GRAY);
        COLOR_MAP.put("<dark gray>", ChatColor.DARK_GRAY);
        COLOR_MAP.put("<dgray>", ChatColor.DARK_GRAY);
        COLOR_MAP.put("<d gray>", ChatColor.DARK_GRAY);
        COLOR_MAP.put("<darkgray>", ChatColor.DARK_GRAY);
        COLOR_MAP.put("<dark grey>", ChatColor.DARK_GRAY);
        COLOR_MAP.put("<dgrey>", ChatColor.DARK_GRAY);
        COLOR_MAP.put("<d grey>", ChatColor.DARK_GRAY);
        COLOR_MAP.put("<darkgrey>", ChatColor.DARK_GRAY);
        COLOR_MAP.put("<blue>", ChatColor.BLUE);
        COLOR_MAP.put("<green>", ChatColor.GREEN);
        COLOR_MAP.put("<aqua>", ChatColor.AQUA);
        COLOR_MAP.put("<red>", ChatColor.RED);
        COLOR_MAP.put("<yellow>", ChatColor.YELLOW);
        COLOR_MAP.put("<white>", ChatColor.WHITE);
        COLOR_MAP.put("<magic>", ChatColor.MAGIC);
        COLOR_MAP.put("<bold>", ChatColor.BOLD);
        COLOR_MAP.put("<italic>", ChatColor.ITALIC);
        COLOR_MAP.put("<underline>", ChatColor.UNDERLINE);
        COLOR_MAP.put("<strike>", ChatColor.STRIKETHROUGH);
        COLOR_MAP.put("<reset>", ChatColor.RESET);
    }

    private TextUtils() {
        // do nothing
    }

    public static ChatColor convertTag(String string) {
        for (Map.Entry<String, ChatColor> entry : COLOR_MAP.entrySet()) {
            if (entry.getKey().equals(string)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static String convertTag(ChatColor chatColor) {
        for (Map.Entry<String, ChatColor> entry : COLOR_MAP.entrySet()) {
            if (entry.getValue() == chatColor) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static String color(String string) {
        String s = string;
        for (Map.Entry<String, ChatColor> entry : COLOR_MAP.entrySet()) {
            s = s.replace(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return s;
    }

    public static List<String> color(List<String> list) {
        List<String> strings = new ArrayList<>();
        for (String s : list) {
            strings.add(color(s));
        }
        return strings;
    }

    public static String args(String string, String[][] args) {
        String s = string;
        for (String[] arg : args) {
            s = s.replace(arg[0], arg[1]);
        }
        return s;
    }

    public static List<String> args(List<String> list, String[][] args) {
        List<String> newList = new ArrayList<>();
        for (String string : list) {
            String s = string;
            for (String[] arg : args) {
                s = s.replace(arg[0], arg[1]);
            }
            newList.add(s);
        }
        return newList;
    }

}
