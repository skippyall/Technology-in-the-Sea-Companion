package io.github.skippyall.technology_in_the_sea_companion.groups;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.skippyall.technology_in_the_sea_companion.StartManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.*;

public class GroupCommand {
    public static void groupCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, RegistrationEnvironment environment) {
        dispatcher.register(literal("technologygroup")
                .then(literal("start")
                        .then(literal("group_create_button")
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> {
                                    StartManager.formGroup(context.getSource().getWorld());
                                    return 0;
                                })
                        )
                )
                .then(literal("create")
                        .then(argument("name", StringArgumentType.string())
                                .executes(GroupCommand::createCommand)
                        )
                )
                .then(literal("join")
                        .then(argument("name", StringArgumentType.string())
                                .suggests(GroupCommand::suggestGroup)
                                .executes(GroupCommand::joinCommand)
                        )
                )
                .then(literal("list")
                        .executes(GroupCommand::listCommand)
                )
                .then(literal("teleport")
                        .then(argument("name", StringArgumentType.string())
                                .suggests(GroupCommand::suggestGroup)
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(GroupCommand::teleportCommand)
                        )
                )
        );
    }

    public static int createCommand(CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        if(GroupManager.getGroup(context.getSource().getServer(), name).isPresent()) {
            context.getSource().sendError(Text.of("Group " + name + " already exists"));
            return 0;
        }

        GroupManager.createGroup(context.getSource().getServer(), name);

        context.getSource().sendFeedback(() -> Text.of("Group " + name + " was created"), false);
        return 1;
    }

    public static int joinCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String name = StringArgumentType.getString(context, "name");

        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        Optional<Group> oldGroup = GroupManager.getGroup(player);
        if(oldGroup.isPresent()) {
            oldGroup.get().leave(player);
        }
        Optional<Group> group = GroupManager.getGroup(context.getSource().getServer(), name);
        if(group.isPresent()) {
            group.get().join(context.getSource().getPlayerOrThrow());
        } else {
            context.getSource().sendError(Text.of("Group " + name + " doesn't exist"));
        }

        return 1;
    }

    public static int teleportCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String name = StringArgumentType.getString(context, "name");
        Optional<Group> group = GroupManager.getGroup(context.getSource().getServer(), name);
        if(group.isPresent()) {
            group.get().getBase().teleport(context.getSource().getPlayerOrThrow());
            context.getSource().sendFeedback(() -> Text.literal("Teleported to base of " + name), true);
        } else {
            context.getSource().sendError(Text.of("Group " + name + " doesn't exist"));
        }

        return 0;
    }

    public static int listCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        StringBuilder text = new StringBuilder();
        for(Group group : GroupManager.getGroups(context.getSource().getServer())) {
            text.append(group.getName()).append("\n");
        }
        context.getSource().sendFeedback(() -> Text.literal(text.toString()), false);
        return 0;
    }

    public static CompletableFuture<Suggestions> suggestGroup(CommandContext<ServerCommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) {
        for (Group group : GroupManager.getGroups(commandContext.getSource().getServer())) {
            suggestionsBuilder.suggest(group.getName());
        }
        return suggestionsBuilder.buildFuture();
    }
}
