package com.CSCI32006.CLI.Games;

import com.CSCI32006.CLI.SetupDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.support.SelectorItem;
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
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Command(command = "game search", description = "Search for a game by term")
    private void gameSearch(searchTerms terms) {
        //TODO Check if list is > 0
        //TODO move to Helper
        var list = SetupDatabase.getJdbcTemplate().query(
                "SELECT * FROM game ORDER BY ?;", new GameRowMapper(), terms.name()
        );
        var t = list.stream().map((game -> SelectorItem.of(game.getTitle(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
        t.add(SelectorItem.of("cancel", "-1"));
        SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                t, "Select a game", null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                .run(SingleItemSelector.SingleItemSelectorContext.empty());
        var x = Integer.parseInt(context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
    }
}
