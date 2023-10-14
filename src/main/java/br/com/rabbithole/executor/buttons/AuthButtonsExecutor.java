package br.com.rabbithole.executor.buttons;

import br.com.rabbithole.channel.text.AuthReportTextChannel;
import br.com.rabbithole.dto.PlayerDTO;
import br.com.rabbithole.manager.AuthPlayerManager;
import br.com.rabbithole.publisher.AuthResponsePublisher;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AuthButtonsExecutor implements ButtonExecutor {

    private final Dotenv env;
    private final JDA jda;

    private final AuthResponsePublisher authenticationResponsePublisher;
    private final AuthPlayerManager authenticationManager;

    public AuthButtonsExecutor(JDA jda, Dotenv env, AuthResponsePublisher authenticationResponsePublisher, AuthPlayerManager authenticationManager) {
        this.env = env;
        this.jda = jda;
        this.authenticationResponsePublisher = authenticationResponsePublisher;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public List<String> buttonIds() {
        return Arrays.asList(
                "auth_accepted",
                "auth_deny"
        );
    }

    @Override
    public void execute(@NotNull ButtonInteractionEvent event) {
        // Verifica o id do botão clicado e executa a função para o botão clicado.
        switch (Objects.requireNonNull(event.getButton().getId())) {
            case "auth_accepted" ->  acceptedButton(event);
            case "auth_deny" -> itsNotMeButton(event);
        }

        // Responde a interação para o discord com os botões desativados.
        event.editComponents(ActionRow.of(
                Button.of(ButtonStyle.SUCCESS, "auth_accepted", "Aceitar").asDisabled(),
                Button.of(ButtonStyle.DANGER, "auth_deny", "Não sou eu, denunciar!").asDisabled()
        )).queue();
    }

    private void acceptedButton(@NotNull ButtonInteractionEvent event) {
        User whoClicked = Objects.requireNonNull(event.getMember()).getUser();
        String whoseChannelIs = event.getChannel().getName().split("\\-")[1];

        // Verifica se quem clicou tem o mesmo nome da sala.
        if (!Objects.requireNonNull(whoClicked.getGlobalName()).equalsIgnoreCase(whoseChannelIs)) return;

        // Publica no redis que o usuário confirmou o login.
        if (authenticationManager.contains(whoClicked.getId())) {
            PlayerDTO player = authenticationManager.get(whoClicked.getId());
            authenticationResponsePublisher.publish("%s;%s;accepted".formatted(player.playerName(), player.ip()));
            authenticationManager.remove(whoClicked.getId());
        }
    }

    private void itsNotMeButton(@NotNull ButtonInteractionEvent event) {
        User whoClicked = Objects.requireNonNull(event.getMember()).getUser();
        String whoseChannelIs = event.getChannel().getName().split("\\-")[1];

        // Verifica se quem clicou tem o mesmo nome da sala.
        if (!Objects.requireNonNull(whoClicked.getGlobalName()).equalsIgnoreCase(whoseChannelIs)) return;

        // Publica no redis que não é o usuário logando.
        if (authenticationManager.contains(whoClicked.getId())) {
            PlayerDTO player = authenticationManager.get(whoClicked.getId());
            authenticationResponsePublisher.publish("%s;%s;deny".formatted(player.playerName(), player.ip()));
            new AuthReportTextChannel(env, jda.getGuildById(env.get("GUILD_ID")), player).createTextChannel();
            authenticationManager.remove(whoClicked.getId());
        }
    }
}
