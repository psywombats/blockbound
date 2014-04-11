/**
 *  CharaSelector.java
 *  Created on Apr 6, 2014 3:56:39 PM for project saga-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.saga.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.mgne.graphics.ScreenGraphic;
import net.wombatrpgs.mgne.io.CommandListener;
import net.wombatrpgs.mgne.io.command.CMapMenu;
import net.wombatrpgs.mgne.ui.Graphic;
import net.wombatrpgs.mgne.ui.Nineslice;
import net.wombatrpgs.mgneschema.io.data.InputCommand;
import net.wombatrpgs.saga.CharacterInsert;
import net.wombatrpgs.saga.core.SGlobal;
import net.wombatrpgs.saga.rpg.Chara;

/**
 * Instead of selecting options, it selects characters. This should only really
 * work on the hero party, so it takes it by default.
 */
public class CharaSelector extends ScreenGraphic implements CommandListener {
	
	// layout
	protected static final int INSERTS_MARGIN = 4;
	protected static final int INSERTS_COUNT_HORIZ = 2;
	protected static final int INSERTS_COUNT_VERT = 3;
	
	// calculated
	protected int insertsWidth, insertsHeight;
	protected List<CharacterInsert> inserts;
	protected Nineslice bg;
	
	// cursor
	protected boolean cursorOn;
	protected boolean cancellable;
	protected int selectedX, selectedY;
	protected float cursorX, cursorY;
	protected SelectionListener listener;
	
	/**
	 * Creates a new character selector using the party for options.
	 */
	public CharaSelector() {
		insertsWidth = CharacterInsert.WIDTH;
		insertsHeight = CharacterInsert.HEIGHT;
		insertsWidth *= INSERTS_COUNT_HORIZ;
		insertsHeight *= INSERTS_COUNT_VERT;
		insertsWidth += 2 * INSERTS_MARGIN;
		insertsHeight += 2 * (INSERTS_MARGIN+1);
		
		bg = new Nineslice();
		assets.add(bg);
	}

	/**
	 * @see net.wombatrpgs.mgne.ui.OptionSelector#getWidth()
	 */
	@Override
	public int getWidth() {
		return insertsWidth;
	}

