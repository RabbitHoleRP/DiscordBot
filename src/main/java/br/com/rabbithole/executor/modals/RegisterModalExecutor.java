package br.com.rabbithole.executor.modals;

import br.com.rabbithole.core.builder.commands.generics.Del;
import br.com.rabbithole.core.builder.commands.string.gets.GetAllByPrefix;
import br.com.rabbithole.dto.AccountDTO;
import br.com.rabbithole.repositories.AccountRepository;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegisterModalExecutor implements ModalExecutor {

    private static final String MODAL_ID = "modal_register";

    private final AccountRepository accountRepository;

    public RegisterModalExecutor(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Modal modal() {
        TextInput codeInput = TextInput.create("modal_input_code", "Código", TextInputStyle.SHORT)
                .setPlaceholder("XXXXXX")
                .setRequiredRange(6,6)
                .build();
        return Modal.create(modalId(), "Registra Sua Conta Minecraft")
                .addActionRow(codeInput)
                .build();
    }

    @Override
    public String modalId() {
        return MODAL_ID;
    }

    @Override
    public void execute(@NotNull ModalInteractionEvent event) {
        String code = event.getValue("modal_input_code").getAsString();

        final Optional<Map<String, String>> authenticatorRedisMap = new GetAllByPrefix.Builder().setPrefix("authenticator").execute();

        authenticatorRedisMap.ifPresentOrElse(map -> {
            AtomicBoolean found = new AtomicBoolean(false);
            map.forEach((key, value) -> {
                if (code.equalsIgnoreCase(value)) {
                    found.set(true);
                    String playerName = key.split("\\.")[1];
                    String discordId = event.getUser().getId();
                    accountRepository.updateDiscordId(new AccountDTO(playerName, discordId, null, null));
                    new Del.Builder().setKey(key).execute();
                    event.reply("Conta registrada com sucesso!").setEphemeral(true).queue();
                }
            });
            if (!found.get()) {
                event.reply("Código errado ou expirou. Caso tenha expirado entre no servidor do Minecraft novamente para gerar outro!").setEphemeral(true).queue();
            }
        }, () -> {
            event.reply("Código errado ou expirou. Caso tenha expirado entre no servidor do Minecraft novamente para gerar outro!").setEphemeral(true).queue();
        });
    }
}
