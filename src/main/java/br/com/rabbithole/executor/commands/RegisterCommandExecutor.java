package br.com.rabbithole.executor.commands;

import br.com.rabbithole.executor.modals.RegisterModalExecutor;
import br.com.rabbithole.repositories.AccountRepository;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class RegisterCommandExecutor implements CommandExecutor {

    private AccountRepository accountRepository;

    public RegisterCommandExecutor(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public String getName() {
        return "registrar";
    }

    @Override
    public String getDescription() {
        return "Registra seu Discord com a conta Minecraft.";
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
    public void execute(SlashCommandInteractionEvent event) {
        event.replyModal(new RegisterModalExecutor(accountRepository).modal()).queue();
    }
}
