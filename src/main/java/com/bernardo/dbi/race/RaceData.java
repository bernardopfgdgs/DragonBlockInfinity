package com.bernardo.dbi.race;

import com.bernardo.dbi.status.StatusBaseMultiversal;

public class RaceData {

	public final Race.RaceType raceType;
	public final StatusBaseMultiversal statusBase;

	public RaceData(Race.RaceType raceType, StatusBaseMultiversal statusBase) {
		this.raceType = raceType;
		this.statusBase = statusBase;
	}

}