/**
 *  AbilFxTest.java
 *  Created on Oct 18, 2013 7:02:27 AM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.saga.graphics.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import net.wombatrpgs.saga.core.Constants;
import net.wombatrpgs.saga.core.SGlobal;
import net.wombatrpgs.saga.graphics.Disposable;
import net.wombatrpgs.saga.graphics.Graphic;
import net.wombatrpgs.saga.graphics.PostRenderable;
import net.wombatrpgs.saga.graphics.ShaderFromData;
import net.wombatrpgs.saga.maps.Level;
import net.wombatrpgs.saga.maps.events.MapEvent;
import net.wombatrpgs.saga.screen.Screen;
import net.wombatrpgs.saga.screen.WindowSettings;
import net.wombatrpgs.sagaschema.graphics.ShaderMDO;
import net.wombatrpgs.sagaschema.graphics.effects.EffectRealityMDO;

/**
 * Not a real effect.
 */
public class EffectReality extends Effect implements	PostRenderable,
														Disposable {
	
	protected EffectRealityMDO mdo;
	protected Graphic sphere;
	protected ShaderFromData shader;
	protected FrameBuffer buffer;
	protected SpriteBatch privateBatch;
	protected float elapsed;

	/**
	 * Creates an effect from data, parent.
	 * @param	mdo					The data to generate from
	 * @param	abil				The ability to generate for
	 */
	public EffectReality(Level parent, EffectRealityMDO mdo) {
		super(parent, mdo);
		this.mdo = mdo;
		sphere = new Graphic(Constants.TEXTURES_DIR, mdo.graphic);
		shader = new ShaderFromData(SGlobal.data.getEntryFor(mdo.shader, ShaderMDO.class));
		assets.add(sphere);
		elapsed = 0;
		
		privateBatch = new SpriteBatch();
		privateBatch.setShader(shader);

	}

	/**
	 * @see net.wombatrpgs.saga.graphics.PostRenderable#renderPost()
	 */
	@Override
	public void renderPost() {
		
		Texture t = sphere.getTexture();
		WindowSettings win = SGlobal.window;
		Screen sc = SGlobal.screens.peek();
		float scale = (parent.getTileWidth()*1.5f) / (float) t.getWidth() * 4f;
		
		MapEvent boss = parent.getEventByName("boss");
		if (boss == null) return;
		float atX = boss.getX() + parent.getTileWidth()/2f - t.getWidth()*scale/2f;
		float atY = boss.getY() + parent.getTileHeight()/2f - t.getHeight()*scale/2f;
		
		shader.begin();
		shader.setUniformi("u_mask", 1);
		shader.setUniformf("u_at",
				-(boss.getX() - SGlobal.hero.getX()) - parent.getTileWidth() / 2f,
				-(boss.getY() - SGlobal.hero.getY()) - parent.getTileHeight() / 2f);
		shader.setUniformf("u_screensize", win.getResolutionWidth(), win.getResolutionHeight());
		shader.setUniformf("u_power", .06f);
		shader.setUniformf("u_done", 1.2f * elapsed);
		shader.end();
		
		sc.getBuffer().end();
		buffer.begin();
		sc.getUIBatch().begin();
		sc.getUIBatch().draw(sc.getLastBuffer().getColorBufferTexture(),
				0, 0,
				win.getWidth(), win.getHeight());
		sc.getUIBatch().end();
		buffer.end();
		sc.getBuffer().begin();
		
		privateBatch.setProjectionMatrix(sc.getCamera().combined);
		privateBatch.begin();
		
		t.bind(1);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		
		privateBatch.draw(buffer.getColorBufferTexture(), atX, atY, scale*t.getWidth(), scale*t.getHeight());
		
		privateBatch.end();
	}

	/**
	 * @see net.wombatrpgs.saga.graphics.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		buffer.dispose();
		shader.dispose();
		SGlobal.levelManager.getScreen().removePostRender(this);
	}

	/**
	 * @see net.wombatrpgs.saga.maps.MapThing#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager, int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		super.postProcessing(manager, pass);
		buffer = new FrameBuffer(Format.RGB565, 
				sphere.getWidth(),
				sphere.getHeight(),
				false);
	}

	/**
	 * @see net.wombatrpgs.saga.screen.ScreenObject#onAddedToScreen()
	 */
	@Override
	public void onAddedToScreen() {
		super.onAddedToScreen();
		SGlobal.levelManager.getScreen().registerPostRender(this);
	}

	/**
	 * @see net.wombatrpgs.saga.screen.ScreenObject#onRemovedFromScreen()
	 */
	@Override
	public void onRemovedFromScreen() {
		super.onRemovedFromScreen();
		SGlobal.levelManager.getScreen().removePostRender(this);
	}

	/**
	 * @see net.wombatrpgs.saga.screen.ScreenObject#update(float)
	 */
	@Override
	public void update(float elapsed) {
		if (SGlobal.stasis) return;
		super.update(elapsed);
		this.elapsed += elapsed;
	}

}
