package br.com.rabbithole.executor.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.ArrayList;
import java.util.List;

public interface CommandExecutor {

    void execute(SlashCommandInteractionEvent event);

    String getName();
    String getDescription();

    /**
     * Only appears in guilds wich have been specified using thre id.
     *
     * @return whether the command is guild only for specific guilds
     */
    boolean isSpecificGuild();

    /**
     * Whether the command can appear in dms or not.
     *
     * @return whether the command can appear in dms or not
     */
    boolean isGuildOnly();

    default List<OptionData> getOptions() {
        return new ArrayList<>();
    }

    default CommandData getCommandData() {
        return new CommandDataImpl(getName(), getDescription())
                .setGuildOnly(isGuildOnly())
                .addOptions(getOptions());
    }
}
