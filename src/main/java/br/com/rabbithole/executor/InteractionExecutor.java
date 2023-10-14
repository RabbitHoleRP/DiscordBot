package br.com.rabbithole.executor;

import br.com.rabbithole.executor.buttons.ButtonExecutor;
import br.com.rabbithole.executor.commands.CommandExecutor;
import br.com.rabbithole.executor.modals.ModalExecutor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InteractionExecutor extends ListenerAdapter {

    private List<CommandExecutor> commandExecutors = new ArrayList<>();
    private List<ModalExecutor> modalExecutors = new ArrayList<>();
    private List<ButtonExecutor> buttonExecutors = new ArrayList<>();

    public InteractionExecutor(List<CommandExecutor> commandExecutorList, List<ModalExecutor> modalExecutorList, List<ButtonExecutor> buttonExecutorList) {
        this.commandExecutors = commandExecutorList;
        this.modalExecutors = modalExecutorList;
        this.buttonExecutors = buttonExecutorList;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        commandExecutors.forEach(commandExecutor -> {
            if (commandExecutor.getName().equalsIgnoreCase(event.getName())) {
                commandExecutor.execute(event);
            }
        });
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        modalExecutors.forEach(modalExecutor -> {
            if (modalExecutor.modalId().equalsIgnoreCase(event.getModalId())) {
                modalExecutor.execute(event);
            }
        });
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        buttonExecutors.forEach(buttonExecutor -> {
            if (buttonExecutor.buttonIds().contains(event.getButton().getId())) {
                buttonExecutor.execute(event);
            }
        });
    }
}
