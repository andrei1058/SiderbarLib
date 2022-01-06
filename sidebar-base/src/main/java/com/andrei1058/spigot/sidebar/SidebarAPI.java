package com.andrei1058.spigot.sidebar;

import org.bukkit.entity.Player;

import java.util.List;

public interface SidebarAPI {

    /**
     * Add a new line to the scoreboard.
     *
     * @param sidebarLine content.
     */
    void addLine(SidebarLine sidebarLine);

    /**
     * Set a line content.
     * Ignored if not exists.
     * It will send the update to players automatically.
     *
     * @param sidebarLine content.
     * @param line        position. 0 bottom.
     */
    void setLine(SidebarLine sidebarLine, int line);

    /**
     * Set scoreboard title.
     * It will send the update to players automatically.
     *
     * @param title content.
     */
    void setTitle(SidebarLine title);

    /**
     * Refresh scoreboard placeholders.
     * Can be used async.
     */
    void refreshPlaceholders();

    /**
     * Refresh scoreboard title.
     * Useful when you have an animated title.
     * Can be used async.
     */
    void refreshTitle();

    /**
     * It is very important to call this when
     * a player logs out or before deleting a scoreboard.
     *
     * @param player uuid.
     */
    void remove(Player player);

    /**
     * Apply scoreboard to a player.
     *
     * @param player user.
     */
    void add(Player player);

    /**
     * Refresh animated lines. Title excluded.
     * Can be used async.
     */
    void refreshAnimatedLines();

    /**
     * Remove a line from the scoreboard.
     * Ignored if line does not exist.
     * Line 0 is the first from bottom.
     * It will send the update to players automatically.
     *
     * @param line position. 0 is bottom.
     */
    void removeLine(int line);

    /**
     * @return lines amount.
     */
    int lineCount();

    /**
     * Remove a placeholder.
     *
     * @param placeholder placeholder to be removed.
     */
    void removePlaceholder(String placeholder);

    /**
     * Get placeholder providers list.
     *
     * @return placeholder providers list.
     */
    List<PlaceholderProvider> getPlaceholders();

    void refreshHealth(Player player, int health);

    void hidePlayersHealth();

    void showPlayersHealth(SidebarLine displayName, boolean list);

    /**
     * Hide the name tag on head of the player.
     * Usually used when drinking invisibility potions.
     */
    void playerListHideNameTag(Player player);

    /**
     * Show the name tag on head of the player.
     * Usually used when an invisibility potion has expired.
     */
    void playerListRestoreNameTag(Player player);

//    PlayerTab playerTabCreate(String identifier, Player player, SidebarLine prefix, SidebarLine suffix, boolean disablePushing);
//
//    PlayerTab playerTabCreate(String identifier, List<Player> player, SidebarLine prefix, SidebarLine suffix, boolean disablePushing);

//    void clearTab(String identifier);

    void addPlaceholder(PlaceholderProvider placeholderProvider);
}