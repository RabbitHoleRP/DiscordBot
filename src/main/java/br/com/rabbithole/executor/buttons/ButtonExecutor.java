package br.com.rabbithole.executor.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ButtonExecutor {

    List<String> buttonIds();
    void execute(@NotNull ButtonInteractionEvent event);
}
