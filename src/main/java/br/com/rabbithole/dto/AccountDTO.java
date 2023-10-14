package br.com.rabbithole.dto;

import java.sql.Timestamp;

public record AccountDTO(String playerName, String discordId, String firstIp, Timestamp createdAt) {

}
