package eu.janinko.Andaria.logparser.messages;

import eu.janinko.Andaria.logparser.MessageType;
import java.util.Calendar;

public class TargetedCommand extends Message {
	Integer uid;
	String name;
	Integer amount;

	public TargetedCommand(Calendar dt, String m, Integer uid, String name, Integer amount) {
		super(dt, m, MessageType.TargetedCommand);
		this.uid = uid;
		this.name = name;
		this.amount = amount;
	}
	public TargetedCommand(Calendar dt, String m, Integer uid, String name, Integer amount, boolean tweak) {
		super(dt, m, tweak ? MessageType.TargetedTweak : MessageType.TargetedCommand);
		this.uid = uid;
		this.name = name;
		this.amount = amount;
	}

	public Integer getUid() {
		return uid;
	}

	public String getName() {
		return name;
	}

	public Integer getAmount() {
		return amount;
	}


}
