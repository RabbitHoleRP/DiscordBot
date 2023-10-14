package br.com.rabbithole.repositories;

import br.com.rabbithole.common.core.sql.Database;
import br.com.rabbithole.common.core.sql.Query;
import br.com.rabbithole.dto.AccountDTO;

import java.sql.ResultSet;
import java.util.Optional;

public class AccountRepository {
    private final Database DATABASE;

    public AccountRepository(Database database) {
        DATABASE = database;
    }

    public void createTableIfNotExists() {
        String sqlQuery = """
                CREATE TABLE IF NOT EXISTS account (
                player_name VARCHAR(16) NOT NULL PRIMARY KEY,
                discord_id VARCHAR(100),
                first_ip VARCHAR(25),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                """;
        try(Query query = DATABASE.executeQuery(sqlQuery)) {
            query.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
    }

    public void save(AccountDTO accountDTO) {
        String sqlQuery = """
                INSERT INTO account (
                player_name,
                discord_id
                )
                VALUES (
                ?,
                ?
                );
                """;
        try(Query query = DATABASE.executeQuery(sqlQuery)) {
            query.getStatement().setString(1, accountDTO.playerName());
            query.getStatement().setString(2, accountDTO.discordId());
            query.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
    }

    public Optional<AccountDTO> findById(String playerName) {
        String sqlQuery = """
                SELECT * FROM account WHERE playerName = ?;
                """;
        Optional<AccountDTO> accountDTO;
        try(Query query = DATABASE.executeQuery(sqlQuery)) {
            query.getStatement().setString(1, playerName);
            ResultSet resultSet = query.executeQuery();
            if(resultSet.next()) {
                accountDTO = Optional.of(new AccountDTO(
                        resultSet.getString("playerName"),
                        resultSet.getString("discord_id"),
                        null,
                        resultSet.getTimestamp("created_at")
                ));
                return accountDTO;
            } else {
                return Optional.empty();
            }
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            return Optional.empty();
        }
    }

    public void updateDiscordId(AccountDTO accountDTO) {
        String sqlQuery = """
                UPDATE account 
                set discord_id = ? 
                where player_name = ?;
                """;
        try(Query query = DATABASE.executeQuery(sqlQuery)) {
            query.getStatement().setString(1, accountDTO.discordId());
            query.getStatement().setString(2, accountDTO.playerName());
            query.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
    }
}
