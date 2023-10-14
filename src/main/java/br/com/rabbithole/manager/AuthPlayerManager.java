package br.com.rabbithole.manager;


import br.com.rabbithole.dto.PlayerDTO;
import io.github.cdimascio.dotenv.Dotenv;
import jdk.jfr.Category;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.*;
import java.util.function.Predicate;

public class AuthPlayerManager {

    private final Map<String, PlayerDTO> playerMap = new HashMap<>();

    public void add(String playerName, String discordId, String ip) {
        playerMap.put(discordId, new PlayerDTO(playerName, discordId, ip));
    }

    public void remove(String discord) {
        playerMap.remove(discord);
    }

    public boolean contains(String discord) {
        return playerMap.containsKey(discord);
    }

    public PlayerDTO get(String discord) {
        return playerMap.get(discord);
    }

    public List<PlayerDTO> filter(Predicate<String> predicate) {
        final List<PlayerDTO> filteredList = new ArrayList<>();
        for (Map.Entry<String, PlayerDTO> entry : playerMap.entrySet()) {
            if (predicate.test(entry.getKey())) {
                filteredList.add(entry.getValue());
            }
        }
        return filteredList;
    }
}
