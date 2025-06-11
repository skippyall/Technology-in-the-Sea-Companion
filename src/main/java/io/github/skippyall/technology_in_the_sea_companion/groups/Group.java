package io.github.skippyall.technology_in_the_sea_companion.groups;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Uuids;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Group {
    public static final Codec<Group> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(Group::getName),
                    Uuids.CODEC.listOf().fieldOf("players").forGetter(Group::getPlayers),
                    Base.CODEC.fieldOf("base").forGetter(Group::getBase)
            ).apply(instance, Group::new)
    );

    private List<UUID> players;
    private Base base;
    private String name;

    public Group(String name, Base base) {
        this(name, new ArrayList<>(), base);
    }

    public Group(String name, List<UUID> players, Base base) {
        this.name = name;
        this.players = new ArrayList<>(players);
        this.base = base;
    }

    public List<UUID> getPlayers() {
        return List.copyOf(players);
    }

    public void join(ServerPlayerEntity player) {
        players.add(player.getUuid());

        MinecraftServer server = player.getServer();
        for (UUID uuid : players) {
            if (server.getPlayerManager().getPlayer(uuid) != null) {
                server.getPlayerManager().getPlayer(uuid).sendMessage(
                        Text.of(player.getGameProfile().getName() + " joined this group").copy().setStyle(Style.EMPTY.withColor(Formatting.YELLOW))
                );
            }
        }
    }

    public boolean contains(ServerPlayerEntity player) {
        return players.contains(player.getUuid());
    }

    public Base getBase() {
        return base;
    }

    public String getName() {
        return name;
    }
}
