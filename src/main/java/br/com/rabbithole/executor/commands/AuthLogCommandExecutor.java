package br.com.rabbithole.executor.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AuthLogCommandExecutor implements CommandExecutor {
    @Override
    public String getName() {
        return "authlog";
    }

    @Override
    public String getDescription() {
        return "Mostrar Logs de Autenticação de algum jogador.";
    }

    @Override
    public boolean isSpecificGuild() {
        return true;
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "minecraft", "Pesquisar pelo Nome no Minecraft.", false),
                new OptionData(OptionType.USER, "discord", "Pesquisar pelo Nome no Discord.", false)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

    }

    private void searchByMinecraftName(final SlashCommandInteractionEvent event) {
        event.replyEmbeds(selectIntervalDateEmbed()).queue();
    }

    private void searchByDiscordId(final SlashCommandInteractionEvent event) {
        event.replyEmbeds(selectIntervalDateEmbed()).queue();
    }

    private List<OptionData> dateOptionsDataList() {
        List<OptionData> optionDataList = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate now = LocalDate.now();
        for (int i=0; i < 23; i++) {
            String dateDisplay = dateTimeFormatter.format(now.minusDays(i));
            optionDataList.add(new OptionData(OptionType.STRING, dateDisplay, dateDisplay));
        }
        return optionDataList;
    }

    @NotNull
    private MessageEmbed selectIntervalDateEmbed() {
        return null;
    }

    @NotNull
    private MessageEmbed logsEmbed() {
        return null;
    }
}
