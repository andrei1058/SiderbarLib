package com.andrei1058.spigot.sidebar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class WrappedSidebar implements SidebarAPI {

    private final LinkedList<ScoreLine> lines = new LinkedList<>();
    public final LinkedList<Player> receivers = new LinkedList<>();
    private final LinkedList<PlaceholderProvider> placeholderProviders = new LinkedList<>();
    // indexing
    private final LinkedList<String> availableColors = new LinkedList<>();
    private final SidebarObjective sidebarObjective;
    private SidebarObjective healthObjective;
    private final ConcurrentHashMap<String, PlayerTab> teamList = new ConcurrentHashMap<>();


    public WrappedSidebar(@NotNull SidebarLine title, @NotNull Collection<SidebarLine> lines, Collection<PlaceholderProvider> placeholderProvider) {
        for (ChatColor chatColor : ChatColor.values()) {
            this.availableColors.add(chatColor.toString());
        }

        this.sidebarObjective = SidebarManager.getInstance().createObjective("Sidebar", false, title, 1);
        this.placeholderProviders.addAll(placeholderProvider);
        for (SidebarLine line : lines) {
            this.addLine(line);
        }
    }

    @Override
    public void setTitle(SidebarLine title) {
        this.sidebarObjective.setTitle(title);
    }

    @Override
    public void addPlaceholder(PlaceholderProvider placeholderProvider) {
        placeholderProviders.remove(placeholderProvider);
        placeholderProviders.add(placeholderProvider);
        for (ScoreLine line : lines) {
            if (!line.getLine().isHasPlaceholders()) {
                if (line.getLine() instanceof SidebarLineAnimated) {
                    for (String string : ((SidebarLineAnimated) line.getLine()).getLines()) {
                        if (string.contains(placeholderProvider.getPlaceholder())) {
                            line.getLine().setHasPlaceholders(true);
                            break;
                        }
                    }
                } else if (line.getLine().getLine().contains(placeholderProvider.getPlaceholder())) {
                    line.getLine().setHasPlaceholders(true);
                }
            }
        }
    }

    /**
     * @return -1 if no more lines can be added.
     */
    private int getAvailableScore() {
        if (this.lines.isEmpty()) return 0;
        if (this.lines.size() == 16) return -1;
        return this.lines.getFirst().getScore();
    }

    // sends score update packet
    // used when adding a line
    private static void scoreOffsetIncrease(@NotNull Collection<ScoreLine> lineCollections) {
        for (ScoreLine line : lineCollections) {
            line.setScore(line.getScore() + 1);
        }
    }

    // used when adding/ removing a line
    private void order() {
        Collections.sort(this.lines);
    }

    public void addLine(SidebarLine sidebarLine) {
        int score = getAvailableScore();
        if (score == -1) return;
        scoreOffsetIncrease(this.lines);
        String color = availableColors.get(0);
        availableColors.remove(0);
        ScoreLine s = SidebarManager.getInstance().createScoreLine(this, sidebarLine, score == 0 ? score : score - 1, color);
        s.sendCreate();
        this.lines.add(s);
        order();
    }

    @Override
    public void setLine(SidebarLine sidebarLine, int line) {
        if (line >= 0 && line < this.lines.size()) {
            ScoreLine s = this.lines.get(line);
            for (PlaceholderProvider placeholder : placeholderProviders) {
                if (sidebarLine.getLine().contains(placeholder.getPlaceholder())) {
                    sidebarLine.setHasPlaceholders(true);
                }
            }
            s.setLine(sidebarLine);
        }
    }

    @Override
    public void add(Player player) {
        sidebarObjective.sendCreate(player);
        this.lines.forEach(line -> line.sendCreate(player));
        if (healthObjective != null) {
            healthObjective.sendCreate(player);
            for (Map.Entry<String, PlayerTab> entry : teamList.entrySet()) {
                entry.getValue().sendCreate(player);
            }
        }
        this.receivers.add(player);
    }

    @Override
    public void refreshPlaceholders() {
        for (ScoreLine line : this.lines) {
            if (line.getLine().isHasPlaceholders()) {
                String content = line.getLine().getLine();
                for (PlaceholderProvider pp : this.placeholderProviders) {
                    if (content.contains(pp.getPlaceholder())) {
                        content = content.replace(pp.getPlaceholder(), pp.getReplacement());
                    }
                }
                line.setContent(content);
                line.sendUpdate();
            }
        }
    }

    @Override
    public void refreshTitle() {
        this.sidebarObjective.sendUpdate();
    }

    @Override
    public void refreshAnimatedLines() {
        for (ScoreLine line : lines) {
            if (line.getLine() instanceof SidebarLineAnimated) {
                if (line.getLine().isHasPlaceholders()) {
                    String content = line.getLine().getLine();
                    for (PlaceholderProvider pp : this.placeholderProviders) {
                        if (content.contains(pp.getPlaceholder())) {
                            content = content.replace(pp.getPlaceholder(), pp.getReplacement());
                        }
                    }
                    line.setContent(content);
                } else {
                    line.setContent(line.getLine().getLine());
                }
                line.sendUpdate();
            }
        }
    }

    // sends score update
    // used when removing a line
    private static void scoreOffsetDecrease(@NotNull Collection<ScoreLine> lineCollections) {
        lineCollections.forEach(c -> c.setScore(c.getScore() - 1));
    }

    @Override
    public void removeLine(int line) {
        if (line >= 0 && line < this.lines.size()) {
            ScoreLine scoreLine = this.lines.get(line);
            scoreLine.remove();
            this.lines.remove(line);
            scoreOffsetDecrease(this.lines.subList(line, this.lines.size()));
        }
    }

    @Override
    public int lineCount() {
        return lines.size();
    }

    @Override
    public void removePlaceholder(String placeholder) {
        placeholderProviders.removeIf(p -> p.getPlaceholder().equalsIgnoreCase(placeholder));
    }

    @Override
    public List<PlaceholderProvider> getPlaceholders() {
        return Collections.unmodifiableList(placeholderProviders);
    }

    @Override
    public void remove(Player player) {
        this.receivers.remove(player);
        teamList.forEach((b, c) -> c.sendRemove(player));
        lines.forEach(line -> line.sendRemove(player));
        this.sidebarObjective.sendRemove(player);
        if (this.healthObjective != null) {
            this.healthObjective.sendRemove(player);
        }
    }

    @Override
    public void refreshHealth(Player player, int health) {
        if (health < 0) {
            health = 0;
        }
        SidebarManager.getInstance().sendScore(this, player.getName(), health);
    }

    public SidebarObjective getHealthObjective() {
        return healthObjective;
    }

    public LinkedList<Player> getReceivers() {
        return receivers;
    }

    @Override
    public void hidePlayersHealth() {
        if (healthObjective != null) {
            this.receivers.forEach(receiver -> healthObjective.sendRemove(receiver));
            healthObjective = null;
        }
    }

    @Override
    public void showPlayersHealth(SidebarLine displayName, boolean list) {
        if (healthObjective == null) {
            healthObjective = SidebarManager.getInstance().createObjective(list ? "health" : "health2", true, displayName, 2);
            this.receivers.forEach(receiver -> healthObjective.sendCreate(receiver));
        } else {
            healthObjective.sendUpdate();
        }
    }

    @Deprecated(since = "asta ar trebui sa fie pe clasa de tab")
    @Override
    public void playerListHideNameTag(@NotNull Player player) {
        PlayerTab listed = teamList.get(player.getName());
        if (listed != null) {
            listed.hideNameTag(player);
        }
    }

    @Override
    public void playerListRestoreNameTag(@NotNull Player player) {
        PlayerTab listed = teamList.get(player.getName());
        if (listed != null) {
            listed.showNameTag(player);
        }
    }

    public SidebarObjective getSidebarObjective() {
        return sidebarObjective;
    }
}