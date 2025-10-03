package io.github.skippyall.technology_in_the_sea_companion.groups;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class Group {
    public static final Codec<Group> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(Group::getName),
                    Uuids.CODEC.listOf().fieldOf("players").forGetter(group -> List.copyOf(group.players)),
                    Base.CODEC.fieldOf("base").forGetter(Group::getBase)
            ).apply(instance, Group::new)
    );

    private Set<UUID> players;
    private Base base;
    private String name;

    public Group(String name, Base base) {
        this.name = name;
        this.players = new HashSet<>();
        this.base = base;
    }

    public Group(String name, List<UUID> players, Base base) {
        this.name = name;
        this.players = new HashSet<>(players);
        this.base = base;
    }

    public Collection<UUID> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public void join(ServerPlayerEntity player) {
        Optional<Group> oldGroup = GroupManager.getGroup(player);
        if(oldGroup.isPresent()) {
            oldGroup.get().leave(player);
        }

        broadcastMessage(player.getDisplayName().copy().append(Text.literal(" joined this group")), player.server);

        players.add(player.getUuid());
    }

    public void join(Collection<ServerPlayerEntity> joinPlayers) {
        for(ServerPlayerEntity joinPlayer : joinPlayers) {
            broadcastMessage(joinPlayer.getDisplayName().copy().append(Text.literal(" joined this group")), joinPlayer.server);
        }

        for(ServerPlayerEntity joinPlayer : joinPlayers) {
            players.add(joinPlayer.getUuid());
        }
    }

    public void leave(ServerPlayerEntity player) {
        players.remove(player.getUuid());
        broadcastMessage(player.getDisplayName().copy().append(" left this group"), player.server);
    }

    public void broadcastMessage(Text message, MinecraftServer server) {
        for (UUID uuid : players) {
            PlayerEntity otherPlayer = server.getPlayerManager().getPlayer(uuid);
            if (otherPlayer != null) {
                otherPlayer.sendMessage(message);
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
