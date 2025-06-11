package io.github.skippyall.technology_in_the_sea_companion.groups;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.*;

public class GroupCommand {
    public static void groupCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("technologygroup")
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
                        .then(literal("teleport")
                                .then(argument("name", StringArgumentType.string())
                                        .suggests(GroupCommand::suggestGroup)
                                        .executes(GroupCommand::teleportCommand)
                                )
                        )
        );
    }

    public static int createCommand(CommandContext<ServerCommandSource> context) {
        GroupManager.createGroup(context.getSource().getServer(), StringArgumentType.getString(context, "name"));
        return 0;
    }

    public static int joinCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String name = StringArgumentType.getString(context, "name");
        Optional<Group> group = GroupManager.getGroup(context.getSource().getServer(), name);
        if(group.isPresent()) {
            group.get().join(context.getSource().getPlayerOrThrow());
        } else {
            context.getSource().sendMessage(Text.of("Group " + name + " doesn't exist"));
        }

        return 0;
    }

    public static int teleportCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String name = StringArgumentType.getString(context, "name");
        Optional<Group> group = GroupManager.getGroup(context.getSource().getServer(), name);
        if(group.isPresent()) {
            group.get().getBase().teleport(context.getSource().getPlayerOrThrow());
        } else {
            context.getSource().sendMessage(Text.of("Group " + name + " doesn't exist"));
        }

        return 0;
    }

    public static CompletableFuture<Suggestions> suggestGroup(CommandContext<ServerCommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) {
        for (Group group : GroupManager.getGroups(commandContext.getSource().getServer())) {
            suggestionsBuilder.suggest(group.getName());
        }
        return suggestionsBuilder.buildFuture();
    }
}
