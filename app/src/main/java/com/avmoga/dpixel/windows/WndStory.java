/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.avmoga.dpixel.windows;

import com.avmoga.dpixel.Chrome;
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.ShatteredPixelDungeon;
import com.avmoga.dpixel.scenes.PixelScene;
import com.avmoga.dpixel.ui.RenderedTextMultiline;
import com.avmoga.dpixel.ui.Window;
import com.watabou.input.Touchscreen.Touch;
import com.watabou.noosa.Game;
import com.watabou.noosa.TouchArea;
import com.watabou.utils.SparseArray;

public class WndStory extends Window {

	private static final int WIDTH_P = 120;
	private static final int WIDTH_L = 144;
	private static final int MARGIN = 6;

	private static final float bgR = 0.77f;
	private static final float bgG = 0.73f;
	private static final float bgB = 0.62f;

	public static final int ID_SEWERS = 0;
	public static final int ID_PRISON = 1;
	public static final int ID_CAVES = 2;
	public static final int ID_METROPOLIS = 3;
	public static final int ID_HALLS = 4;

	private static final SparseArray<String> CHAPTERS = new SparseArray<String>();

	static {
		CHAPTERS.put(
				ID_SEWERS,
				Messages.get(WndStory.class, "sewers"));

		CHAPTERS.put(
				ID_PRISON,
				Messages.get(WndStory.class, "prison"));

		CHAPTERS.put(
				ID_CAVES,
				Messages.get(WndStory.class, "caves"));

		CHAPTERS.put(
				ID_METROPOLIS,
				Messages.get(WndStory.class, "city"));

		CHAPTERS.put(
				ID_HALLS,
				Messages.get(WndStory.class, "halls"));
	}

	private RenderedTextMultiline tf;

	private float delay;

	public WndStory(String text) {
		super(0, 0, Chrome.get(Chrome.Type.SCROLL));

		tf = PixelScene.renderMultiline(text, 7);
		tf.maxWidth(ShatteredPixelDungeon.landscape() ?
				WIDTH_L - MARGIN * 2 :
				WIDTH_P - MARGIN * 2);
		tf.invert();
		tf.setPos(MARGIN, 0);
		add(tf);

		add(new TouchArea(chrome) {
			@Override
			protected void onClick(Touch touch) {
				hide();
			}
		});

		resize((int) (tf.width() + MARGIN * 2),
				(int) Math.min(tf.height(), 180));
	}

	@Override
	public void update() {
		super.update();

		if (delay > 0 && (delay -= Game.elapsed) <= 0) {
			shadow.visible = chrome.visible = tf.visible = true;
		}
	}

	public static void showChapter(int id) {

		if (Dungeon.chapters.contains(id)) {
			return;
		}

		String text = CHAPTERS.get(id);
		if (text != null) {
			WndStory wnd = new WndStory(text);
			if ((wnd.delay = 0.6f) > 0) {
				wnd.shadow.visible = wnd.chrome.visible = wnd.tf.visible = false;
			}

			Game.scene().add(wnd);

			Dungeon.chapters.add(id);
		}
	}
}
