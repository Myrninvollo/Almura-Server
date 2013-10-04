package org.bukkit.craftbukkit.scoreboard;

import java.util.Map;

import net.minecraft.server.Scoreboard;
import net.minecraft.server.ScoreboardObjective; // Almura
import net.minecraft.server.ScoreboardScore;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

/**
 * TL;DR: This class is special and lazily grabs a handle...
 * ...because a handle is a full fledged (I think permanent) hashMap for the associated name.
 * <p>
 * Also, as an added perk, a CraftScore will (intentionally) stay a valid reference so long as objective is valid.
 */
final class CraftScore implements Score {
    private final String playerName;
    private final CraftObjective objective;

    CraftScore(CraftObjective objective, String playerName) {
        this.objective = objective;
        this.playerName = playerName;
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(playerName);
    }

    public Objective getObjective() {
        return objective;
    }

    public int getScore() throws IllegalStateException {
        Scoreboard board = objective.checkState().board;

        if (board.getPlayers().contains(playerName)) { // Lazy
            Map<String, ScoreboardScore> scores = board.getPlayerObjectives(playerName);
            ScoreboardScore score = scores.get(objective.getHandle());
            if (score != null) { // Lazy
                return score.getScore();
            }
        }
        return 0; // Lazy
    }

    public void setScore(int score) throws IllegalStateException {
        objective.checkState().board.getPlayerScoreForObjective(playerName, objective.getHandle()).setScore(score);
    }

    public CraftScoreboard getScoreboard() {
        return objective.getScoreboard();
    }

    // Almura Start
    public boolean isSet() throws IllegalStateException {
        return objective.checkState().board.getPlayerObjectives(playerName).containsKey(objective.getHandle());
    }

    public void reset() throws IllegalStateException {
        CraftScoreboard myBoard = objective.checkState();
        Map<ScoreboardObjective, ScoreboardScore> savedScores = myBoard.board.getPlayerObjectives(playerName);
        if (savedScores.remove(objective.getHandle()) == null) {
            // If they don't have a score to delete, don't delete it.
            return;
        }
        myBoard.board.resetPlayerScores(playerName);
        for (Map.Entry<ScoreboardObjective, ScoreboardScore> e : savedScores.entrySet()) {
            myBoard.board.getPlayerScoreForObjective(playerName, e.getKey()).setScore(e.getValue().getScore());
        }
    }
    // Almura End
}
