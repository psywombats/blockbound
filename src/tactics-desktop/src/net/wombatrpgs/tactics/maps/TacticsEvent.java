/**
 *  TacticsEvent.java
 *  Created on Feb 12, 2014 2:42:46 AM for project tactics-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.tactics.maps;

import java.util.ArrayList;
import java.util.List;

import net.wombatrpgs.mgne.ai.AStarPathfinder;
import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.mgne.core.interfaces.FinishListener;
import net.wombatrpgs.mgne.maps.Loc;
import net.wombatrpgs.mgne.maps.events.MapEvent;
import net.wombatrpgs.mgneschema.maps.data.OrthoDir;
import net.wombatrpgs.tactics.core.TGlobal;
import net.wombatrpgs.tactics.rpg.TacticsController;
import net.wombatrpgs.tacticsschema.rpg.data.Stat;

/**
 * A TacticsEvent is an event on the map that has a link to a relevant unit in
 * the tactics RPG. That's it. It shouldn't take on the role of the old
 * CharacterEvent. Instead, it should just serve to link the physical to the
 * RPG. This thing is created anew every time a new battle starts by passing
 * it a game unit to take. If there's a unit on the map that's supposed to be
 * the "hero" or whatever, it should get taken out before battle.
 */
public class TacticsEvent extends MapEvent {
	
	protected TacticsController controller;
	protected List<OrthoDir> path;
	protected FinishListener movementFinishListener;

	/**
	 * Constructs a new TacticsEvent given a GameUnit. Really shouldn't be
	 * called by anything but a GameUnit when it gets created.
	 * @param	unit			The unit this event is for
	 */
	public TacticsEvent(TacticsController unit) {
		super(unit.extractEventMDO());
		this.controller = unit;
	}
	
	/** @return The game unit this event represents */
	public TacticsController getUnit() { return controller; }

	/**
	 * @see net.wombatrpgs.mgne.maps.events.MapEvent#update(float)
	 */
	@Override
	public void update(float elapsed) {
		super.update(elapsed);
		if (path != null && !isTracking()) {
			if (path.size() > 0) {
				OrthoDir step = path.get(0);
				setFacing(step);
				path.remove(0);
				targetLocation(
						x + step.getVector().x * parent.getTileWidth(),
						y + step.getVector().y * parent.getTileHeight());
				vx = step.getVector().x * (parent.getTileWidth() / MGlobal.constants.getDelay());
				vy = step.getVector().y * (parent.getTileHeight() / MGlobal.constants.getDelay());
				tileX += step.getVector().x;
				tileY += step.getVector().y;
			} else {
				path = null;
				if (movementFinishListener != null) {
					movementFinishListener.onFinish();
					movementFinishListener = null;
				}
			}
		}
	}

	/**
	 * Calculates everywhere this unit could step next turn.
	 * @return					A list of viable step locations
	 */
	public List<Loc> getMoveRange() {
		int move = Math.round(controller.getUnit().stat(Stat.MOVE_RANGE));
		List<Loc> availableSquares = new ArrayList<Loc>();
		AStarPathfinder pather = new AStarPathfinder();
		pather.setMap(parent);
		pather.setStart(tileX, tileY);
		for (int x = tileX - move; x <= tileX + move; x += 1) {
			for (int y = tileY - move; y <= tileY + move; y += 1) {
				if (x < 0 || x >= parent.getWidth()) continue;
				if (y < 0 || y >= parent.getHeight()) continue;
				if (this.tileDistanceTo(x, y) > move) {
					continue;
				}
				pather.setTarget(x, y);
				List<OrthoDir> path = pather.getOrthoPath(this);
				if (path != null && path.size() <= move) {
					availableSquares.add(new Loc(x, y));
				}
			}
		}
		return availableSquares;
	}
	
	/**
	 * Called when this unit is killed.
	 */
	public void onDeath() {
		remove();
	}
	
	/**
	 * Removes this unit from the map it's currently on.
	 */
	public void remove() {
		controller.getMap().removeDoll(this);
	}
	
	/**
	 * Attempts to move to where the cursor is as part of our turn. Fails if
	 * there is no path to the cursor.
	 * @param	listener		The method to call when movement completes
	 * @return					True if there was a path, false otherwise
	 */
	public boolean attemptFollowCursor(FinishListener listener) {
		this.movementFinishListener = listener;
		int targetX = TGlobal.ui.getCursor().getTileX();
		int targetY = TGlobal.ui.getCursor().getTileY();
		AStarPathfinder pather = new AStarPathfinder(parent,
				getTileX(), getTileY(),
				targetX, targetY);
		path = pather.getOrthoPath(this);
		return (path != null);
	}

}
