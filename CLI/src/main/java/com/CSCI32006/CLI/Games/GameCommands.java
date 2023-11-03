package com.CSCI32006.CLI.Games;

import com.CSCI32006.CLI.Collections.CollectionRowMapper;
import com.CSCI32006.CLI.Helper;
import com.CSCI32006.CLI.Platforms.PlatformRowMapper;
import com.CSCI32006.CLI.SetupDatabase;
import com.CSCI32006.CLI.Users.UserCommands;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.AbstractShellComponent;

import java.util.ArrayList;
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
                if ("cancel".equals(x)) return;

                var list = SetupDatabase.getJdbcTemplate().query(
                        "SELECT g.gameid, title, esrb_rating, developername, publishername,\n" +
                                "       name AS platformname, avgHours, avgMinutes, avgRatings FROM game as g\n" +
                                "JOIN p320_06.game_on_platforms gop on g.gameid = gop.gameid\n" +
                                "JOIN p320_06.platform p on p.platformid = gop.platformid\n" +
                                "JOIN (SELECT gameid, TRUNC(AVG(hour), 2) AS avgHours, TRUNC(AVG(minutes), 2) AS avgMinutes FROM user_played_game group by gameid) upg ON upg.gameid = g.gameid\n" +
                                "JOIN (SELECT gameid, TRUNC(AVG(starrating), 2) AS avgRatings FROM user_star_game group by gameid) usg ON usg.gameid = g.gameid\n" +
                                "WHERE LOWER(LEFT(title, 1)) = LOWER(?) ORDER BY title, gop.releasedate ;", new GameDisplayRowMapper(), x
                );
                var w = list.stream().map((game -> SelectorItem.of(game.toString(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
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
                if (x == -1) return;

                var list = SetupDatabase.getJdbcTemplate().query(
                        "SELECT g.gameid, title, esrb_rating, developername, publishername,\n" +
                                "       name AS platformname, avgHours, avgMinutes, avgRatings FROM game as g\n" +
                                "JOIN p320_06.game_on_platforms gop on g.gameid = gop.gameid\n" +
                                "JOIN p320_06.platform p on p.platformid = gop.platformid\n" +
                                "JOIN (SELECT gameid, TRUNC(AVG(hour), 2) AS avgHours, TRUNC(AVG(minutes), 2) AS avgMinutes FROM user_played_game group by gameid) upg ON upg.gameid = g.gameid\n" +
                                "JOIN (SELECT gameid, TRUNC(AVG(starrating), 2) AS avgRatings FROM user_star_game group by gameid) usg ON usg.gameid = g.gameid\n" +
                                "WHERE gop.platformid = ? ORDER BY title, gop.releasedate;", new GameDisplayRowMapper(), x
                );
                var w = list.stream().map((game -> SelectorItem.of(game.toString(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
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
                if ("cancel".equals(x)) return;

                var list = SetupDatabase.getJdbcTemplate().query(
                        "SELECT g.gameid, title, esrb_rating, developername, publishername,\n" +
                                "       name AS platformname, avgHours, avgMinutes, avgRatings FROM game as g\n" +
                                "JOIN p320_06.game_on_platforms gop on g.gameid = gop.gameid\n" +
                                "JOIN p320_06.platform p on p.platformid = gop.platformid\n" +
                                "JOIN (SELECT gameid, TRUNC(AVG(hour), 2) AS avgHours, TRUNC(AVG(minutes), 2) AS avgMinutes FROM user_played_game group by gameid) upg ON upg.gameid = g.gameid\n" +
                                "JOIN (SELECT gameid, TRUNC(AVG(starrating), 2) AS avgRatings FROM user_star_game group by gameid) usg ON usg.gameid = g.gameid\n" +
                                "WHERE developername = ? ORDER BY title, gop.releasedate;", new GameDisplayRowMapper(), x
                );
                var w = list.stream().map((game -> SelectorItem.of(game.toString(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
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
                if ("cancel".equals(x)) return;

                var list = SetupDatabase.getJdbcTemplate().query(
                        "SELECT g.gameid, title, esrb_rating, developername, publishername,\n" +
                                "       name AS platformname, avgHours, avgMinutes, avgRatings FROM game as g\n" +
                                "JOIN p320_06.game_on_platforms gop ON g.gameid = gop.gameid\n" +
                                "JOIN p320_06.platform p ON p.platformid = gop.platformid\n" +
                                "JOIN (SELECT gameid, TRUNC(AVG(hour), 2) AS avgHours, TRUNC(AVG(minutes), 2) AS avgMinutes FROM user_played_game group by gameid) upg ON upg.gameid = g.gameid\n" +
                                "JOIN (SELECT gameid, TRUNC(AVG(starrating), 2) AS avgRatings FROM user_star_game group by gameid) usg ON usg.gameid = g.gameid\n" +
                                "JOIN (SELECT gameid, genretype FROM game_belongs_to_genre JOIN p320_06.genre g2 on game_belongs_to_genre.genreid = g2.genreid) gog ON g.gameid = gog.gameid" +
                                " WHERE genretype = ? ORDER BY title, gop.releasedate;", new GameDisplayRowMapper(), x
                );
                var w = list.stream().map((game -> SelectorItem.of(game.toString(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
                w.add(SelectorItem.of("cancel", "-1"));
                SingleItemSelector<String, SelectorItem<String>> component2 = new SingleItemSelector<>(getTerminal(),
                        w, "Select a game", null);
                component2.setResourceLoader(getResourceLoader());
                component2.setTemplateExecutor(getTemplateExecutor());
                SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context2 = component2
                        .run(SingleItemSelector.SingleItemSelectorContext.empty());
                var z = Integer.parseInt(context2.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
            }
            case releaseDate -> {
                var t = new ArrayList<SelectorItem<String>>();
                t.add(SelectorItem.of("GTR", "GTR"));
                t.add(SelectorItem.of("LES", "LES"));
                t.add(SelectorItem.of("BETWEEN", "BETWEEN"));
                t.add(SelectorItem.of("cancel", "cancel"));
                SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                        t, "Select a range", null);
                component.setResourceLoader(getResourceLoader());
                component.setTemplateExecutor(getTemplateExecutor());
                SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                        .run(SingleItemSelector.SingleItemSelectorContext.empty());
                var x = context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
                if ("cancel".equals(x)) return;
                switch (x) {
                    case "GTR" -> {
                        var date = java.sql.Date.valueOf(Helper.getContextValue(false, "Enter date greater than: ", "YYYY-MM-dd", getTerminal(), getResourceLoader(), getTemplateExecutor()));
                        var games = SetupDatabase.getJdbcTemplate().query(
                                "SELECT g.gameid, title, esrb_rating, developername, publishername,\n" +
                                        "       name AS platformname, avgHours, avgMinutes, avgRatings FROM game as g\n" +
                                        "JOIN p320_06.game_on_platforms gop on g.gameid = gop.gameid\n" +
                                        "JOIN p320_06.platform p on p.platformid = gop.platformid\n" +
                                        "JOIN (SELECT gameid, TRUNC(AVG(hour), 2) AS avgHours, TRUNC(AVG(minutes), 2) AS avgMinutes FROM user_played_game group by gameid) upg ON upg.gameid = g.gameid\n" +
                                        "JOIN (SELECT gameid, TRUNC(AVG(starrating), 2) AS avgRatings FROM user_star_game group by gameid) usg ON usg.gameid = g.gameid\n" +
                                        "WHERE gop.releasedate > ? ORDER BY releasedate, title;", new GameDisplayRowMapper(), date
                        );
                        var w = games.stream().map((game -> SelectorItem.of(game.toString(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
                        w.add(SelectorItem.of("cancel", "cancel"));
                        SingleItemSelector<String, SelectorItem<String>> component2 = new SingleItemSelector<>(getTerminal(),
                                w, "Games made after " + date, null);
                        component2.setResourceLoader(getResourceLoader());
                        component2.setTemplateExecutor(getTemplateExecutor());
                        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context2 = component2
                                .run(SingleItemSelector.SingleItemSelectorContext.empty());
                        var y = context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
                    }
                    case "LES" -> {
                        var date = java.sql.Date.valueOf(Helper.getContextValue(false, "Enter date less than: ", "YYYY-MM-dd", getTerminal(), getResourceLoader(), getTemplateExecutor()));
                        var games = SetupDatabase.getJdbcTemplate().query(
                                "SELECT g.gameid, title, esrb_rating, developername, publishername,\n" +
                                        "       name AS platformname, avgHours, avgMinutes, avgRatings FROM game as g\n" +
                                        "JOIN p320_06.game_on_platforms gop on g.gameid = gop.gameid\n" +
                                        "JOIN p320_06.platform p on p.platformid = gop.platformid\n" +
                                        "JOIN (SELECT gameid, TRUNC(AVG(hour), 2) AS avgHours, TRUNC(AVG(minutes), 2) AS avgMinutes FROM user_played_game group by gameid) upg ON upg.gameid = g.gameid\n" +
                                        "JOIN (SELECT gameid, TRUNC(AVG(starrating), 2) AS avgRatings FROM user_star_game group by gameid) usg ON usg.gameid = g.gameid\n" +
                                        "WHERE gop.releasedate < ? ORDER BY releasedate, title;", new GameDisplayRowMapper(), date
                        );
                        var w = games.stream().map((game -> SelectorItem.of(game.toString(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
                        w.add(SelectorItem.of("cancel", "cancel"));
                        SingleItemSelector<String, SelectorItem<String>> component2 = new SingleItemSelector<>(getTerminal(),
                                w, "Games made before " + date, null);
                        component2.setResourceLoader(getResourceLoader());
                        component2.setTemplateExecutor(getTemplateExecutor());
                        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context2 = component2
                                .run(SingleItemSelector.SingleItemSelectorContext.empty());
                        var y = context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
                    }
                    case "BETWEEN" -> {
                        var lDate = java.sql.Date.valueOf(Helper.getContextValue(false, "Enter lower bound date: ", "YYYY-MM-dd", getTerminal(), getResourceLoader(), getTemplateExecutor()));
                        var uDate = java.sql.Date.valueOf(Helper.getContextValue(false, "Enter upper bound date: ", "YYYY-MM-dd", getTerminal(), getResourceLoader(), getTemplateExecutor()));
                        var games = SetupDatabase.getJdbcTemplate().query(
                                "SELECT g.gameid, title, esrb_rating, developername, publishername,\n" +
                                        "       name AS platformname, avgHours, avgMinutes, avgRatings FROM game as g\n" +
                                        "JOIN p320_06.game_on_platforms gop on g.gameid = gop.gameid\n" +
                                        "JOIN p320_06.platform p on p.platformid = gop.platformid\n" +
                                        "JOIN (SELECT gameid, TRUNC(AVG(hour), 2) AS avgHours, TRUNC(AVG(minutes), 2) AS avgMinutes FROM user_played_game group by gameid) upg ON upg.gameid = g.gameid\n" +
                                        "JOIN (SELECT gameid, TRUNC(AVG(starrating), 2) AS avgRatings FROM user_star_game group by gameid) usg ON usg.gameid = g.gameid\n" +
                                        "WHERE gop.releasedate BETWEEN ? AND ? ORDER BY releasedate, title;", new GameDisplayRowMapper(), lDate, uDate
                        );
                        var w = games.stream().map((game -> SelectorItem.of(game.toString(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
                        w.add(SelectorItem.of("cancel", "cancel"));
                        SingleItemSelector<String, SelectorItem<String>> component2 = new SingleItemSelector<>(getTerminal(),
                                w, "Games made between " + lDate + ", " + uDate, null);
                        component2.setResourceLoader(getResourceLoader());
                        component2.setTemplateExecutor(getTemplateExecutor());
                        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context2 = component2
                                .run(SingleItemSelector.SingleItemSelectorContext.empty());
                        var y = context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
                    }
                }
            }
            case price -> {
                var t = new ArrayList<SelectorItem<String>>();
                t.add(SelectorItem.of("GTR", "GTR"));
                t.add(SelectorItem.of("LES", "LES"));
                t.add(SelectorItem.of("BETWEEN", "BETWEEN"));
                t.add(SelectorItem.of("cancel", "cancel"));
                SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                        t, "Select a range", null);
                component.setResourceLoader(getResourceLoader());
                component.setTemplateExecutor(getTemplateExecutor());
                SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                        .run(SingleItemSelector.SingleItemSelectorContext.empty());
                var x = context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
                if ("cancel".equals(x)) return;
                switch (x) {
                    case "GTR" -> {
                        var price = Double.valueOf(Helper.getContextValue(false, "Enter price greater than: ", "0", getTerminal(), getResourceLoader(), getTemplateExecutor()));
                        var games = SetupDatabase.getJdbcTemplate().query(
                                "SELECT g.gameid, title, esrb_rating, developername, publishername,\n" +
                                        "       name AS platformname, avgHours, avgMinutes, avgRatings FROM game as g\n" +
                                        "JOIN p320_06.game_on_platforms gop on g.gameid = gop.gameid\n" +
                                        "JOIN p320_06.platform p on p.platformid = gop.platformid\n" +
                                        "JOIN (SELECT gameid, TRUNC(AVG(hour), 2) AS avgHours, TRUNC(AVG(minutes), 2) AS avgMinutes FROM user_played_game group by gameid) upg ON upg.gameid = g.gameid\n" +
                                        "JOIN (SELECT gameid, TRUNC(AVG(starrating), 2) AS avgRatings FROM user_star_game group by gameid) usg ON usg.gameid = g.gameid\n" +
                                        "WHERE gop.price > ? ORDER BY title, gop.releasedate;", new GameDisplayRowMapper(), price
                        );
                        var w = games.stream().map((game -> SelectorItem.of(game.toString(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
                        w.add(SelectorItem.of("cancel", "cancel"));
                        SingleItemSelector<String, SelectorItem<String>> component2 = new SingleItemSelector<>(getTerminal(),
                                w, "Games cost more than " + price, null);
                        component2.setResourceLoader(getResourceLoader());
                        component2.setTemplateExecutor(getTemplateExecutor());
                        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context2 = component2
                                .run(SingleItemSelector.SingleItemSelectorContext.empty());
                        var y = context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
                    }
                    case "LES" -> {
                        var price = Double.valueOf(Helper.getContextValue(false, "Enter price less than: ", "0", getTerminal(), getResourceLoader(), getTemplateExecutor()));
                        var games = SetupDatabase.getJdbcTemplate().query(
                                "SELECT g.gameid, title, esrb_rating, developername, publishername,\n" +
                                        "       name AS platformname, avgHours, avgMinutes, avgRatings FROM game as g\n" +
                                        "JOIN p320_06.game_on_platforms gop on g.gameid = gop.gameid\n" +
                                        "JOIN p320_06.platform p on p.platformid = gop.platformid\n" +
                                        "JOIN (SELECT gameid, TRUNC(AVG(hour), 2) AS avgHours, TRUNC(AVG(minutes), 2) AS avgMinutes FROM user_played_game group by gameid) upg ON upg.gameid = g.gameid\n" +
                                        "JOIN (SELECT gameid, TRUNC(AVG(starrating), 2) AS avgRatings FROM user_star_game group by gameid) usg ON usg.gameid = g.gameid\n" +
                                        "WHERE gop.price < ? ORDER BY title, gop.releasedate;", new GameDisplayRowMapper(), price
                        );
                        var w = games.stream().map((game -> SelectorItem.of(game.toString(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
                        w.add(SelectorItem.of("cancel", "cancel"));
                        SingleItemSelector<String, SelectorItem<String>> component2 = new SingleItemSelector<>(getTerminal(),
                                w, "Games less than " + price, null);
                        component2.setResourceLoader(getResourceLoader());
                        component2.setTemplateExecutor(getTemplateExecutor());
                        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context2 = component2
                                .run(SingleItemSelector.SingleItemSelectorContext.empty());
                        var y = context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
                    }
                    case "BETWEEN" -> {
                        var lPrice = Double.valueOf(Helper.getContextValue(false, "Enter lower bound price: ", "0", getTerminal(), getResourceLoader(), getTemplateExecutor()));
                        var uPrice = Double.valueOf(Helper.getContextValue(false, "Enter upper bound price: ", "0", getTerminal(), getResourceLoader(), getTemplateExecutor()));
                        var games = SetupDatabase.getJdbcTemplate().query(
                                "SELECT g.gameid, title, esrb_rating, developername, publishername,\n" +
                                        "       name AS platformname, avgHours, avgMinutes, avgRatings FROM game as g\n" +
                                        "JOIN p320_06.game_on_platforms gop on g.gameid = gop.gameid\n" +
                                        "JOIN p320_06.platform p on p.platformid = gop.platformid\n" +
                                        "JOIN (SELECT gameid, TRUNC(AVG(hour), 2) AS avgHours, TRUNC(AVG(minutes), 2) AS avgMinutes FROM user_played_game group by gameid) upg ON upg.gameid = g.gameid\n" +
                                        "JOIN (SELECT gameid, TRUNC(AVG(starrating), 2) AS avgRatings FROM user_star_game group by gameid) usg ON usg.gameid = g.gameid\n" +
                                        "WHERE gop.price BETWEEN ? AND ? ORDER BY title, gop.releasedate;", new GameDisplayRowMapper(), lPrice, uPrice
                        );
                        var w = games.stream().map((game -> SelectorItem.of(game.toString(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
                        w.add(SelectorItem.of("cancel", "cancel"));
                        SingleItemSelector<String, SelectorItem<String>> component2 = new SingleItemSelector<>(getTerminal(),
                                w, "Games cost between " + lPrice + ", " + uPrice, null);
                        component2.setResourceLoader(getResourceLoader());
                        component2.setTemplateExecutor(getTemplateExecutor());
                        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context2 = component2
                                .run(SingleItemSelector.SingleItemSelectorContext.empty());
                        var y = context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
                    }
                }
            }

        }
    }
}
