package br.com.rabbithole.subscribers;

import br.com.rabbithole.RedisLib;

import br.com.rabbithole.channel.text.AuthTextChannel;
import br.com.rabbithole.dto.PlayerDTO;
import br.com.rabbithole.manager.AuthPlayerManager;
import br.com.rabbithole.publisher.AuthResponsePublisher;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class AuthRequestSubscriber {

    private final Dotenv env;
    private final JDA jda;
    private final AuthPlayerManager authenticationManager;
    private final AuthResponsePublisher responsePublisher;

    public AuthRequestSubscriber(JDA jda, Dotenv env, AuthPlayerManager authenticationManager, AuthResponsePublisher responsePublisher) {
        this.env = env;
        this.jda = jda;
        this.authenticationManager = authenticationManager;
        this.responsePublisher = responsePublisher;
    }

    public void subscribe() {
        Jedis subscriber = RedisLib.getJedis().getResource();

        JedisPubSub pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                String[] keys = message.split("\\;");
                PlayerDTO playerDTO = new PlayerDTO(keys[0], keys[1], keys[2]);
                AuthTextChannel authTextChannel = new AuthTextChannel(env, jda.getGuildById(env.get("GUILD_ID")), authenticationManager, responsePublisher, playerDTO);
                authenticationManager.add(playerDTO.playerName(), playerDTO.discordId(), playerDTO.ip());
                authTextChannel.createTextChannel();
            }
        };
        subscriber.subscribe(pubSub, "authenticator.request");
    }
}
