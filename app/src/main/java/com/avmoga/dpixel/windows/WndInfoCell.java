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
import com.avmoga.dpixel.DungeonTilemap;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.blobs.Blob;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.levels.Terrain;
import com.avmoga.dpixel.scenes.PixelScene;
import com.avmoga.dpixel.ui.RenderedTextMultiline;
import com.avmoga.dpixel.ui.Window;
import com.watabou.noosa.BitmapTextMultiline;
import com.watabou.noosa.Image;

public class WndInfoCell extends Window {

	private static final float GAP = 2;

	private static final int WIDTH = 120;

	private static final String TXT_NOTHING = Messages.get(WndInfoCell.class, "nothing");

	public WndInfoCell(int cell) {

		super();

		int tile = Dungeon.level.map[cell];
		if (Level.water[cell]) {
			tile = Terrain.WATER;
		} else if (Level.pit[cell]) {
			tile = Terrain.CHASM;
		}

		IconTitle titlebar = new IconTitle();
		if (tile == Terrain.WATER) {
			Image water = new Image(Dungeon.level.waterTex());
			water.frame(0, 0, DungeonTilemap.SIZE, DungeonTilemap.SIZE);
			titlebar.icon(water);
		} else {
			titlebar.icon(DungeonTilemap.tile(tile));
		}
		titlebar.label(Dungeon.level.tileName(tile));
		titlebar.setRect(0, 0, WIDTH, 0);
		add(titlebar);

		RenderedTextMultiline info = PixelScene.renderMultiline(6);
		add(info);

		StringBuilder desc = new StringBuilder(Dungeon.level.tileDesc(tile));

		for (Blob blob : Dungeon.level.blobs.values()) {
			if (blob.cur[cell] > 0 && blob.tileDesc() != null) {
				if (desc.length() > 0) {
					desc.append("\n\n");
				}
				desc.append(blob.tileDesc());
			}
		}

		info.text(desc.length() > 0 ? desc.toString() : TXT_NOTHING);
		info.maxWidth(WIDTH);
		info.setPos(titlebar.left(), titlebar.bottom() + GAP);

		resize(WIDTH, (int) (info.top() + info.height()));
	}
}
