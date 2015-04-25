package mud.game;

import mud.d20.Races;
import mud.interfaces.Ruleset;


/*
 * Copyright (c) 2012 Jeremy N. Harton
 * 
 * Released under the MIT License:
 * LICENSE.txt, http://opensource.org/licenses/MIT
 * 
 * NOTE: license provided with code controls, if any
 * changes are made to the one referred to.
 */

public class Race {
	private Ruleset rules;
	
	private String name;
	private Subrace sub;
	private int id;
	private Integer[] statAdj;  // stats adjustments pertaining to a particular race
	private boolean restricted; // is the race restricted (not available from normal selection)
	private boolean canFly;     // is this race capable of flight
	
	// no args constructor
	/*public Race() {
		this.name = "noname";
		this.sub = null;
		this.id = -1;
		this.statAdj = new Integer[] { 0, 0, 0, 0, 0, 0 };
		this.restricted = false;
		this.canFly = false;
	}*/
	
	public Race(Ruleset rs, String name, int id, boolean restricted) {
		this(rs, name, id, null, restricted);
	}
	
	public Race(Ruleset rs, String name, int id, boolean restricted, boolean canFly) {
		this(rs, name, id, null, restricted, canFly);
	}

	public Race(Ruleset rs, String name, int id, Integer[] statAdj, boolean restricted) {
		this(rs, name, id, statAdj, restricted, false);
	}
	
	public Race(Ruleset rs, String name, int id, Integer[] statAdj, boolean restricted, boolean canFly) {
		this.rules = rs;
		this.name = name;
		this.id = id;
		this.statAdj = statAdj;
		this.restricted = restricted;
		this.canFly = canFly;
	}
	
	public Ruleset getRules() {
		return this.rules;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setSubrace(Subrace sub) {
		this.sub = sub;
	}
	
	public Subrace getSubrace() {
		return this.sub;
	}
	
	public int getId() {
		return this.id;
	}

	public Integer[] getStatAdjust() {
		if( this.statAdj == null ) {
			if( this.rules != null ) {
				return new Integer[this.rules.getAbilities().length];
			}
			else return new Integer[0];
		}
		
		return this.statAdj;
	}

	public boolean isRestricted() {
		return this.restricted;
	}
	
	public boolean canFly() {
		return this.canFly;
	}

	public String toString() {
		if (sub == null) {
			return this.name;
		}
		else {
			return this.sub.name;
		}
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof Race) {
			final Race otherRace = (Race) object;
			
			// TODO fix kludge, two races of the same name are equal according to this
			if(this.getName().equals( otherRace.getName() )) {
				return true;
			}
			
			return false;
		}
		
		return false;
	}
	
	public class Subrace {
		public Race parentRace;
		public String name;     // name of the subrace
		public String alt;      // common alternate name for the subrace

		public Subrace(Race pRace, String name, String alt) {
			this.parentRace = pRace;
			this.name = name;
			this.alt = alt;
		}
	}
	
	public final class Subraces {
		public final Subrace DARK_ELF = new Subrace(Races.ELF, "Dark Elf", "Drow");
		public final Subrace MOON_ELF = new Subrace(Races.ELF, "Moon Elf", "Gray Elf");
		public final Subrace SEA_ELF = new Subrace(Races.ELF, "Sea Elf", "");
		public final Subrace SUN_ELF = new Subrace(Races.ELF, "Sun Elf", "Gold Elf");
		public final Subrace WILD_ELF = new Subrace(Races.ELF, "Wild Elf", "");
		public final Subrace WOOD_ELF = new Subrace(Races.ELF, "Wood Elf", "");
	}
}