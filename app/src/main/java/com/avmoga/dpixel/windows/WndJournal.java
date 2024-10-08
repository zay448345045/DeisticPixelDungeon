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

import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Journal;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.ShatteredPixelDungeon;
import com.avmoga.dpixel.items.keys.GoldenKey;
import com.avmoga.dpixel.items.keys.IronKey;
import com.avmoga.dpixel.items.keys.SkeletonKey;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.scenes.PixelScene;
import com.avmoga.dpixel.ui.Icons;
import com.avmoga.dpixel.ui.RedButton;
import com.avmoga.dpixel.ui.RenderedTextMultiline;
import com.avmoga.dpixel.ui.ScrollPane;
import com.avmoga.dpixel.ui.Window;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.ui.Component;

import java.util.Collections;

public class WndJournal extends Window {

	private static final int WIDTH = 112;
	private static final int HEIGHT_P = 160;
	private static final int HEIGHT_L = 144;

	private static final int ITEM_HEIGHT = 18;

	private static final String TXT_TITLE = Messages.get(WndJournal.class, "journal");

	private RenderedText txtTitle;
	private ScrollPane list;

	public WndJournal() {

		super();
		resize(WIDTH, ShatteredPixelDungeon.landscape() ? HEIGHT_L : HEIGHT_P);

		txtTitle = PixelScene.renderText(TXT_TITLE, 9);
		txtTitle.hardlight(Window.TITLE_COLOR);
		txtTitle.x = PixelScene.align(PixelScene.uiCamera,
				(WIDTH - txtTitle.width()) / 2);
		add(txtTitle);

		Component content = new Component();

		Collections.sort(Journal.records);

		float pos = 0;
		for (Journal.Record rec : Journal.records) {
			ListItem item = new ListItem(rec.feature, rec.depth);
			item.setRect(0, pos, WIDTH, ITEM_HEIGHT);
			content.add(item);

			pos += item.height();
		}

		content.setSize(WIDTH, pos);

		list = new ScrollPane(content);
		add(list);

		list.setRect(0, txtTitle.height(), WIDTH, height - txtTitle.height());
	}

	private static class ListItem extends Component {

		private RenderedText feature;
		private BitmapText depth;

		private Image icon;

		public ListItem(Journal.Feature f, int d) {
			super();

			feature.text(f.desc);

			depth.text(Integer.toString(d));
			depth.measure();

			if (d == Dungeon.depth) {
				feature.hardlight(TITLE_COLOR);
				depth.hardlight(TITLE_COLOR);
			}
		}

		@Override
		protected void createChildren() {
			feature = PixelScene.renderText(9);
			add(feature);

			depth = new BitmapText(PixelScene.font1x);
			add(depth);

			icon = Icons.get(Icons.DEPTH);
			add(icon);
		}

		@Override
		protected void layout() {

			icon.x = width - icon.width;

			depth.x = icon.x - 1 - depth.width();
			depth.y = PixelScene.align(y + (height - depth.height()) / 2);

			icon.y = depth.y - 1;

			feature.y = PixelScene.align(depth.y + depth.baseLine()
					- feature.baseLine());
		}
	}
}
