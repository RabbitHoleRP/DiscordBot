package br.com.rabbithole;

import br.com.rabbithole.common.core.sql.Database;
import br.com.rabbithole.configurations.RedisConfig;
import br.com.rabbithole.executor.InteractionExecutor;
import br.com.rabbithole.executor.buttons.AuthButtonsExecutor;
import br.com.rabbithole.executor.buttons.ButtonExecutor;
import br.com.rabbithole.executor.commands.AuthLogCommandExecutor;
import br.com.rabbithole.executor.commands.CommandExecutor;
import br.com.rabbithole.executor.commands.RegisterCommandExecutor;
import br.com.rabbithole.executor.modals.RegisterModalExecutor;
import br.com.rabbithole.executor.modals.ModalExecutor;
import br.com.rabbithole.manager.AuthPlayerManager;
import br.com.rabbithole.publisher.AuthResponsePublisher;
import br.com.rabbithole.repositories.AccountRepository;
import br.com.rabbithole.subscribers.AuthRequestSubscriber;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final Dotenv env = Dotenv.load();

    private static JDA jda;

    private final static List<CommandExecutor> commandExecutors = new ArrayList<>();
    private final static List<ModalExecutor> modalExecutors = new ArrayList<>();
    private final static List<ButtonExecutor> buttonExecutors = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {

        // Conexão com o banco de dados.
        Database database = new Database(
                "localhost",
                3306,
                "rabbithole",
                "root",
                "123456"
        );

        // Conexão com o redis.
        RedisLib.init(new RedisConfig(
                "discord_bot",
                false,
                "redis-16195.c91.us-east-1-3.ec2.cloud.redislabs.com",
                16195,
                "default",
                "hj3U1jVqugXKkkBwSfg9U3yXw9Rw0Swz",
                4)
        );

        jda = JDABuilder.createDefault(env.get("TOKEN"),
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MEMBERS)
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(new InteractionExecutor(commandExecutors, modalExecutors, buttonExecutors))
                .build();

        AccountRepository accountRepository = new AccountRepository(database);
        AuthResponsePublisher authResponsePublisher = new AuthResponsePublisher();
        AuthPlayerManager authenticationManager = new AuthPlayerManager();
        AuthRequestSubscriber authenticationRequestSubscriber = new AuthRequestSubscriber(jda, env, authenticationManager, authResponsePublisher);

        registerModal(accountRepository);
        registerButton(authResponsePublisher, authenticationManager);
        registerCommands(accountRepository);

        accountRepository.createTableIfNotExists();
        authenticationRequestSubscriber.subscribe();
    }

    private static void registerCommands(AccountRepository accountRepository) {
        commandExecutors.add(new RegisterCommandExecutor(accountRepository));
        commandExecutors.add(new AuthLogCommandExecutor());
    }

    private static void registerModal(AccountRepository accountRepository) {
        modalExecutors.add(new RegisterModalExecutor(accountRepository));
    }

    private static void registerButton(AuthResponsePublisher authResponsePublisher, AuthPlayerManager authPlayerManager) {
        buttonExecutors.add(new AuthButtonsExecutor(jda, env, authResponsePublisher, authPlayerManager));
    }
}
