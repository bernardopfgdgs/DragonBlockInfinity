ackage com.bernardo.dbi.race;

import com.bernardo.dbi.status.StatusBaseMultiversal;
import com.bernardo.dbi.util.Identifiable;

public class RaceData implements Identifiable {

	public final String id;
	public final Race.RaceType raceType;
	public final StatusBaseMultiversal statusBase;

	public RaceData(String id, Race.RaceType raceType, StatusBaseMultiversal statusBase) {
		this.id = id;
		this.raceType = raceType;
		this.statusBase = statusBase;
	}

	@Override
	public String getId() {
		return id;
	}

}