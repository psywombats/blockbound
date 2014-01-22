package net.wombatrpgs.saga.desktop;

import net.wombatrpgs.saga.core.SagaGame;
import net.wombatrpgs.saga.core.Platform;
import net.wombatrpgs.saga.core.Reporter;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * Autogenerated by setup UI. This is the updated version for the new Saga
 * project.
 */
public class Main {
	
	public static final String WARMUP_NAME = "One moment please...";
	public static final int WARMUP_WIDTH = 320;
	public static final int WARMUP_HEIGHT = 240;
	
	/**
	 * Main launcher. Autogenerated at one point.
	 * @param 	args		Unusused
	 */
	public static void main(String[] args) {
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = WARMUP_NAME;
		cfg.useGL20 = true;
		cfg.width = WARMUP_WIDTH;
		cfg.height = WARMUP_HEIGHT;
		cfg.stencil = 8;
		cfg.resizable = false;
		// dammit I want the old seticon back!!
		cfg.addIcon("res/ui/icon_128.png", FileType.Internal);
		cfg.addIcon("res/ui/icon_32.png", FileType.Internal);
		cfg.addIcon("res/ui/icon_16.png", FileType.Internal);
		
		new LwjglApplication(new SagaGame(new Platform() {
			@Override public Reporter getReporter() {
				return new PrintReporter();
			}
		}), cfg);
	}
}
