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

import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.hero.HeroSubClass;
import com.avmoga.dpixel.actors.hero.HeroSubRace;
import com.avmoga.dpixel.items.TomeOfMastery;
import com.avmoga.dpixel.items.TomeOfSpecialty;
import com.avmoga.dpixel.scenes.PixelScene;
import com.avmoga.dpixel.sprites.ItemSprite;
import com.avmoga.dpixel.ui.RedButton;
import com.avmoga.dpixel.ui.RenderedTextMultiline;
import com.avmoga.dpixel.ui.Window;
import com.avmoga.dpixel.utils.Utils;

public class WndChooseWay extends Window {

	private static final String TXT_MESSAGE = Messages.get(WndChooseWay.class, "msg");
	private static final String TXT_CANCEL = Messages.get(WndChooseWay.class, "cancel");

	private static final int WIDTH = 120;
	private static final int BTN_HEIGHT = 18;
	private static final float GAP = 2;

	public WndChooseWay(final TomeOfMastery tome, final HeroSubClass way1,
						final HeroSubClass way2) {

		super();

		IconTitle titlebar = new IconTitle();
		titlebar.icon(new ItemSprite(tome.image(), null));
		titlebar.label(tome.name());
		titlebar.setRect(0, 0, WIDTH, 0);
		add(titlebar);

		RenderedTextMultiline hl = PixelScene.renderMultiline(6);
		hl.text(way1.desc() + "\n\n" + way2.desc() + "\n\n" + Messages.get(this, "message"), WIDTH);
		hl.setPos(titlebar.left(), titlebar.bottom() + GAP);
		add(hl);

		RedButton btnWay1 = new RedButton(Utils.capitalize(way1.title())) {
			@Override
			protected void onClick() {
				hide();
				tome.choose(way1);
			}
		};
		btnWay1.setRect(0, hl.bottom() + GAP, (WIDTH - GAP) / 2, BTN_HEIGHT);
		add(btnWay1);

		RedButton btnWay2 = new RedButton(Utils.capitalize(way2.title())) {
			@Override
			protected void onClick() {
				hide();
				tome.choose(way2);
			}
		};
		btnWay2.setRect(btnWay1.right() + GAP, btnWay1.top(), btnWay1.width(),
				BTN_HEIGHT);
		add(btnWay2);

		RedButton btnCancel = new RedButton(TXT_CANCEL) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		btnCancel.setRect(0, btnWay2.bottom() + GAP, WIDTH, BTN_HEIGHT);
		add(btnCancel);

		resize(WIDTH, (int) btnCancel.bottom());
	}

	public WndChooseWay(final TomeOfSpecialty tome, final HeroSubRace way1,
						final HeroSubRace way2) {

		super();

		IconTitle titlebar = new IconTitle();
		titlebar.icon(new ItemSprite(tome.image(), null));
		titlebar.label(tome.name());
		titlebar.setRect(0, 0, WIDTH, 0);
		add(titlebar);

		RenderedTextMultiline hl = PixelScene.renderMultiline(6);
		hl.text(way1.desc() + "\n\n" + way2.desc() + "\n\n" + Messages.get(this, "message"), WIDTH);
		hl.setPos(titlebar.left(), titlebar.bottom() + GAP);
		add(hl);

		RedButton btnWay1 = new RedButton(Utils.capitalize(way1.title())) {
			@Override
			protected void onClick() {
				hide();
				tome.choose(way1);
			}
		};
		btnWay1.setRect(0, hl.bottom() + GAP, (WIDTH - GAP) / 2, BTN_HEIGHT);
		add(btnWay1);

		RedButton btnWay2 = new RedButton(Utils.capitalize(way2.title())) {
			@Override
			protected void onClick() {
				hide();
				tome.choose(way2);
			}
		};
		btnWay2.setRect(btnWay1.right() + GAP, btnWay1.top(), btnWay1.width(),
				BTN_HEIGHT);
		add(btnWay2);

		RedButton btnCancel = new RedButton(TXT_CANCEL) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		btnCancel.setRect(0, btnWay2.bottom() + GAP, WIDTH, BTN_HEIGHT);
		add(btnCancel);

		resize(WIDTH, (int) btnCancel.bottom());
	}
}
