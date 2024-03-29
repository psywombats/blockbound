/**
 *  EnemyVenustron.java
 *  Created on Mar 14, 2013 4:37:37 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.characters.instances;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.wombatrpgs.rainfall.characters.CharacterEvent;
import net.wombatrpgs.rainfall.characters.enemies.EnemyEvent;
import net.wombatrpgs.rainfall.core.Queueable;
import net.wombatrpgs.rainfall.core.RGlobal;
import net.wombatrpgs.rainfall.graphics.FacesAnimation;
import net.wombatrpgs.rainfall.graphics.FacesAnimationFactory;
import net.wombatrpgs.rainfall.io.audio.SoundObject;
import net.wombatrpgs.rainfall.maps.Level;
import net.wombatrpgs.rainfall.maps.Positionable;
import net.wombatrpgs.rainfall.maps.events.AnimationPlayer;
import net.wombatrpgs.rainfall.maps.events.MapEvent;
import net.wombatrpgs.rainfall.physics.CollisionResult;
import net.wombatrpgs.rainfall.physics.Hitbox;
import net.wombatrpgs.rainfall.physics.NoHitbox;
import net.wombatrpgs.rainfall.physics.RectHitbox;
import net.wombatrpgs.rainfallschema.audio.SoundMDO;
import net.wombatrpgs.rainfallschema.characters.enemies.EnemyEventMDO;
import net.wombatrpgs.rainfallschema.graphics.AnimationMDO;
import net.wombatrpgs.rainfallschema.maps.data.Direction;

/**
 * The level 1 boss, obviously.
 */
public class EnemyVenustron extends EnemyEvent {
	
	private static final String KEY_4DIR_VERT = "venustron_vert_4dir";
	private static final String KEY_4DIR_HORIZ = "venustron_horiz_4dir";
	private static final String KEY_4DIR_NORTHEAST = "venustron_northeast_4dir";
	private static final String KEY_4DIR_NORTHWEST = "venustron_northwest_4dir";
	private static final String KEY_ANIM_SPARKS = "animation_sparks";
	private static final String KEY_SFX_MOVEMENT = "sound_venustron_move";
	private static final String KEY_SFX_SHOOT = "sound_venustron_shoot";
	
	private List<Queueable> assets;
	private FacesAnimation appearanceVert, appearanceHoriz;
	private FacesAnimation turnNortheast, turnNorthwest;
	private SoundObject sfxMove, sfxShoot;
	
	private Hitbox offBox;
	private float hitX, hitY;
	
	private static final float SWIVEL_TIME = .25f; // in s
	private Direction travelDir;
	private float swiveled;
	private boolean swiveling;
	
	private static final float LASER_RANGE = 48; // px from base
	private MapEvent laser;
	private ShapeRenderer shapes;
	private AnimationPlayer sparks;
	private boolean lasering;

