package com.CSCI32006.CLI.Games;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.TerminalUIBuilder;
import org.springframework.shell.component.view.control.BoxView;
import org.springframework.shell.component.view.control.ButtonView;
import org.springframework.shell.component.view.control.ListView;
import org.springframework.shell.component.view.control.StatusBarView;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.component.view.event.KeyEvent;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.geom.HorizontalAlign;
import org.springframework.shell.geom.VerticalAlign;
import org.springframework.shell.standard.AbstractShellComponent;

import java.util.Arrays;
import java.util.List;

enum searchTerms {
    name,
    platform,
    releaseDate,
    developer,
    price,
    genre
}
@Command(group = "Collection Commands", interactionMode = InteractionMode.INTERACTIVE)
public class GameCommands extends AbstractShellComponent {

    @Command(command = "game search", description = "Search for a game")
    private void gameSearch() {
        TerminalUIBuilder builder = new TerminalUIBuilder(getTerminal());
        var terminal = builder.build();
        StatusBarView.StatusItem item1 = StatusBarView.StatusItem.of("Item1");

        Runnable action1 = () -> {};
        StatusBarView.StatusItem item2 = StatusBarView.StatusItem.of("Item2", action1);

        Runnable action2 = () -> {
            getTerminal().writer().println("HELLO");
        };
        ListView<String> view = new ListView<>(ListView.ItemStyle.RADIO);
        terminal.configure(view);
        terminal.setRoot(view, false);
        view.setItems(Arrays.asList("item1", "item2", "item3"));
        terminal.run();
    }
}
