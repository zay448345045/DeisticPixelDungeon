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
package com.avmoga.dpixel.scenes;

import com.avmoga.dpixel.Assets;
import com.avmoga.dpixel.Badges;
import com.avmoga.dpixel.Chrome;
import com.avmoga.dpixel.ShatteredPixelDungeon;
import com.avmoga.dpixel.ui.Archs;
import com.avmoga.dpixel.ui.BadgesList;
import com.avmoga.dpixel.ui.ExitButton;
import com.avmoga.dpixel.ui.ScrollPane;
import com.avmoga.dpixel.ui.Window;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Callback;

public class BadgesScene extends PixelScene {

	private static final String TXT_TITLE = "你的徽章";

	private static final int MAX_PANE_WIDTH = 160;

	@Override
	public void create() {

		super.create();

		Music.INSTANCE.play(Assets.THEME, true);
		Music.INSTANCE.volume(1f);

		uiCamera.visible = false;

		int w = Camera.main.width;
		int h = Camera.main.height;

		Archs archs = new Archs();
		archs.setSize(w, h);
		add(archs);

		int pw = Math.min(MAX_PANE_WIDTH, w - 6);
		int ph = h - 30;

		NinePatch panel = Chrome.get(Chrome.Type.WINDOW);
		panel.size(pw, ph);
		panel.x = (w - pw) / 2;
		panel.y = (h - ph) / 2;
		add(panel);

		RenderedText title = PixelScene.renderText(TXT_TITLE, 9);
		title.hardlight(Window.HEADER_COLOR);
		title.x = align((w - title.width()) / 2);
		title.y = align((panel.y - title.baseLine()) / 2);
		add(title);

		Badges.loadGlobal();

		ScrollPane list = new BadgesList(true);
		add(list);

		list.setRect(panel.x + panel.marginLeft(), panel.y + panel.marginTop(),
				panel.innerWidth(), panel.innerHeight());

		ExitButton btnExit = new ExitButton();
		btnExit.setPos(Camera.main.width - btnExit.width(), 0);
		add(btnExit);

		fadeIn();

		Badges.loadingListener = new Callback() {
			@Override
			public void call() {
				if (Game.scene() == BadgesScene.this) {
					ShatteredPixelDungeon.switchNoFade(BadgesScene.class);
				}
			}
		};
	}

	@Override
	public void destroy() {

		Badges.saveGlobal();
		Badges.loadingListener = null;

		super.destroy();
	}

	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchNoFade(TitleScene.class);
	}
}
