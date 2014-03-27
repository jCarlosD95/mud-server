package mud.objects;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mud.ObjectFlag;
import mud.MUDObject;
import mud.Trigger;
import mud.TriggerType;
import mud.TypeFlag;

//import mud.miscellaneous.Atmosphere;

import mud.events.EventSource;
import mud.events.SayEvent;
import mud.events.SayEventListener;
import mud.misc.Zone;
import mud.objects.Thing;
import mud.utils.Utils;
import mud.weather.Weather;

/*
 * Copyright (c) 2012 Jeremy N. Harton
 * 
 * Released under the MIT License:
 * LICENSE.txt, http://opensource.org/licenses/MIT
 * 
 * NOTE: license provided with code controls, if any
 * changes are made to the one referred to.
 */

/**
 * Room Class
 * 
 * @author Jeremy N. Harton
 * 
 */
public class Room extends MUDObject implements EventSource
{
	// NON,FOR,MSH,HILL,MTN,DST,AQU,SKY?
	public enum Terrain { NONE, FOREST, MARSH, HILLS, MOUNTAIN, DESERT, PLAINS, AQUATIC, SKY };
	
	// NOTE: for room, location and "parent" are the same

	private RoomType roomType = RoomType.NONE;                // the type of room (I = Inside, O = Outside, P = Protected, N = None)
	private Terrain terrain = Terrain.NONE;                   // terrain type of the room (affects movement speed?)
	private Weather weather;                                  // the weather in this room
	//private Atmosphere atmosphere = new Atmosphere();       // the atmosphere of the room (weather related)

	private ArrayList<Exit> exits = new ArrayList<Exit>();    // the exits leading away from the room
	private String exitNames;                                 // formatted string containing the usable exit names

	private ArrayList<Thing> things = new ArrayList<Thing>(); // the objects the room contains (things)
	private ArrayList<Item> items = new ArrayList<Item>();    // the objects the room contains (items)

	public String music;                                      // the ambient background music for this room (filename, probably a wav file)
	public String timeOfDay = "DAY";                          // replace this with an enum with one type per each or a hashmap string, boolean?
	// DAY or NIGHT

	private Zone zone = null; // the zone this room belongs to

	private Integer instance_id = null; // instance_id, if this is the original, it should be null

	public int x = 10, y = 10; // size of the room ( 10x10 default )
	public int z = 10;         // height of room ( 10 default )

	private BitSet[][] tiles;
	
	private ArrayList<Player> listeners; // Player(s) in the Room listening to what is going on
	private List<SayEventListener> _listeners = new ArrayList<SayEventListener>(); // 

	private HashMap<TriggerType, List<Trigger>> triggers = new HashMap<TriggerType, List<Trigger>>();
	
	// initialize trigger lists and some basic triggers
	{
		triggers.put(TriggerType.onEnter, new LinkedList<Trigger>());
		triggers.put(TriggerType.onLeave, new LinkedList<Trigger>());
		(triggers.get(TriggerType.onEnter)).add( new Trigger("TRIGGER: enter") );
		(triggers.get(TriggerType.onLeave)).add( new Trigger("TRIGGER: leave") );
	}
	
	// misc note: parent == location

	/**
	 * Construct a room using only default parameter
	 */
	public Room() {
		super(-1);
		this.type = TypeFlag.ROOM;
		this.name = "room";
		this.desc = "You see nothing.";
		this.flags = EnumSet.of(ObjectFlag.SILENT);
		this.locks = "";   // Set the locks
		this.location = 0; // Set the location

		this.tiles = new BitSet[x][y];

		this.listeners = new ArrayList<Player>();
	}
	
