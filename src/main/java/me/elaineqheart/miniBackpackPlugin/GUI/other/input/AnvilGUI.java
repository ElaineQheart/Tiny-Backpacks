package me.elaineqheart.miniBackpackPlugin.GUI.other.input;

import org.bukkit.entity.Player;

@FunctionalInterface // an interface with just a single abstract method
public interface AnvilGUI {

    void execute(Player p, String input);

}
