package com.andrei1058.spigot.sidebar;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class SidebarProvider {

    /**
     * Create a new sidebar.
     *
     * @param title                scoreboard title.
     * @param lines                scoreboard lines.
     * @param placeholderProviders placeholders.
     * @return sb instance.
     */
    public abstract SidebarAPI createSidebar(SidebarLine title, Collection<SidebarLine> lines, Collection<PlaceholderProvider> placeholderProviders);


    public abstract SidebarObjective createObjective(String name, boolean health, SidebarLine title, int type);

    public abstract ScoreLine createScoreLine(WrappedSidebar sidebar, SidebarLine line, int score, String color);


    public abstract void sendScore(@NotNull WrappedSidebar sidebar, String playerName, int score);
}