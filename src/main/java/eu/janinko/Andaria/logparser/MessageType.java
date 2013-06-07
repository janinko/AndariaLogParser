package eu.janinko.Andaria.logparser;

public enum MessageType {
	AT(null),
		PlayerSays(AT),
			PlayerSaysMuted(PlayerSays),
			PlayerSaysParty(PlayerSays),
		Command(AT),
			Tweak(Command),
			TargetedCommand(Command),
			TargetedTweak(TargetedCommand,Tweak),
		Located(AT),
			IncognitoSet(Located),
			IncognitoReset(Located)
	;
	
    private MessageType[] parents = null;
    
    private MessageType(MessageType ... parents) {
        this.parents = parents;
    }
    
    public boolean is(MessageType other) {
        if (other == null) return false;
        if (other == this) return true;
        if (parents == null) return false;
        
        for(MessageType t : parents){
        	if(t.is(other)) return true;
        }
        return false;
    }
}
