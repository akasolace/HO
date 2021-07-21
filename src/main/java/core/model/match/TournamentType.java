package core.model.match;

public enum TournamentType {
    NONE(0),
    LEAGUE_WITH_PLAYOFFS(3),
    CUP(4),  // League match
    DIVISIONBATTLE(10), //Qualification match

    // U21 Nation team matches
    U21_Friendlies(4894807),
    U21_Africa_Cup( 4878492),
    U21_America_Cup(4878490),
    U21_Asia_Oceania_Cup(4878493),
    U21_Europe_Cup(4878483),
    U21_Nations_Cup(4892615),
    U21_Wildcard_Rounds(4891573),
    U21_World_Cup(4892549),

    // Nation team matches
    Wildcard_Rounds(5001311),
    World_Cup(5001315),
    Africa_Cup(5001278),
    America_Cup(5001277),
    Asia_Oceania_Cup(5001279),
    Europe_Cup(5001273),
    Nations_Cup(5001319),
    NT_Friendlies(5001325);

    private final int id;

    TournamentType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    public boolean isU21TeamMatch(){
        return id > 4800000 && id < 4900000;
    }
    public boolean isNTTeamMatch(){
        return id > 5000000 && id < 5100000;
    }
    public boolean isWorldCup(){
        return id == World_Cup.id || id == U21_World_Cup.id;
    }
    public boolean isNTFriendly(){
        return id== NT_Friendlies.id || id == U21_Friendlies.id;
    }
    public boolean isNationsCup(){
        return id== Nations_Cup.id || id == U21_Nations_Cup.id;
    }
    public boolean isContinentalCup(){
        return switch (this) {
            case U21_Africa_Cup, U21_America_Cup, U21_Asia_Oceania_Cup, U21_Europe_Cup,
                    Africa_Cup, America_Cup, Asia_Oceania_Cup, Europe_Cup -> true;
            default -> false;
        };
    }

    public static TournamentType getById(int id) {
        for (TournamentType oTournamentType : TournamentType.values()) {
            if (oTournamentType.getId() == id) {
                return oTournamentType;
            }
        }
        return null;
    }
}
