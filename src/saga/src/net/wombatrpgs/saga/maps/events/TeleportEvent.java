/**
 *  TeleportEvent.java
 *  Created on Dec 24, 2012 2:00:17 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.saga.maps.events;

import net.wombatrpgs.saga.core.FinishListener;
import net.wombatrpgs.saga.core.SGlobal;
import net.wombatrpgs.saga.maps.Level;
import net.wombatrpgs.saga.maps.Loc;
import net.wombatrpgs.saga.rpg.CharacterEvent;

/**
 * Constructs a teleportation device! (or event, just depends on your
 * perspective...)
 * MR: Contains a link to some map data. If that map doesn't exist, generate it.
 * This is meant to be the payload of any staircase map event, and they should
 * extend it.
 */
public class TeleportEvent extends MapEvent {
	
	protected String mapKey;
	
	protected boolean triggered;
	protected MapEvent victim;

	/**
	 * Creates a new teleport for the supplied parent level using coordinates
	 * inferred from the tiled object. Called from the superclass's factory
	 * method.
	 * @param 	parent			The level we want to teleport from
	 * @param	mapKey			The MDO key of the map we want to teleport to
	 * @param	dir				Whether the stairs go up or down
	 */
	public TeleportEvent(Level parent, String mapKey) {
		super(parent);
		this.mapKey = mapKey;
		
		triggered = false;
		victim = null;
	}

	/**
	 * @see net.wombatrpgs.saga.maps.events.MapEvent#update(float)
	 */
	@Override
	public void update(float elapsed) {
		super.update(elapsed);
		if (triggered && !parent.isMoving()) {
			teleport(victim);
			triggered = false;
		}
	}

	/**
	 * @see net.wombatrpgs.saga.maps.events.MapEvent#collideWith
	 * (net.wombatrpgs.saga.rpg.CharacterEvent)
	 */
	@Override
	public void collideWith(CharacterEvent character) {
		if (!triggered && character == SGlobal.hero) {
			triggered = true;
			victim = character;
		}
	}

	/**
	 * @see net.wombatrpgs.saga.maps.events.MapEvent#isPassable()
	 */
	@Override
	public boolean isPassable() {
		return true;
	}
	
	/**
	 * Teleports the hero from one map to another.
	 * @param	other			The event that triggered this (hero)	
	 */
	protected void teleport(MapEvent other) {
		if (other != SGlobal.hero) return;
		SGlobal.levelManager.getTele().getPre().addListener(new FinishListener() {
			@Override
			public void onFinish() {
				Level newMap = SGlobal.levelManager.getLevel(mapKey);
				Loc to = newMap.getTeleInLoc(parent.getKey());
				SGlobal.levelManager.getTele().teleport(newMap, to.x, to.y);
				SGlobal.levelManager.getTele().getPost().run();
			}
		});
		SGlobal.levelManager.getTele().getPre().run();
	}
	
}
