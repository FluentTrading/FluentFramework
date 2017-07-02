package fluent.framework.order;

public enum Side{

    BUY     ("BUY",     "RECEIVE"),
    SELL    ("SELL",    "PAY"),
    UNKNOWN ("UNKNOWN", "UNKNOWN");

    private final String name;
    private final String synonomousName;

    private Side( String name, String synonomousName ){
        this.name           = name;
        this.synonomousName = synonomousName;
    }

    public final String getName( ){
        return name;
    }

    public final char getChar( ){
        return name.charAt(0);
    }
    
    public final String getSynonymousName( ){
        return synonomousName;
    }


}
