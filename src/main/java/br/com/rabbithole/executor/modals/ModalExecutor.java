package br.com.rabbithole.executor.modals;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

public interface ModalExecutor {

    String modalId();

    Modal modal();

    void execute(@NotNull ModalInteractionEvent event);
}
