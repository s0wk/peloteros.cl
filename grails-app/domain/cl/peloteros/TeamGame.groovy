package cl.peloteros

class TeamGame {

    int teamRol //0:host,1:cliente

    //la relacion pertenece a un usuario y un juego
    static belongsTo = [team: Team, game: Game]

    //vinculo una isntancia de equipo con una instancia de juego
    static TeamGame link(Team team, Game game, int teamRol) {
        def tg = TeamGame.findByTeamAndGame(team, game)
        if (!tg) {
            tg = new TeamGame()
            team?.addToGames(tg)
            game?.addToTeams(tg)
            tg.teamRol = teamRol
            tg.save()
        }
    }

    //desvinculo las instancias
    static void unlink(Team team, Game game) {
        def tg = TeamGame.findByTeamAndGame(team, game)
        if (tg) {
            team?.removeFromGames(tg)
            game?.removeFromTeams(tg)
            tg.delete()
        }
    }

    public TeamGame 

    //solo hay 2 roles posibles
    static constraints = {
        teamRol(inList: [0, 1])
    }
}
