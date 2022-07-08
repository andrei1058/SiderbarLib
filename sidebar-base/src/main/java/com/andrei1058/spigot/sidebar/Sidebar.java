package com.andrei1058.spigot.sidebar;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Sidebar {

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
    @SuppressWarnings("unused")
    void setLine(SidebarLine sidebarLine, int line);

    /**
     * Set scoreboard title.
     * It will send the update to players automatically.
     *
     * @param title content.
     */
    @SuppressWarnings("unused")
    void setTitle(SidebarLine title);

    /**
     * Refresh scoreboard placeholders.
     * Can be used async.
     */
    @SuppressWarnings("unused")
    void refreshPlaceholders();

    /**
     * Refresh scoreboard title.
     * Useful when you have an animated title.
     * Can be used async.
     */
    @SuppressWarnings("unused")
    void refreshTitle();

    /**
     * It is very important to call this when
     * a player logs out or before deleting a scoreboard.
     *
     * @param player uuid.
     */
    @SuppressWarnings("unused")
    void remove(Player player);

    /**
     * Apply scoreboard to a player.
     *
     * @param player user.
     */
    @SuppressWarnings("unused")
    void add(Player player);

    /**
     * Refresh animated lines. Title excluded.
     * Can be used async.
     */
    @SuppressWarnings("unused")
    void refreshAnimatedLines();

    /**
     * Remove a line from the scoreboard.
     * Ignored if line does not exist.
     * Line 0 is the first from bottom.
     * It will send the update to players automatically.
     *
     * @param line position. 0 is bottom.
     */
    @SuppressWarnings("unused")
    void removeLine(int line);

    /**
     * @return lines amount.
     */
    @SuppressWarnings("unused")
    int lineCount();

    /**
     * Get placeholder providers list.
     *
     * @return placeholder providers list.
     */
    List<PlaceholderProvider> getPlaceholders();

    /**
     * Update player health if shown with {@link #showPlayersHealth(SidebarLine, boolean)}.
     * @param player subject.
     * @param health amount.
     */
    @SuppressWarnings("unused")
    void setPlayerHealth(Player player, int health);

    /**
     * Hide players health previously shown with {@link #showPlayersHealth(SidebarLine, boolean)}.
     */
    @SuppressWarnings("unused")
    void hidePlayersHealth();

    /**
     * Show players name.
     * @param displayName text under player name.
     * @param list show health scale on tab.
     */
    @SuppressWarnings("unused")
    void showPlayersHealth(SidebarLine displayName, boolean list);

    /**
     * Create a new tab list layout group.
     * Players added to this group will have the same prefix-suffix.
     * @param identifier group identifier.
     * @param player initial member.
     * @param prefix prefix text or animation.
     * @param suffix suffix text or animation.
     * @param pushingRule how to manage pushing.
     * @return tab group instance.
     */
    @SuppressWarnings("unused")
    PlayerTab playerTabCreate(String identifier, @Nullable Player player, SidebarLine prefix, SidebarLine suffix, PlayerTab.PushingRule pushingRule);

    /**
     * Remove tab group form tab list by identifier.
     * @param identifier name.
     */
    @SuppressWarnings("unused")
    void removeTab(String identifier);

    /**
     * Refresh tab-list animations (if making use of {@link SidebarLineAnimated}).
     */
    @SuppressWarnings("unused")
    void playerTabRefreshAnimation();

    /**
     * Refresh bellow player-name animations (if making use of {@link SidebarLineAnimated)}
     * and if health is displayed via {@link #showPlayersHealth(SidebarLine, boolean)}.
     */
    @SuppressWarnings("unused")
    void playerHealthRefreshAnimation();

    /**
     * Add a new placeholder provider for sidebar lines.
     * @param placeholderProvider placeholder.
     */
    @SuppressWarnings("unused")
    void addPlaceholder(PlaceholderProvider placeholderProvider);

    /**
     * Remove a placeholder.
     *
     * @param placeholder placeholder to be removed.
     */
    @SuppressWarnings("unused")
    void removePlaceholder(String placeholder);
}