	/**
	 * Copy Constructor
	 * 
	 * @param toCopy
	 */
	public Room(Room toCopy)
	{
		super(toCopy.getDBRef());
		this.type = TypeFlag.ROOM;
		this.name = toCopy.getName();         // Set the name
		this.desc = toCopy.getDesc();         // Set the description to the default
		this.flags = toCopy.getFlags();       // Set the flags
		this.locks = "";                      // Set the locks
		this.location = toCopy.getLocation(); // Set the location

		this.tiles = toCopy.getTiles();

		this.listeners = new ArrayList<Player>();
	}
	
	/**
	 * Loading Constructor
	 * 
	 * @param tempDBREF
	 * @param tempName
	 * @param tempFlags
	 * @param tempDesc
	 * @param tempLocation
	 */
	public Room(int tempDBREF, String tempName, final EnumSet<ObjectFlag> tempFlags, String tempDesc, int tempLocation)
	{
		super(tempDBREF);
		this.type = TypeFlag.ROOM;
		//this.dbref = tempDBREF;                                    // Set the dbref (database reference)
		this.name = tempName;                                        // Set the name
		this.desc = tempDesc;                                        // Set the description
		this.flags = tempFlags;                                      // Set room flags
		this.locks = "";                                             // Set the locks
		this.location = tempLocation;                                // Set the location

		this.tiles = new BitSet[x][y];

		this.listeners = new ArrayList<Player>();
	}

	/**
	 * Set parent room.
	 * 
	 * @param parentRoomId
	 */
	public void setParent(int parentRoomId) {
		this.setLocation(parentRoomId);
	}

	/**
	 * Get parent room.
	 * 
	 * @return int parent room dbref number
	 */
	public int getParent() {
		return this.getLocation();
	}

	/**
	 * Set the type of room.
	 * 
	 * @param newRoomType
	 */
	public void setRoomType(final RoomType newRoomType) {
		this.roomType = newRoomType;
	}

	/**
	 * Get the type of room.
	 * 
	 * @return
	 */
	public RoomType getRoomType() {
		return this.roomType;
	}

	public void setWeather(Weather weather) {
		this.weather = weather;
	}

	public Weather getWeather() {
		return this.weather;
	}

	/**
	 * @return the exits
	 */
	public ArrayList<Exit> getExits() {
		//return new ArrayList<Exit>(exits); // ???
		return exits;

	}

	public String getExitNames() {
		if( exits.size() > 0 ) {
			final StringBuilder buf = new StringBuilder();
			for (final Exit e : exits) {
				if( e instanceof Door ) {
					Door d = (Door) e;
					
					if( getDBRef() == d.getLocation() ) {
						if( d.isLocked() ) buf.append(", ").append(d.getName().split(";")[0] + " (locked)");
						else buf.append(", ").append(d.getName().split(";")[0]);
					}
					else if( getDBRef() == d.getDestination() ) {
						if( d.isLocked() ) buf.append(", ").append(d.getName().split(";")[1] + " (locked)");
						else buf.append(", ").append(d.getName().split(";")[1]);
					}
				} 
				else buf.append(", ").append(e.getName());
			}
			return buf.toString().substring(2); // clip off the initial, unnecessary " ,"
		}
		else { return ""; }
	}

	public String getVisibleExitNames() {
		final StringBuilder buf = new StringBuilder();
		for (final Exit e : exits) {
			if (!e.getFlags().contains("D")) {
				if( e instanceof Door ) {
					Door d = (Door) e;
					
					if( getDBRef() == d.getLocation() ) {
						if( d.isLocked() ) buf.append(", ").append(d.getName().split(";")[0] + " (locked)");
						else buf.append(", ").append(d.getName().split(";")[0]);
					}
					else if( getDBRef() == d.getDestination() ) {
						if( d.isLocked() ) buf.append(", ").append(d.getName().split(";")[1] + " (locked)");
						else buf.append(", ").append(d.getName().split(";")[1]);
					}
				} 
				else buf.append(", ").append(e.getName());
			}
		}
		return buf.toString().substring(2); // clip off the initial, unecessary " ,"
	}

