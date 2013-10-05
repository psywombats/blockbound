/**
 *  MapEvent.java
 *  Created on Dec 24, 2012 2:41:32 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mrogue.maps.events;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.wombatrpgs.mrogue.characters.CharacterEvent;
import net.wombatrpgs.mrogue.core.MGlobal;
import net.wombatrpgs.mrogue.graphics.PreRenderable;
import net.wombatrpgs.mrogue.maps.Level;
import net.wombatrpgs.mrogue.maps.MapThing;
import net.wombatrpgs.mrogue.maps.PositionSetable;
import net.wombatrpgs.mrogue.maps.layers.EventLayer;
import net.wombatrpgs.mrogueschema.maps.data.Direction;

/**
 * A map event is any map object defined in Tiled, including characters and
 * teleports and other fun stuff. Revised as of 2012-01-30 to be anything that
 * exists on a Tiled layer, even if it wasn't created in Tiled itself.
 * 
 * MR: MapEvent is anything that exists in the world of tiles, as opposed to a
 * thing that just lives on a map. It isn't necessarily a character
 */
public abstract class MapEvent extends MapThing implements	PositionSetable,
															PreRenderable {
	
	/** A thingy to fool the prerenderable, a sort of no-appear flag */
	protected static final TextureRegion NO_APPEARANCE = null;
	
	/** Is this object hidden from view/interaction due to cutscene? */
	protected boolean commandHidden;
	protected boolean switchHidden;
	/** Another toggle on our visibility - if it exists, link it to hidden */
	protected String showSwitch;
	protected String hideSwitch;
	
	/** Coords in pixels relative to map origin */
	protected float x, y;
	/** Coords in tiles, (0,0) is upper left */
	protected int tileX, tileY;
	/** Velocity the object is currently moving at in pixels/second */
	protected float vx, vy;
	/** Are we currently moving towards some preset destination? */
	protected boolean tracking;
	/** The place we're possibly moving for */
	protected float targetX, targetY;
	/** Gotta keep track of these for some reason (tracking reasons!) */
	protected float lastX, lastY;

	/**
	 * Creates a new map event for the level at the origin.
	 * @param 	parent		The parent level of the event
	 */
	protected MapEvent(Level parent) {
		super(parent);
		zeroCoords();
		// TODO: MapEvent
	}
	
	/**
	 * Creates a blank map event associated with no map. Assumes the subclass
	 * will do something interesting in its constructor.
	 */
	protected MapEvent() {
		zeroCoords();
	}
	
	/** @see net.wombatrpgs.mrogue.maps.Positionable#getX() */
	@Override
	public float getX() { return x; }

	/** @see net.wombatrpgs.mrogue.maps.Positionable#getY() */
	@Override
	public float getY() { return y; }

	/** @see net.wombatrpgs.mrogue.maps.PositionSetable#setX(int) */
	@Override
	public void setX(float x) { this.x = x; }

	/** @see net.wombatrpgs.mrogue.maps.PositionSetable#setY(int) */
	@Override
	public void setY(float y) { this.y = y; }
	
	/** @param tileX The new x-coord of this event (in tiles) */
	public void setTileX(int tileX) { this.tileX = tileX; }
	
	/** @param tileY The new y-coord of this event (in tiles) */
	public void setTileY(int tileY) { this.tileY = tileY; }
	
	/** @return x-coord of the center of this object, in px */
	public float getCenterX() { return x; }
	
	/** @return y-coord of the center of this object, in px */
	public float getCenterY() { return y; }
	
	/** @return x-coord of this object, in tiles */
	public int getTileX() { return tileX; }
	
	/** @return y-coord of this object, in tiles */
	public int getTileY() { return tileY; }
	
	/** @return The x-velocity of this object, in px/s */
	public float getVX() { return this.vx; }
	
	/** @return The y-velocity of this object, in px/s */
	public float getVY() { return this.vy; }
	
	/** @param f The offset to add to x */
	public void moveX(float f) { this.x += f; }
	
	/** @param y The offset to add to x */
	public void moveY(float g) { this.y += g; }
	
	/** @return True if this object is moving towards a location */
	public boolean isTracking() { return tracking; }

	
	/**
	 * Determines if this object is "stuck" or not. This means it's tracking
	 * but hasn't moved much at all.
	 * @return					True if the event is stuck, false otherwise
	 */
	public boolean isStuck() {
		return 	isTracking() &&
				Math.abs(lastX - x) < Math.abs(vx) / 2.f &&
				Math.abs(lastY - y) < Math.abs(vy) / 2.f;
	}
	
	/** @see net.wombatrpgs.mrogue.graphics.PreRenderable#getRenderX() */
	@Override
	public int getRenderX() { return (int) getX(); }

	/** @see net.wombatrpgs.mrogue.graphics.PreRenderable#getRenderY() */
	@Override
	public int getRenderY() { return (int) getY(); }

	/**
	 * Default is inivisible.
	 * @see net.wombatrpgs.mrogue.graphics.PreRenderable#getRegion()
	 */
	@Override
	public TextureRegion getRegion() {
		return NO_APPEARANCE;
	}
	
	/**
	 * Stops this event from a period of pathing towards its logical next
	 * turn position by permanently setting it to is next turn positon.
	 */
	public void stopMoving() {
		x = tileX * parent.getTileWidth();
		y = tileY * parent.getTileHeight();
		halt();
	}
	
	/**
	 * Update yoself! This is called from the rendering loop but it's with some
	 * filters set on it for target framerate. As of 2012-01-30 it's not called
	 * from the idiotic update loop.
	 * @param 	elapsed			Time elapsed since last update, in seconds
	 */
	public void update(float elapsed) {
		super.update(elapsed);
		
		if (Float.isNaN(vx) || Float.isNaN(vy)) {
			MGlobal.reporter.warn("NaN values in physics!! " + this);
		}
		integrate(elapsed);
		if (tracking) {
			if ((x < targetX && lastX > targetX) || (x > targetX && lastX < targetX)) {
				x = targetX;
				vx = 0;
			}
			if ((y < targetY && lastY > targetY) || (y > targetY && lastY < targetY)) {
				y = targetY;
				vy = 0;
			}
			if (x == targetX && y == targetY) {
				tracking = false;
			}
		}
		storeXY();
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.maps.MapThing#render
	 * (com.badlogic.gdx.graphics.OrthographicCamera)
	 */
	@Override
	public void render(OrthographicCamera camera) {
		if (hidden()) return;
		super.render(camera);
	}
	
	/**
	 * Sets the hide status of this map event via event command. Hidden events
	 * do not update or interact with other events. It's a way of having objects
	 * on the map but not using them until they're needed.
	 * @param 	hidden			True to hide the event, false to reveal it
	 */
	public void setCommandHidden(boolean hidden) {
		this.commandHidden = hidden;
	}
	
	/**
	 * Gets the name of this event as specified in Tiled. Null if the event is
	 * unnamed in tiled or was not created from tiled.
	 * @return
	 */
	public String getName() {
		// TODO: getName
		return "TODO: getName";
	}
	
	/**
	 * Checks if this event's in a specific group. Events can belong to multiple
	 * groups if their group name contains the separator character.
	 * @param 	groupName		The name of the group we may be in
	 * @return					True if in that group, false if not
	 */
	public boolean inGroup(String groupName) {
		// TODO: inGroup
		return false;
	}

	/**
	 * @see net.wombatrpgs.mrogue.maps.MapThing#renderLocal
	 * (com.badlogic.gdx.graphics.OrthographicCamera,
	 * com.badlogic.gdx.graphics.g2d.TextureRegion, int, int, int)
	 */
	public void renderLocal(OrthographicCamera camera, TextureRegion sprite, 
			int offX, int offY, int angle) {
		super.renderLocal(camera, sprite, getRenderX() + offX, getRenderY() + offY, 
				angle, 0);
	}
	
	/**
	 * Uses this event's x/y to render locally.
	 * @see net.wombatrpgs.mrogue.maps.MapThing#renderLocal
	 * (com.badlogic.gdx.graphics.OrthographicCamera, 
	 * com.badlogic.gdx.graphics.g2d.TextureRegion, int, int)
	 */
	public void renderLocal(OrthographicCamera camera, TextureRegion sprite) {
		super.renderLocal(camera, sprite, (int) getX(), (int) getY(), 0);
	}

	/**
	 * Determines if this object is currently in motion.
	 * @return					True if the object is moving, false otherwise
	 */
	public boolean isMoving() {
		return vx != 0 || vy != 0;
	}
	
	/**
	 * Determines if this object is passable by characters or not.
	 * @return					True if the object is passable, false otherwise
	 */
	public boolean isPassable() {
		return true;
	}
	
	/**
	 * Stops all movement in a key-friendly way.
	 */
	public void halt() {
		vx = 0;
		vy = 0;
	}
	
	/**
	 * Gets the y were we sort at. This is for relative positioning with the z-
	 * layer. Used for above/below hero in subclasses. By default is y-coord.
	 * @return
	 */
	public float getSortY() {
		return getY();
	}
	
	/**
	 * Gives this map object a new target to track towards.
	 * @param 	targetX		The target location x-coord (in px)
	 * @param 	targetY		The target location y-coord (in px)
	 */
	public void targetLocation(float targetX, float targetY) {
		this.targetX = targetX;
		this.targetY = targetY;
		this.tracking = true;
	}
	
	/**
	 * Updates the effective velocity of this map object.
	 * @param 	vx			The new x-velocity of the object, in px/s
	 * @param 	vy			The new y-velocity of the object, in px/s
	 */
	public void setVelocity(float vx, float vy) {
		this.vx = vx;
		this.vy = vy;
	}
	
	/**
	 * Calculates distance. This is like 7th grade math class here.
	 * @param 	other			The other object in the calculation
	 * @return					The distance between this and other, in pixels
	 */
	public float distanceTo(MapEvent other) {
		float dx = other.x - x;
		float dy = other.y - y;
		return (float) Math.sqrt(dx*dx + dy*dy);
	}
	
	/**
	 * Calculates the direction towards some other map event.
	 * @param 	event			The event to get direction towards
	 * @return					The direction towards that event
	 */
	public Direction directionTo(MapEvent event) {
		return directionTo(event.getX(), event.getY());
	}
	
	/**
	 * Calculates the direction towards some tile on the map.
	 * @param	tileX			The x-coord to face towards (in tiles)
	 * @param	tileY			The y-coord to face towards (in tiles)
	 * @return					The direction to that tile
	 */
	public Direction directionToTile(int tileX, int tileY) {
		return directionTo(tileX * parent.getTileWidth(), tileY * parent.getTileHeight());
	}
	
	/**
	 * Calculates the direction towards some point on the map.
	 * @param	x				The x-coord to face towards (in gamespace px)
	 * @param	y				The y-coord to face towards (in gamespace px)
	 * @return
	 */
	public Direction directionTo(float x, float y) {
		float dx = x - this.getX();
		float dy = y - this.getY();
		if (Math.abs(dx) > Math.abs(dy)) {
			if (dx > 0) {
				return Direction.RIGHT;
			} else {
				return Direction.LEFT;
			}
		} else {
			if (dy > 0) {
				return Direction.UP;
			} else {
				return Direction.DOWN;
			}
		}
	}
	
	/**
	 * Called when this object is added to an object layer. Nothing by default.
	 * @param 	layer			The layer this object is being added to
	 */
	public void onAdd(EventLayer layer) {
		this.lastX = getX();
		this.lastY = getY();
		// nothing by default
	}
	
	/**
	 * Called when this object begins updating its position in the move phase.
	 */
	public void onMoveStart() {
		int tWidth = parent.getTileWidth();
		int tHeight = parent.getTileHeight();
		targetLocation(tileX * tWidth, tileY * tHeight);
		float vx = (tileX*tWidth - x) / parent.getMoveTimeLeft();
		float vy = (tileY*tHeight - y) / parent.getMoveTimeLeft();
		setVelocity(vx, vy);
	}
	
	/**
	 * What happens when a character moves into this event? By default, nothing
	 * happens, but characters should be attacked, items should be auto-grabbed,
	 * and so on.
	 * @param	character		The jerk that ran into us
	 */
	public void collideWith(CharacterEvent character) {
		// default is nothing
	}
	
	/**
	 * Calculates how long this event has to go before taking an action of its
	 * own. The default never acts and just returns a large number.
	 * @return					The number of ticks to go before moving
	 */
	public int ticksToAct() {
		// most game actions should be like 1000 tops
		return 10000;
	}
	
	/**
	 * Simulate the passage of a certain number of game ticks. This usually just
	 * deducts an amount from the cost to move.
	 * @param	ticks			The number of game ticks elapsed
	 */
	public void simulateTime(int ticks) {
		// default is nothing
	}
	
	/**
	 * Do whatever it is you want to do on this turn. Default does nothing.
	 * Events that care about energy costs etc should update their time to next
	 * move here.
	 * @return					How many game ticks spent this turn
	 */
	public void act() {
		// default is nothing
	}
	
	/**
	 * Determines if an event is "hidden" either by switch or command.
	 * @return					True if the event is hidden, false otherwise
	 */
	protected boolean hidden() {
		return commandHidden || switchHidden;
	}
	
	/**
	 * Does some constructor-like stuff to reset physical variables.
	 */
	protected void zeroCoords() {
		x = 0;
		y = 0;
		vx = 0;
		vy = 0;
	}
	
	/**
	 * Applies the physics integration for a timestep.
	 * @param 	elapsed			The time elapsed in that timestep
	 */
	protected void integrate(float elapsed) {
		x += vx * elapsed;
		y += vy * elapsed;
	}
	
	/**
	 * Updates last x/y.
	 */
	protected void storeXY() {
		lastX = x;
		lastY = y;
	}

}
