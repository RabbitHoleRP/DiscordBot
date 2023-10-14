package br.com.rabbithole.channel.text;

import br.com.rabbithole.dto.PlayerDTO;
import br.com.rabbithole.manager.AuthPlayerManager;
import br.com.rabbithole.publisher.AuthResponsePublisher;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AuthTextChannel {

    private final Guild guild;
    private final Dotenv env;
    private final PlayerDTO playerDTO;
    private final AuthPlayerManager playerManager;
    private final AuthResponsePublisher responsePublisher;

    public AuthTextChannel(Dotenv env, Guild guild, AuthPlayerManager playerManager, AuthResponsePublisher responsePublisher, PlayerDTO playerDTO) {
        this.env = env;
        this.guild = guild;
        this.playerDTO = playerDTO;
        this.playerManager = playerManager;
        this.responsePublisher = responsePublisher;
    }

    public void createTextChannel() {
        Category category;
        Member member = guild.retrieveMemberById(playerDTO.discordId()).complete();
        String channelName = "autenticação-%s".formatted(member.getUser().getGlobalName());

        if (hasRole(member)) {
            category = guild.getCategoryById(env.get("AUTHENTICATOR_ADM_CATEGORY"));
        } else {
            category = guild.getCategoryById(env.get("AUTHENTICATOR_CATEGORY"));
        }

        EnumSet<Permission> channelPermissions = EnumSet.of(Permission.VIEW_CHANNEL);

        TextChannel textChannel = Objects.requireNonNull(category).createTextChannel(channelName)
                .addPermissionOverride(member, channelPermissions, null)
                .addPermissionOverride(guild.getPublicRole(), null, channelPermissions)
                .complete();

        textChannel.sendMessage(member.getAsMention()).addEmbeds(embed()).addComponents(buttons()).queue();
        textChannel.delete().queueAfter(20, TimeUnit.SECONDS,unused -> {
            if (playerManager.contains(playerDTO.discordId())) {
                playerManager.remove(playerDTO.discordId());
                responsePublisher.publish("%s;%s;timeout".formatted(playerDTO.playerName(), playerDTO.ip()));
            }
        });
    }

    private MessageEmbed embed() {
        return new EmbedBuilder().setTitle("Autorização de Login")
                .setDescription("""
                        AAAAAAAAAAAAAAAAAAAAAAAAA
                        """)
                .build();
    }

    private ActionRow buttons() {
        return ActionRow.of(
                Button.of(ButtonStyle.SUCCESS, "auth_accepted", "Aceitar"),
                Button.of(ButtonStyle.DANGER, "auth_deny", "Não sou eu, denúnciar!")
        );
    }

    private boolean hasRole(Member member) {
        return member.getRoles().stream().map(Role::getName).anyMatch(env.get("ADM_ROLE")::equalsIgnoreCase);
    }
}
