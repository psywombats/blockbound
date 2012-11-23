package net.wombatrpgs.rainfall;

import net.wombatrpgs.rainfall.maps.Level;
import net.wombatrpgs.rainfallschema.graphics.AnimationMDO;
import net.wombatrpgs.rainfallschema.maps.MapMDO;
import net.wombatrpgs.rainfallschema.test.MapLoadTestMDO;
import net.wombatrpgs.rainfallschema.test.SpriteRenderTestMDO;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

public class RainfallGame implements ApplicationListener {
	private OrthographicCamera camera;
	private Level map;
	private Rectangle glViewport;
	private AnimationMDO animMDO;
	
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(w, h);
		camera.position.set(w/2, h/2, 0);
		glViewport = new Rectangle(0, 0, w, h);
		
		SpriteRenderTestMDO testMDO = (SpriteRenderTestMDO) RGlobal.data.getEntryByKey("anim_test");
		animMDO = (AnimationMDO) RGlobal.data.getEntryByKey(testMDO.anim);
		RGlobal.reporter.inform("We're trying to load from " + RGlobal.SPRITES_DIR + animMDO.file);	
		RGlobal.assetManager.load(RGlobal.SPRITES_DIR + animMDO.file, Texture.class);
		
		MapLoadTestMDO mapTestMDO = (MapLoadTestMDO) RGlobal.data.getEntryByKey("map_test");
		MapMDO mapMDO = (MapMDO) RGlobal.data.getEntryByKey(mapTestMDO.map);
		map = new Level(mapMDO);

	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// camera
		GL10 gl = Gdx.graphics.getGL10();
		gl.glViewport((int) glViewport.x, (int) glViewport.y,
				(int) glViewport.width, (int) glViewport.height);
		camera.update();
		camera.apply(gl);
		if (TimeUtils.millis() % 50 == 0) {
			System.out.println("FPS: " + (1/Gdx.graphics.getDeltaTime()));
		}

		if (RGlobal.assetManager.update()) {
			if (map != null) {
				map.render(camera);
			}
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}