	/**
	 * @param exits the exits to set
	 */
	public void setExits(ArrayList<Exit> exits) {
		this.exits = exits;
	}

	public Integer getInstanceId() {
		if (this.instance_id != null) {
			return this.instance_id;
		}
		else {
			return -1;
		}
	}

	public void setTiles(BitSet[][] newTiles) {
		this.tiles = newTiles;
	}

	public BitSet[][] getTiles() {
		return this.tiles;
	}

	public ArrayList<Player> getListeners() {
		return this.listeners;
	}

	public void addListener(Player player) {
		listeners.add(player);
	}

	public void removeListener(Player player) {
		listeners.remove(player);
	}

	public synchronized void addSayEventListener(SayEventListener listener)  {
		_listeners.add(listener);
	}
	public synchronized void removeSayEventListener(SayEventListener listener)   {
		_listeners.remove(listener);
	}
	
	/**
	 * Add an Item to the room.
	 * 
	 * @param item
	 */
	public void addItem(Item item) {
		this.items.add(item);
	}
	
	/**
	 * Add multiple items to the room at the same time.
	 * 
	 * @param items List of Item(s)
	 */
	public void addItems(List<Item> items) {
		for(Item item : items) {
			addItem(item);
		}
	}
	
	/**
	 * Remove an Item from the room.
	 * @param item
	 */
	public void removeItem(Item item) {
		this.items.remove(item);
	}
	
	/**
	 * Get a List of the Item(s) in the Room.
	 * 
	 * @return
	 */
	public List<Item> getItems() {
		return this.items;
	}
	
	/**
	 * Add a Thing to the room
	 * 
	 * @param thing the Thing to add
	 */
	public void addThing(Thing thing) {
		this.things.add(thing);
	}
	
	/**
	 * Add multiple Thing(s) to the room at the same time.
	 * 
	 * @param things List of Thing(s) to add
	 */
	public void addThings(List<Thing> things) {
		for(Thing thing : things) {
			addThing(thing);
		}
	}
	
	/**
	 * Remove a Thing from the room
	 * 
	 * @param thing
	 */
	public void removeThing(Thing thing) {
		this.things.remove(thing);
	}

	public List<Thing> getThings() {
		return this.things;
	}
	
	public void setTrigger(TriggerType type, Trigger trigger) {
		this.triggers.get(type).add(trigger);
	}

	public List<Trigger> getTriggers(TriggerType triggerType) {
		return this.triggers.get(triggerType);
	}

	public Terrain getTerrain() {
		return this.terrain;
	}

	public void setZone(final Zone zone) {
		this.zone = zone;
	}

	public Zone getZone() {
		return this.zone;
	}

	// call this method whenever you want to notify
	//the event listeners of the particular event
	@Override
	public synchronized void fireEvent(String message) {
		SayEvent event = new SayEvent(this, message);
		Iterator<SayEventListener> iter = _listeners.iterator();
		while(iter.hasNext())  {
			iter.next().handleSayEvent(event);
		}
	}

	/**
	 * Translate the persistent aspects of the room into the string
	 * format used by the database
	 */
	public String toDB() {
		String[] output = new String[9];
		output[0] = this.getDBRef() + "";                         // database reference number
		output[1] = this.getName();                               // name
		output[2] = this.type + this.getFlagsAsString();          // flags
		output[3] = this.getDesc();                               // description
		output[4] = this.getLocation() + "";                      // location (a.k.a parent)
		output[5] = "" + this.getRoomType().toString().charAt(0); // room type
		output[6] = this.x + "," + this.y + "," + this.z;         // room dimensions (x,y,z)
		output[7] = "-1";                                         // room terrain
		if( zone != null ) output[8] = "" + zone.getId();         // zone data
		else output[8] = "-1";

		return Utils.join(output, "#");
	}

	public String toJSON() {
		return null;
	}

	@Override
	public Room fromJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	public String toString() {
		return getName() + " (#" + getDBRef() + ")";
	}
}