package cl.peloteros

class Team {
    String name
    String description
    SortedSet messages
    //hay varios miembros, partidos y mensajes
    static hasMany = [memberships: Membership, games: TeamGame, messages: Message]
    static mapping = {
        memberships batchSize: 20
        games batchSize: 10
    }

    static constraints = {
        name(blank: false, unique: true)//obligatorio
        description(blank: true, nullable: true)
    }

    //defino el formato imprimible
    public String toString() {
        return name;
    }

    int userCount() {
        return Membership.createCriteria().get {
            projections {
                countDistinct('user')
                eq('team', this)
            }
        }
    }

    //método para obtener los mensajes aun no vistos
    public int unseenMessages(User user) {
        Membership m = Membership.findByUserAndTeam(user, this)
        return m.unseenMessages()
    }

    //devuelve todos los juegos futuros del equipo
    List futureGames() {
        //defino la fecha de hoy
        Date hoy = new Date()
        //return games.findAll {it.game.date > hoy}.collect{it.game}
        return TeamGame.withCriteria {
            eq('team', this)
            game {
                ge('date', hoy)
            }
        }.collect {it.game}
    }

    //reviso si un usuario es administrador
    boolean isTeamAdmin(User user) {
        def result = Membership.createCriteria().get {
            and {
                eq('user', user)
                eq('team', this)
                or {
                    eq('userRol', 'admin')
                    eq('userRol', 'superadmin')
                }
            }
        }
        if (result) return true
        return false
    }

    //agrego un usuario al equipo
    public Team addToUsers(User user, String rol) {
        Membership.link(user, this, rol)
        return this
    }

    //agrego un game al equipo
    public Team addToGames(Game game, String teamRol) {
        TeamGame.link(this, game, teamRol)
        return this
    }

    //agregar un mensaje al equipo
    public Team addToMessages(String message, User user) {
        Message.link(user, this, message)
        return this
    }

    //quito a un usuario del equipo, no debe ser el administrador
    public Team removeFromUsers(User user) {
        Membership.unlink(user, this)
        return this
    }

    //elimino un game del equipo
    public Team removeFromGames(Game game) {
        TeamGame.unlink(this, game)
        return this
    }
}
