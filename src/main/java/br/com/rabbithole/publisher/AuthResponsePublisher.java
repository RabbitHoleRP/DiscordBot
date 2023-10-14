package br.com.rabbithole.publisher;

import br.com.rabbithole.RedisLib;
import redis.clients.jedis.Jedis;

public class AuthResponsePublisher {

    public void publish(String message) {
        try(Jedis publisher = RedisLib.getJedis().getResource()) {
            publisher.publish("authenticator.response", message);
        }
    }
}
