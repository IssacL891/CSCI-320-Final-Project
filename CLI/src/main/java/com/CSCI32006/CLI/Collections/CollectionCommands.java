package com.CSCI32006.CLI.Collections;

import com.CSCI32006.CLI.Games.GameRowMapper;
import com.CSCI32006.CLI.Helper;
import com.CSCI32006.CLI.SetupDatabase;
import com.CSCI32006.CLI.Users.UserCommands;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.AbstractShellComponent;

import java.util.Optional;
import java.util.stream.Collectors;

@Command(group = "Collection Commands", interactionMode = InteractionMode.INTERACTIVE)
public class CollectionCommands extends AbstractShellComponent {

    private int getCollection(String title) {
        //TODO Check if list is > 0
        //TODO move to helper class
        //TODO generalize for all tables
        var id = UserCommands.getUser().getUserId();
        var list = SetupDatabase.getJdbcTemplate().query(
                "SELECT collection.userid, collection.collectionid, collectionname,\n" +
                        "COUNT(gic.gameid) AS countOfGames, coalesce(SUM(hour) + SUM(minutes) / 60, 0) AS hours,\n" +
                        "coalesce(SUM(minutes) % 60, 0) AS minutes\n" +
                        "FROM collection\n" +
                        "LEFT JOIN game_in_collection gic\n" +
                        "on collection.collectionid = gic.collectionid\n" +
                        "LEFT JOIN user_played_game upg on collection.userid = upg.userid\n" +
                        "WHERE collection.userid = ?\n" +
                        "group by collection.userid, collection.collectionid\n", new CollectionDisplayRowMapper(), id
        );
        var t = list.stream().map((collectionDisplay -> SelectorItem.of(collectionDisplay.toString(), String.valueOf(collectionDisplay.getCollectionId())))).collect(Collectors.toList());
        t.add(SelectorItem.of("cancel", "-1"));
        SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                t, title, null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                .run(SingleItemSelector.SingleItemSelectorContext.empty());
        return Integer.parseInt(context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
    }

    private int getGameFromCollection(String title, int collectionId) {
        var list = SetupDatabase.getJdbcTemplate().query(
                "SELECT * FROM game WHERE gameid IN (SELECT gameid FROM game_in_collection WHERE collectionid = ?);", new GameRowMapper(), collectionId
        );
        var t = list.stream().map((game -> SelectorItem.of(game.getTitle(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
        t.add(SelectorItem.of("cancel", "-1"));
        SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                t, title, null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                .run(SingleItemSelector.SingleItemSelectorContext.empty());
        return Integer.parseInt(context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
    }

    private int getGame() {
        //TODO Check if list is > 0
        //TODO move to Helper
        var list = SetupDatabase.getJdbcTemplate().query(
                "SELECT * FROM game;", new GameRowMapper()
        );
        var t = list.stream().map((game -> SelectorItem.of(game.getTitle(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
        t.add(SelectorItem.of("cancel", "-1"));
        SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                t, "Select a game", null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                .run(SingleItemSelector.SingleItemSelectorContext.empty());
        return Integer.parseInt(context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
    }
    private String getCollectionName(int collectionId) {
        //TODO move to Helper
        //TODO give response if collection doesn't exist
        return SetupDatabase.getJdbcTemplate().queryForObject(
                "SELECT collectionname FROM collection WHERE collectionid = ?;", String.class, collectionId
        );
    }
    private String getGameName(int gameId) {
        //TODO Move to Helper
        //TODO give response if collection doesn't exist
        return SetupDatabase.getJdbcTemplate().queryForObject(
                "SELECT title FROM game WHERE gameid = ?;", String.class, gameId
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
        //TODO Check if list > 0
        var collectionId = getCollection("Select collection to delete");
        if(collectionId == -1) return;
        var name = getCollectionName(collectionId);
        SetupDatabase.getJdbcTemplate().update(
                "DELETE FROM game_in_collection WHERE collectionid = ?;", collectionId
        );
        SetupDatabase.getJdbcTemplate().update(
                "DELETE FROM collection WHERE collectionid = ?;", collectionId
        );
        getTerminal().writer().println("Collection " + name + " was deleted!");
    }

    @Command(command = "collection rename", description = "renames a collection interactively")
    private void renameCollection() {
        //TODO Check if list > 0
        var collectionId = getCollection("Select collection to rename");
        if(collectionId == -1) return;
        getTerminal().writer().println("Collection " + getCollectionName(collectionId) + " selected: ");
        var name = getCollectionName(collectionId);
        var newName = Helper.getContextValue(false, "New Name: ", name, getTerminal(), getResourceLoader(), getTemplateExecutor());
        SetupDatabase.getJdbcTemplate().update(
                "UPDATE collection SET collectionname = ? WHERE collectionid = ?;", newName, collectionId);
        getTerminal().writer().println("Name successfully changed!");
    }

    @Command(command = "collection add game", description = "adds a game to a collection through an interactive menu")
    private void addGameToCollection() {
        //TODO Check if game already added
        var collectionId = getCollection("Select collection to add to");
        if(collectionId == -1) return;
        var gameId = getGame();
        if(gameId == -1) return;
        try {
            SetupDatabase.getJdbcTemplate().queryForObject(
                    "SELECT gameid FROM game_on_platforms " +
                            "JOIN user_owns_platforms uop " +
                            "on game_on_platforms.platformid = uop.platformid " +
                            "WHERE gameid = ? AND userid = ?", Integer.class, gameId, UserCommands.getUser().getUserId()
            );
        } catch (EmptyResultDataAccessException e) {
            getTerminal().writer().println("You do not own the platform the game is on.");
        }

        SetupDatabase.getJdbcTemplate().update(
                "INSERT INTO game_in_collection (gameid, collectionid, starrating) VALUES(?, ?, ?)", gameId, collectionId, -1
        );
        getTerminal().writer().println("Successfully added game!");
    }
    @Command(command = "collection delete game", description = "delete a game from a collection through an interactive menu")
    private void deleteGameFromCollection() {
        //TODO Check if game exists
        //TODO #12
        var collectionId = getCollection("Select collection to add to");
        if(collectionId == -1) return;
        var gameId = getGameFromCollection("Select Game", collectionId);
        if(gameId == -1) return;
        SetupDatabase.getJdbcTemplate().update(
                "DELETE FROM game_in_collection WHERE gameid = ? AND collectionid = ?", gameId, collectionId
        );
        getTerminal().writer().println("Successfully deleted " + getGameName(gameId) + "!");
    }
    @Command(command = "collection list", description = "lists collections in ascending order")
    private void listCollection() {
        //TODO Check if list > 0
        var collectionId = getCollection("List of collections");
        if(collectionId == -1) return;
        getGameFromCollection("List of games in collection " + getCollectionName(collectionId), collectionId);
    }

    @Command(command = "game rate", description = "Rates a game")
    private void gameRate() {
        var collectionId = getCollection("List of collections");
        if(collectionId == -1) return;
        var t = getGameFromCollection("List of games in collection " + getCollectionName(collectionId), collectionId);
        if(t == -1) return;
        var x = Double.valueOf(Helper.getContextValue(false, "What do you rate this game?", "0", getTerminal(), getResourceLoader(), getTemplateExecutor()));
        SetupDatabase.getJdbcTemplate().update(
                "UPDATE game_in_collection SET starrating = ? WHERE collectionid = ? AND gameid = ?", x, collectionId, t);
        getTerminal().writer().println("Successfully updated " + getGameName(t) + "'s rating to " + x);
    }
}