	public EnemyVenustron(EnemyEventMDO mdo, TiledObject object, Level parent, int x, int y) {
		super(mdo, object, parent, x, y);
		assets = new ArrayList<Queueable>();
		appearanceHoriz = FacesAnimationFactory.create(KEY_4DIR_HORIZ, this);
		appearanceVert = FacesAnimationFactory.create(KEY_4DIR_VERT, this);
		turnNortheast = FacesAnimationFactory.create(KEY_4DIR_NORTHEAST, this);
		turnNorthwest = FacesAnimationFactory.create(KEY_4DIR_NORTHWEST, this);
		sparks = new AnimationPlayer(RGlobal.data.getEntryFor(KEY_ANIM_SPARKS, AnimationMDO.class));
		sfxMove = new SoundObject(RGlobal.data.getEntryFor(KEY_SFX_MOVEMENT, SoundMDO.class), this);
		sfxShoot = new SoundObject(RGlobal.data.getEntryFor(KEY_SFX_SHOOT, SoundMDO.class), this);
		assets.add(appearanceHoriz);
		assets.add(appearanceVert);
		assets.add(turnNorthwest);
		assets.add(turnNortheast);
		assets.add(sparks);
		assets.add(sfxMove);
		assets.add(sfxShoot);
		final Positionable me = this;
		offBox = new RectHitbox(new Positionable() {
			@Override public float getX() { return me.getX() - 16; }
			@Override public float getY() { return me.getY(); }
		}, 17, 1, 47, 31);
		zero();
		shapes = new ShapeRenderer();
		final EnemyVenustron venus = this;
		laser = new MapEvent() {
			@Override
			public boolean requiresChunking() {
				return false;
			}
			@Override
			public void render(OrthographicCamera camera) {
				super.render(camera);
				float x1 = venus.x+16;
				float y1 = venus.y+72;
				float x2 = targetX();
				float y2 = targetY();
				if (venus.getFacing() == Direction.RIGHT) x1 += 6;
				if (venus.getFacing() == Direction.LEFT) x1 -= 6;
				getLevel().getBatch().end();
				shapes.setProjectionMatrix(camera.combined);
				shapes.begin(ShapeType.Line);
				for (int off = -2; off <= 2; off++) {
					float rg = 1 - (Math.abs(off)+1) / 3;
					shapes.setColor(rg, rg, 1, 1);
					shapes.line(x1 + off, y1, x2 + off, y2);
				}
				shapes.end();
				getLevel().getBatch().begin();
			}
			
		};
	}

	@Override
	public void queueRequiredAssets(AssetManager manager) {
		super.queueRequiredAssets(manager);
		for (Queueable asset : assets) {
			asset.queueRequiredAssets(manager);
		}
	}

