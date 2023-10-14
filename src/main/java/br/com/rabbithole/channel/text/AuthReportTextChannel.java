package br.com.rabbithole.channel.text;

import br.com.rabbithole.dto.PlayerDTO;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.EnumSet;
import java.util.Objects;

public class AuthReportTextChannel {

    private final Guild guild;
    private final Dotenv env;
    private final PlayerDTO playerDTO;

    public AuthReportTextChannel(Dotenv env, Guild guild, PlayerDTO playerDTO) {
        this.env = env;
        this.guild = guild;
        this.playerDTO = playerDTO;
    }

    public void createTextChannel() {
        Category category = guild.getCategoryById(env.get("AUTHENTICATOR_REPORT_CATEGORY"));

        Member member = guild.retrieveMemberById(playerDTO.discordId()).complete();
        String channelName = "denúncia-autenticação-%s".formatted(member.getUser().getGlobalName());


        EnumSet<Permission> channelPermissions = EnumSet.of(Permission.VIEW_CHANNEL);

        TextChannel textChannel = Objects.requireNonNull(category).createTextChannel(channelName)
                .addPermissionOverride(member, channelPermissions, null)
                .addPermissionOverride(guild.getPublicRole(), null, channelPermissions)
                .complete();

        textChannel.sendMessage(member.getAsMention()).addEmbeds(embed()).queue();
    }

    private MessageEmbed embed() {
        return new EmbedBuilder().setTitle("Denúncia de Login")
                .setDescription("""
                        AAAAAAAAAAAAAAAAAAAAAAAAA
                        """)
                .build();
    }
}
