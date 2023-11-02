package com.CSCI32006.CLI.Games;

import com.CSCI32006.CLI.Platforms.PlatformRowMapper;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        switch (terms) {
            case name -> {
                var alphabet = SetupDatabase.getJdbcTemplate().queryForList(
                        "SELECT DISTINCT(UPPER(LEFT(title, 1))) AS c FROM game ORDER BY c;", String.class
                );
                var t = alphabet.stream().map((character -> SelectorItem.of(character, character))).collect(Collectors.toList());
                t.add(SelectorItem.of("cancel", "cancel"));
                SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                        t, "Select a section", null);
                component.setResourceLoader(getResourceLoader());
                component.setTemplateExecutor(getTemplateExecutor());
                SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                        .run(SingleItemSelector.SingleItemSelectorContext.empty());
                var x = context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
                if("cancel".equals(x)) return;

                var list = SetupDatabase.getJdbcTemplate().query(
                        "SELECT * FROM game WHERE LOWER(LEFT(title, 1)) = LOWER(?);", new GameRowMapper(), x
                );
                var w = list.stream().map((game -> SelectorItem.of(game.getTitle(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
                w.add(SelectorItem.of("cancel", "-1"));
                SingleItemSelector<String, SelectorItem<String>> component2 = new SingleItemSelector<>(getTerminal(),
                        w, "Select a game", null);
                component2.setResourceLoader(getResourceLoader());
                component2.setTemplateExecutor(getTemplateExecutor());
                SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context2 = component2
                        .run(SingleItemSelector.SingleItemSelectorContext.empty());
                var z = Integer.parseInt(context2.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
            }
            case platform -> {
                var platforms = SetupDatabase.getJdbcTemplate().query(
                        "SELECT * FROM platform;", new PlatformRowMapper()
                );
                var t = platforms.stream().map((platform -> SelectorItem.of(platform.getName(), String.valueOf(platform.getPlatformId())))).collect(Collectors.toList());
                t.add(SelectorItem.of("cancel", "-1"));
                SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                        t, "Select a platform", null);
                component.setResourceLoader(getResourceLoader());
                component.setTemplateExecutor(getTemplateExecutor());
                SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                        .run(SingleItemSelector.SingleItemSelectorContext.empty());
                var x = Integer.parseInt(context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
                if(x == -1) return;

                var list = SetupDatabase.getJdbcTemplate().query(
                        "SELECT * FROM game JOIN game_on_platforms gop on game.gameid = gop.gameid WHERE platformid = ?;", new GameRowMapper(), x
                );
                var w = list.stream().map((game -> SelectorItem.of(game.getTitle(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
                w.add(SelectorItem.of("cancel", "-1"));
                SingleItemSelector<String, SelectorItem<String>> component2 = new SingleItemSelector<>(getTerminal(),
                        w, "Select a game", null);
                component2.setResourceLoader(getResourceLoader());
                component2.setTemplateExecutor(getTemplateExecutor());
                SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context2 = component2
                        .run(SingleItemSelector.SingleItemSelectorContext.empty());
                var z = Integer.parseInt(context2.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
            }
            //TODO add release data
            case developer -> {
                var developers = SetupDatabase.getJdbcTemplate().queryForList(
                        "SELECT DISTINCT(developername) AS c FROM game ORDER BY c;", String.class
                );
                var t = developers.stream().map((developer -> SelectorItem.of(developer, developer))).collect(Collectors.toList());
                t.add(SelectorItem.of("cancel", "cancel"));
                SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                        t, "Select a developer", null);
                component.setResourceLoader(getResourceLoader());
                component.setTemplateExecutor(getTemplateExecutor());
                SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                        .run(SingleItemSelector.SingleItemSelectorContext.empty());
                var x = context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
                if("cancel".equals(x)) return;

                var list = SetupDatabase.getJdbcTemplate().query(
                        "SELECT * FROM game WHERE developername = ?;", new GameRowMapper(), x
                );
                var w = list.stream().map((game -> SelectorItem.of(game.getTitle(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
                w.add(SelectorItem.of("cancel", "-1"));
                SingleItemSelector<String, SelectorItem<String>> component2 = new SingleItemSelector<>(getTerminal(),
                        w, "Select a game", null);
                component2.setResourceLoader(getResourceLoader());
                component2.setTemplateExecutor(getTemplateExecutor());
                SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context2 = component2
                        .run(SingleItemSelector.SingleItemSelectorContext.empty());
                var z = Integer.parseInt(context2.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
            }
            case genre -> {
                var genres = SetupDatabase.getJdbcTemplate().queryForList(
                        "SELECT DISTINCT(genretype) AS c FROM genre ORDER BY c;", String.class
                );
                var t = genres.stream().map((genre -> SelectorItem.of(genre, genre))).collect(Collectors.toList());
                t.add(SelectorItem.of("cancel", "cancel"));
                SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                        t, "Select a genre", null);
                component.setResourceLoader(getResourceLoader());
                component.setTemplateExecutor(getTemplateExecutor());
                SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                        .run(SingleItemSelector.SingleItemSelectorContext.empty());
                var x = context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
                if("cancel".equals(x)) return;

                var list = SetupDatabase.getJdbcTemplate().query(
                        "SELECT * FROM game JOIN game_belongs_to_genre gbtg on game.gameid = gbtg.gameid JOIN genre g on gbtg.genreid = g.genreid WHERE genretype = ?;", new GameRowMapper(), x
                );
                var w = list.stream().map((game -> SelectorItem.of(game.getTitle(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
                w.add(SelectorItem.of("cancel", "-1"));
                SingleItemSelector<String, SelectorItem<String>> component2 = new SingleItemSelector<>(getTerminal(),
                        w, "Select a game", null);
                component2.setResourceLoader(getResourceLoader());
                component2.setTemplateExecutor(getTemplateExecutor());
                SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context2 = component2
                        .run(SingleItemSelector.SingleItemSelectorContext.empty());
                var z = Integer.parseInt(context2.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
            }
        }

    }
}
