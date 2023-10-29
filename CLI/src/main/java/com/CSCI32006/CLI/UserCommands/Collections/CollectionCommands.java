package com.CSCI32006.CLI.UserCommands.Collections;

import com.CSCI32006.CLI.Helper;
import com.CSCI32006.CLI.SetupDatabase;
import com.CSCI32006.CLI.UserCommands.Users.UserCommands;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.control.ListView;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.AbstractShellComponent;

import java.util.Optional;
import java.util.stream.Collectors;

@Command(group = "Collection Commands", interactionMode = InteractionMode.INTERACTIVE)
public class CollectionCommands extends AbstractShellComponent {

    private int getCollection(String title) {
        var id = UserCommands.getUser().getUserId();
        var list = SetupDatabase.getJdbcTemplate().query(
                "SELECT * FROM collection WHERE userid = ?;", new CollectionRowMapper(), id
        );
        var t = list.stream().map((collection -> SelectorItem.of(collection.getCollectionName(), String.valueOf(collection.getCollectionId())))).collect(Collectors.toList());
        t.add(SelectorItem.of("cancel", "-1"));
        SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                t, title, null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                .run(SingleItemSelector.SingleItemSelectorContext.empty());
        return Integer.parseInt(context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
    }

    private String getName(int collectionId) {
        return SetupDatabase.getJdbcTemplate().queryForObject(
                "SELECT collectionname FROM collection WHERE collectionid = ?;", String.class, collectionId
        );
    }
    @Command(command = "collection create", description = "creates a collection interactively")
    private void createCollection(String name) {
        SetupDatabase.getJdbcTemplate().update(
                "INSERT INTO collection (userid, collectionid, collectionname)" +
                        "VALUES (?, (SELECT COALESCE(MAX(collectionid) + 1, 0) FROM collection), ?)", UserCommands.getUser().getUserId(), name
        );
    }

    @Command(command = "collection delete", description = "deletes a collection interactively")
    private void deleteCollection() {
        var collectionId = getCollection("Select collection to delete");
        if(collectionId == -1) return;
        var name = getName(collectionId);
        SetupDatabase.getJdbcTemplate().update(
                "DELETE FROM collection WHERE collectionid = ?;", collectionId
        );
        getTerminal().writer().println("Collection " + name + " was deleted!");
        SetupDatabase.getJdbcTemplate().update(
                "DELETE FROM game_in_collection WHERE collectionid = ?;", collectionId
        );
    }

    @Command(command = "collection rename", description = "renames a collection interactively")
    private void renameCollection() {
        var collectionId = getCollection("Select collection to rename");
        if(collectionId == -1) return;
        getTerminal().writer().println("Collection " + getName(collectionId) + " selected: ");
        var name = getName(collectionId);
        var newName = Helper.getContextValue(false, "New Name: ", name, getTerminal(), getResourceLoader(), getTemplateExecutor());
        SetupDatabase.getJdbcTemplate().update(
                "UPDATE collection SET collectionname = ? WHERE collectionid = ?;", newName, collectionId);
        getTerminal().writer().println("Name successfully changed!");
    }

    @Command(command = "collection add", description = "adds a game to a collection through an interactive menu")
    private void addGameToCollection() {

    }
    @Command(command = "collection delete", description = "delete a game from a collection through an interactive menu")
    private void deleteGameFromCollection() {

    }
    @Command(command = "collection list", description = "lists collections in ascending order")
    private void listCollection() {
        var terminalUI = new TerminalUI(getTerminal());
        ListView<String> collections = new ListView<>();
        var id = UserCommands.getUser().getUserId();
        collections.setItems(SetupDatabase.getJdbcTemplate().queryForList(
                "SELECT collectionname FROM collection WHERE userid = ? ORDER BY collectionname;", String.class, id
        ));
        EventLoop eventLoop = terminalUI.getEventLoop();
        eventLoop.keyEvents().subscribe(event -> eventLoop.dispatch(ShellMessageBuilder.ofInterrupt()));
        terminalUI.setRoot(collections, false);
        terminalUI.run();
    }
}
