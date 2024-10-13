package catali.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Chat GPT 100%
public class GamemodeTeamControl {
    private final Map<Integer, GamemodeTeam> teams = new HashMap<>();
    private final Map<String, Integer> playerTeamMap = new HashMap<>(); // Lưu UUID của player với teamId
    private final Map<String, Integer> leaderTeamMap = new HashMap<>(); // Lưu UUID của player với teamId
    private final Set<String> players = new HashSet<>(); // Lưu danh sách các player đang chơi

    // Thêm một team mới với teamId
    public void addTeam(int teamId, GamemodeTeam team) {
        teams.put(teamId, team);
        leaderTeamMap.put(team.getLeader(), teamId);
    }

    // Xóa một team theo teamId
    public void removeTeam(int teamId) {
        GamemodeTeam team = teams.remove(teamId);
        if (team != null) {
            team.getAllPlayers().forEach(playerTeamMap::remove); // Xóa các player khỏi map
            leaderTeamMap.remove(team.getLeader());
        }
    }

    // Kiểm tra xem team có tồn tại không
    public boolean hasTeam(int teamId) {
        return teams.containsKey(teamId);
    }

    // Lấy team theo teamId
    public GamemodeTeam getTeam(int teamId) {
        return teams.get(teamId);
    }

    // Thêm một player vào team
    public void addPlayerToTeam(String uuid, int teamId) {
        GamemodeTeam team = teams.get(teamId);
        if (team != null) {
            team.addPlayer(uuid);
            playerTeamMap.put(uuid, teamId);
            players.add(uuid); // Thêm vào danh sách players
        }
    }

    // Xóa player khỏi team
    public void removePlayerFromTeam(String uuid) {
        Integer teamId = playerTeamMap.remove(uuid);
        if (teamId != null) {
            GamemodeTeam team = teams.get(teamId);
            if (team != null) {
                team.removePlayer(uuid);
            }
        }
        players.remove(uuid);
    }

    // Lấy teamId theo player UUID
    public Integer getTeamIdByPlayerUuid(String uuid) {
        return playerTeamMap.get(uuid);
    }

    // Lấy teamId theo leader UUID
    public Integer getTeamIdByLeaderUuid(String uuid) {
        return leaderTeamMap.get(uuid);
    }

    // Kiểm tra nếu player đang ở trong team
    public boolean isPlayerInTeam(String uuid) {
        return playerTeamMap.containsKey(uuid);
    }

    // Lấy tất cả các player trong một team
    public Set<String> getPlayersInTeam(int teamId) {
        GamemodeTeam team = teams.get(teamId);
        if (team != null) {
            return team.getAllPlayers();
        }
        return new HashSet<>();
    }

    // Trả về toàn bộ teamId cùng với team thực tế
    public Map<Integer, GamemodeTeam> getAllTeams() {
        return teams; 
    }

    // Kiểm tra xem một player có đang chơi hay không
    public boolean isPlayerPlaying(String uuid) {
        return players.contains(uuid);
    }
}