	/**
	 * @see net.wombatrpgs.mgne.ui.OptionSelector#getHeight()
	 */
	@Override
	public int getHeight() {
		return insertsHeight;
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.ScreenGraphic#setX(float)
	 */
	@Override
	public void setX(float x) {
		for (CharacterInsert insert : inserts) {
			insert.setX(insert.getX() + x - this.x);
		}
		super.setX(x);
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.ScreenGraphic#setY(float)
	 */
	@Override
	public void setY(float y) {
		for (CharacterInsert insert : inserts) {
			insert.setY(insert.getY() + y - this.y);
		}
		super.setY(y);
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.ScreenGraphic#update(float)
	 */
	@Override
	public void update(float elapsed) {
		super.update(elapsed);
		for (CharacterInsert insert : inserts) {
			insert.update(elapsed);
		}
	}
	
	/**
	 * @see net.wombatrpgs.mgne.screen.ScreenObject#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager, int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		super.postProcessing(manager, pass);
		if (pass == 0) {
			createDisplay();
			bg.resizeTo(insertsWidth, insertsHeight);
		}
		for (CharacterInsert insert : inserts) {
			insert.postProcessing(manager, pass);
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.io.CommandListener#onCommand
	 * (net.wombatrpgs.mgneschema.io.data.InputCommand)
	 */
	@Override
	public boolean onCommand(InputCommand command) {
		switch (command) {
		case MOVE_UP:		moveCursorVert(-1);		return true;
		case MOVE_DOWN:		moveCursorVert(1);		return true;
		case MOVE_LEFT:		moveCursorHoriz(-1);	return true;
		case MOVE_RIGHT:	moveCursorHoriz(1);		return true;
		case UI_CONFIRM:	confirm();				return true;
		case UI_CANCEL:		cancel();				return true;
		default:									return true;
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.ui.OptionSelector#coreRender
	 * (com.badlogic.gdx.graphics.g2d.SpriteBatch)
	 */
	@Override
	public void coreRender(SpriteBatch batch) {
		bg.renderAt(batch, x, y);
		for (CharacterInsert insert : inserts) {
			insert.render(batch);
		}
		if (cursorOn) {
			MGlobal.ui.getCursor().renderAt(batch, cursorX, cursorY);
		}
	}
	
	/**
	 * Waits for the user to select a character then returns the listener.
	 * @param	listener		The callback for selection
	 * @param	canCancel		True if the user can cancel to select nobody
	 */
	public void awaitSelection(SelectionListener listener, boolean canCancel) {
		this.listener = listener;
		this.cancellable = canCancel;
		focus();
		cursorOn = true;
		selectedX = 0;
		selectedY = 0;
		updateCursor();
	}
	
	/**
	 * Stops this menu from receiving input. It still displays on the screen.
	 */
	protected void unfocus() {
		MGlobal.screens.peek().removeCommandListener(this);
		MGlobal.screens.peek().popCommandContext();
	}
	
	/**
	 * Resumes the menu for input reception. Should already be on screen.
	 */
	protected void focus() {
		MGlobal.screens.peek().pushCommandContext(new CMapMenu());
		MGlobal.screens.peek().pushCommandListener(this);
	}
	
	/**
	 * Sets up the inserts. Called from the constructor?
	 */
	protected void createDisplay() {
		if (inserts == null) {
			inserts = new ArrayList<CharacterInsert>();
		} else {
			for (CharacterInsert insert : inserts) {
				assets.remove(insert);
			}
			inserts.clear();
		}
		
		float insertX = x + INSERTS_MARGIN;
		float insertY = y + insertsHeight - INSERTS_MARGIN - CharacterInsert.HEIGHT;
		boolean left = true;
		for (Chara hero : SGlobal.heroes.getAll()) {
			CharacterInsert insert = new CharacterInsert(hero);
			insert.setX(insertX);
			insert.setY(insertY);
			if (left) {
				insertX += insert.getWidth();
			} else {
				insertX -= insert.getWidth();
				insertY -= insert.getHeight();
			}
			left = !left;
			inserts.add(insert);
			assets.add(insert);
		}
	}
	
	/**
	 * Updates the cursor position based on the selection variable.
	 */
	protected void updateCursor() {
		Graphic cursor = MGlobal.ui.getCursor();
		cursorX = x;
		cursorY = y + insertsHeight;
		cursorX += CharacterInsert.WIDTH * selectedX;
		cursorY -= CharacterInsert.HEIGHT * selectedY;
		cursorY -= cursor.getHeight() * 3 / 2;
		cursorY += (CharacterInsert.HEIGHT - cursor.getHeight()) / 2;
		cursorX -= (cursor.getWidth() / 2 - 3);
	}
	
	/**
	 * Moves the cursor vertically in the direction of the delta.
	 * @param	delta			The distance to move by
	 */
	protected void moveCursorVert(int delta) {
		selectedY += delta;
		if (selectedY < 0) {
			selectedY = (int) Math.floor(inserts.size() / INSERTS_COUNT_HORIZ);
		}
		if (selectedY >= INSERTS_COUNT_VERT) {
			selectedY = 0;
		}
		while (selectedX + selectedY * INSERTS_COUNT_HORIZ >= inserts.size()) {
			selectedY -= 1;
		}
		updateCursor();
	}
	
	/**
	 * Moves the cursor horizontally in the direction of the delta.
	 * @param	delta			The distance to move by
	 */
	protected void moveCursorHoriz(int delta) {
		selectedX += delta;
		if (selectedX < 0) {
			selectedX = INSERTS_COUNT_HORIZ - 1;
		}
		if (selectedX >= INSERTS_COUNT_HORIZ) {
			selectedX = 0;
		}
		while (selectedX + selectedY * INSERTS_COUNT_HORIZ >= inserts.size()) {
			selectedX -= 1;
		}
		updateCursor();
	}
	
	/**
	 * Called when user cancels the selection process.
	 */
	protected void cancel() {
		if (cancellable) {
			handleSelectResponse(listener.onSelection(null));
		}
	}
	
	/**
	 * Called when the user confirms a character selection.
	 */
	protected void confirm() {
		int selected = selectedX + selectedY * INSERTS_COUNT_HORIZ;
		Chara character = inserts.get(selected).getChara();
		handleSelectResponse(listener.onSelection(character));
	}
	
	/**
	 * Handles the response of the listener callback.
	 * @param	close			True to close the selector
	 */
	protected void handleSelectResponse(boolean close) {
		unfocus();
		cursorOn = false;
		if (close) {
			// TODO: ui: handleSelectResponse close?
		}
	}
	
	/**
	 * When the user selects a character, this gets called.
	 */
	public static abstract class SelectionListener {
		
		/**
		 * Called when the user selects a character.
		 * @param	selected		The character selected, or null for cancel
		 * @return					True if the selector should be closed
		 */
		public abstract boolean onSelection(Chara selected);
		
	}

}
