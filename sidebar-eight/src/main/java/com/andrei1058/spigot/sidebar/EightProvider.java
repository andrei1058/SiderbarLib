package com.andrei1058.spigot.sidebar;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Collection;

@SuppressWarnings("unused")
public class EightProvider extends SidebarProvider {

    private static SidebarProvider instance;

    private Field headerM, footerM;
    private Field scorePlayerName, scoreObjectiveName, scoreScore, scoreAction;

    @Override
    public Sidebar createSidebar(SidebarLine title, Collection<SidebarLine> lines, Collection<PlaceholderProvider> placeholderProviders) {
        return new EightSidebar(title, lines, placeholderProviders);
    }

    @Override
    public SidebarObjective createObjective(@NotNull WrappedSidebar sidebar, String name, boolean health, SidebarLine title, int type) {
        return ((EightSidebar)sidebar).createObjective(name, health ? IScoreboardCriteria.g : IScoreboardCriteria.b, title, type);
    }

    @Override
    public ScoreLine createScoreLine(WrappedSidebar sidebar, SidebarLine line, int score, String color) {
        return ((EightSidebar)sidebar).createScore(line, score, color);
    }

    public void sendScore(@NotNull WrappedSidebar sidebar, String playerName, int score) {
        if (sidebar.getHealthObjective() == null) return;
        PacketPlayOutScoreboardScore packetPlayOutScoreboardScore = new PacketPlayOutScoreboardScore();
        if (null == scorePlayerName){
            try {
                scorePlayerName = PacketPlayOutScoreboardScore.class.getDeclaredField("a");
                scorePlayerName.setAccessible(true);

                scoreObjectiveName = PacketPlayOutScoreboardScore.class.getDeclaredField("b");
                scoreObjectiveName.setAccessible(true);

                scoreScore = PacketPlayOutScoreboardScore.class.getDeclaredField("c");
                scoreScore.setAccessible(true);

                scoreAction = PacketPlayOutScoreboardScore.class.getDeclaredField("d");
                scoreAction.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        try {
            scorePlayerName.set(packetPlayOutScoreboardScore, playerName);
            scoreObjectiveName.set(packetPlayOutScoreboardScore, sidebar.getHealthObjective().getName());
            scoreScore.setInt(packetPlayOutScoreboardScore, score);
            scoreAction.set(packetPlayOutScoreboardScore, PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        for (Player player : sidebar.getReceivers()) {
            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
            playerConnection.sendPacket(packetPlayOutScoreboardScore);
        }
    }

    @Override
    public VersionedTabGroup createPlayerTab(WrappedSidebar sidebar, String identifier, SidebarLine prefix, SidebarLine suffix,
                                             PlayerTab.PushingRule pushingRule, PlayerTab.NameTagVisibility nameTagVisibility) {
        return new EightPlayerList(sidebar, identifier, prefix, suffix, pushingRule, nameTagVisibility);
    }

    @Override
    public void sendHeaderFooter(Player player, String header, String footer) {
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        try {
            if (null == headerM){
                headerM = PacketPlayOutPlayerListHeaderFooter.class.getDeclaredField("a");
                headerM.setAccessible(true);
            }
            if (null == footerM){
                footerM = PacketPlayOutPlayerListHeaderFooter.class.getDeclaredField("b");
                footerM.setAccessible(true);
            }
            headerM.set(packet, new ChatComponentText(header));
            footerM.set(packet, new ChatComponentText(footer));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

    public static SidebarProvider getInstance() {
        return null == instance ? instance = new EightProvider() : instance;
    }
}