	@Override
	public void postProcessing(AssetManager manager, int pass) {
		super.postProcessing(manager, pass);
		for (Queueable asset : assets) {
			asset.postProcessing(manager, pass);
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		zero();
	}

	@Override
	public void update(float elapsed) {
		super.update(elapsed);
		if (!RGlobal.hero.isSet("go_venustron")) return;
		
		if (targetVX != 0 && targetVY != 0) {
			float deltaX = Math.abs(getX() - targetX);
			//float deltaY = Math.abs(getY() - targetY);
			if (deltaX > 0) {
				targetVY = 0;
			} else {
				targetVX = 0;
			}
		}
		if (targetVX == 0 || targetVY != 0) vx = 0;
		if (targetVY == 0 || targetVX != 0) vy = 0;
		
		// HORIZONTAL SWIVELING
		if (Math.abs(targetVX) > 0) {
			if (travelDir == Direction.RIGHT || travelDir == Direction.LEFT) {
				vx = targetVX;
				setAppearance(appearanceHoriz);
				if (targetVX > 0) travelDir = Direction.RIGHT;
				else travelDir = Direction.LEFT;
			} else {
				if (swiveling) {
					if (swiveled > SWIVEL_TIME) {
						swiveling = false;
						if (targetVX > 0) travelDir = Direction.RIGHT;
						else travelDir = Direction.LEFT;
					} else {
						swiveled += elapsed;
					}
				} else {
					swiveling = true;
					sfxMove.play();
					swiveled = 0;
					if (targetVX > 0) {
						if (travelDir == Direction.DOWN) setAppearance(turnNorthwest);
						else setAppearance(turnNortheast);
					} else {
						if (travelDir == Direction.UP) setAppearance(turnNorthwest);
						else setAppearance(turnNortheast);
					}
				}
			}
		}
		
		// VERTICAL SWIVEL
		if (Math.abs(targetVY) > 0) {
			if (travelDir == Direction.UP || travelDir == Direction.DOWN) {
				vy = targetVY;
				setAppearance(appearanceVert);
				if (targetVY > 0) travelDir = Direction.UP;
				else travelDir = Direction.DOWN;
			} else {
				if (swiveling) {
					if (swiveled > SWIVEL_TIME) {
						swiveling = false;
						if (targetVY > 0) travelDir = Direction.UP;
						else travelDir = Direction.DOWN;
					} else {
						swiveled += elapsed;
					}
				} else {
					swiveling = true;
					sfxMove.play();
					swiveled = 0;
					if (targetVY > 0) {
						if (travelDir == Direction.LEFT) setAppearance(turnNorthwest);
						else setAppearance(turnNortheast);
					} else {
						if (travelDir == Direction.RIGHT) setAppearance(turnNorthwest);
						else setAppearance(turnNortheast);
					}
				}
			}
		}
		
		x += vx * elapsed;
		y += vy * elapsed;
		if (tracking && ((lastX < targetX && x > targetX) || (lastX > targetX && x < targetX))) {
			x = targetX;
		}
		if (tracking && ((lastY < targetY && y > targetY) || (lastY > targetY && y < targetY))) {
			y = targetY;
		}
		if (tracking && x == targetX && y == targetY) {
			tracking = false;
		}
		lastX = x;
		lastY = y;
		
		setFacing(directionTo(RGlobal.hero));
		
		float dist = heroDist();
		if (!lasering && canAct() && dist < LASER_RANGE * 1.6) {
			lasering = true;
			if (!getLevel().contains(sparks)) {
				if (getFacing() == Direction.UP) {
					getLevel().addEvent(laser, 0);
					getLevel().addEvent(sparks, 0);
				} else {
					getLevel().addEvent(laser, 2);
					getLevel().addEvent(sparks, 2);
				}
				sparks.start();
				sfxShoot.play();
			}
		}
		if (lasering && dist > LASER_RANGE * 1.8) {
			lasering = false;
			getLevel().removeObject(sparks);
			getLevel().removeEvent(laser);
		}
		if (lasering && dist < LASER_RANGE) {
			RGlobal.hero.die();
		}
		if (lasering) {
			sparks.setX(targetX()-8);
			sparks.setY(targetY());
		}
	}

	@Override
	public void setFacing(Direction dir) {
		appearanceHoriz.setFacing(dir);
		appearanceVert.setFacing(dir);
		turnNorthwest.setFacing(dir);
		turnNortheast.setFacing(dir);
		appearanceVert.startMoving();
		appearanceHoriz.startMoving();
	}

	@Override
	public boolean onCharacterCollide(CharacterEvent other, CollisionResult result) {
		if (other == RGlobal.block) {
			if (other.getX() != hitX || other.getY() != hitY) {
				tracking = false;
				hitX = other.getX();
				hitY = other.getY();
			}
		}
		return super.onCharacterCollide(other, result);
	}

	@Override
	public int getRenderX() {
		return super.getRenderX() - 16;
	}
	@Override
	public Hitbox getHitbox() {
		if (hidden()) return NoHitbox.getInstance();
		return offBox;
	}

	@Override
	protected void integrate(float elapsed) {
		if (!RGlobal.hero.isSet("go_venustron")) super.integrate(elapsed);
		// yeah no
	}

	@Override
	protected void storeXY() {
		if (!RGlobal.hero.isSet("go_venustron")) super.storeXY();
	}

	private void zero() {
		travelDir = Direction.UP;
		swiveled = 0;
		swiveling = false;
		tracking = false;
	}
	
	private float heroDist() {
		float dx = (getX()) - RGlobal.hero.getX();
		float dy = (getY() - 8) - RGlobal.hero.getY();
		return (float) Math.sqrt(dx * dx + dy * dy);
	}
	
	private float targetX() {
		float angle = (float) Math.atan2(
				getY() - RGlobal.hero.getY(), getX() - (RGlobal.hero.getX()));
		float dist = heroDist();
		dist = (dist > LASER_RANGE) ? LASER_RANGE : heroDist();
		return getX() - (float) (Math.cos(angle) * dist) + 10;
	}
	
	private float targetY() {
		float angle = (float) Math.atan2(
				getY() - RGlobal.hero.getY(), getX() - (RGlobal.hero.getX()));
		float dist = heroDist();
		dist = (dist > LASER_RANGE) ? LASER_RANGE : heroDist();
		return getY() - (float) (Math.sin(angle) * dist);
	}

}
