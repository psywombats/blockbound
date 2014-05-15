/**
 *  EffectFog.java
 *  Created on Apr 18, 2013 11:27:12 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mgne.graphics.effects;

import java.util.List;

import net.wombatrpgs.mgne.core.MAssets;
import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.mgne.graphics.AnimationStrip;
import net.wombatrpgs.mgne.graphics.ShaderFromData;
import net.wombatrpgs.mgne.maps.Level;
import net.wombatrpgs.mgne.maps.events.MapEvent;
import net.wombatrpgs.mgne.screen.TrackerCam;
import net.wombatrpgs.mgne.screen.WindowSettings;
import net.wombatrpgs.mgneschema.graphics.AnimationMDO;
import net.wombatrpgs.mgneschema.graphics.ShaderMDO;
import net.wombatrpgs.mgneschema.graphics.effects.EffectFogMDO;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Fog graphical effect.
 */
@Deprecated
public class EffectFog extends Effect {
	
	protected static int MAX_BEACONS = 50;
	
	protected EffectFogMDO mdo;
	protected AnimationStrip anim;
	protected ShaderProgram shader;
	protected float offset;
	
	/**
	 * Inherited constructor.
	 * @param	parent			The parent level
	 * @param 	mdo				The data to construct effect from
	 */
	public EffectFog(Level parent, EffectFogMDO mdo) {
		super(parent, mdo);
		this.mdo = mdo;
		anim = new AnimationStrip(MGlobal.data.getEntryFor(mdo.tex, AnimationMDO.class));
		offset = 0;
		batch.setColor(1, 1, 1, mdo.density);
		shader = new ShaderFromData(MGlobal.data.getEntryFor(mdo.shader, ShaderMDO.class));
		batch.setShader(shader);
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.interfaces.Renderable#render
	 * (com.badlogic.gdx.graphics.g2d.SpriteBatch)
	 */
	@Override
	public void render(SpriteBatch argBatch) {
		WindowSettings win = MGlobal.window;
		TrackerCam cam = MGlobal.levelManager.getScreen().getCamera();
		TextureRegion tex = anim.getCurrentFrame();
		
		batch.setProjectionMatrix(argBatch.getProjectionMatrix());
		batch.begin();
		float atX = cam.position.x - win.getWidth()/2;
		float atY = cam.position.y - win.getHeight()/2;
		shader.setUniformf("u_atX", atX);
		shader.setUniformf("u_atY", atY);
		
		// forward +wrap
		batch.draw(
				tex,
				offset,
				0,
				win.getWidth() / 2,
				win.getHeight() / 2, 
				win.getWidth(),
				win.getHeight(),
				1,
				1,
				0);
		batch.draw(
				tex,
				offset - win.getWidth(), 
				0,
				win.getWidth() / 2,
				win.getHeight() / 2, 
				win.getWidth(),
				win.getHeight(),
				1,
				1,
				0);
		
		// mirror + wrap
		batch.draw(
				tex,
				-offset, 
				0,
				win.getWidth() / 2,
				win.getHeight() / 2, 
				win.getWidth(),
				win.getHeight(),
				1,
				1,
				180);
		batch.draw(
				tex,
				win.getWidth() - offset, 
				0,
				win.getWidth() / 2,
				win.getHeight() / 2, 
				win.getWidth(),
				win.getHeight(),
				1,
				1,
				180);
		
		batch.end();
	}

	/**
	 * @see net.wombatrpgs.mgne.core.interfaces.Queueable#queueRequiredAssets
	 * (MAssets)
	 */
	@Override
	public void queueRequiredAssets(MAssets manager) {
		anim.queueRequiredAssets(manager);
	}

	/**
	 * @see net.wombatrpgs.mgne.core.interfaces.Queueable#postProcessing
	 * (MAssets, int)
	 */
	@Override
	public void postProcessing(MAssets manager, int pass) {
		anim.postProcessing(manager, pass);
		
		List<MapEvent> beacons = parent.getEventsByGroup("antifog_beacon");
		float beaconCoords[] = new float[beacons.size() * 2];
		for (int i = 0; i < beacons.size(); i++) {
			MapEvent beacon = beacons.get(i);
			beaconCoords[i*2] = beacon.getX();
			beaconCoords[i*2+1] = beacon.getY();
		}
		shader.begin();
		shader.setUniform2fv("u_beacons", beaconCoords, 0, beaconCoords.length);
		shader.setUniformi("u_beaconCount", beaconCoords.length/2);
		shader.setUniformi("u_radius", mdo.radius);
		shader.setUniformf("u_factor", mdo.factor);
		shader.setUniformf("u_exp", mdo.exponent);
		shader.end();
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.effects.Effect#update(float)
	 */
	@Override
	public void update(float elapsed) {
		super.update(elapsed);
		anim.update(elapsed);
		offset += mdo.velocity * elapsed;
		if (offset > MGlobal.window.getWidth()) {
			offset -= MGlobal.window.getWidth();
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.effects.Effect#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		shader.dispose();
	}

}
