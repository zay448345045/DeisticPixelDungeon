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
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.actors.mobs.npcs.Tinkerer2;
import com.avmoga.dpixel.items.ActiveMrDestructo;
import com.avmoga.dpixel.items.ActiveMrDestructo2;
import com.avmoga.dpixel.items.InactiveMrDestructo;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.Mushroom;
import com.avmoga.dpixel.scenes.PixelScene;
import com.avmoga.dpixel.sprites.ItemSprite;
import com.avmoga.dpixel.ui.RedButton;
import com.avmoga.dpixel.ui.RenderedTextMultiline;
import com.avmoga.dpixel.ui.Window;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.utils.Utils;

public class WndTinkerer2 extends Window {

	private static final String TXT_MESSAGE1 = Messages.get(WndTinkerer2.class, "msg1");

	private static final String TXT_MESSAGE2 = Messages.get(WndTinkerer2.class, "msg2");

	private static final String TXT_MESSAGE3 = Messages.get(WndTinkerer2.class, "msg3");

	private static final String TXT_UPGRADE = Messages.get(WndTinkerer2.class, "upgrade");
	private static final String TXT_RECHARGE = Messages.get(WndTinkerer2.class, "recharge");
	private static final String TXT_NEW = Messages.get(WndTinkerer2.class, "new");

	private static final String TXT_FARAWELL = Messages.get(WndTinkerer2.class, "farewell");

	private static final int WIDTH = 120;
	private static final int BTN_HEIGHT = 20;
	private static final float GAP = 2;


	public WndTinkerer2(final Tinkerer2 tinkerer, final Item item, final Item mrd) {

		super();

		IconTitle titlebar = new IconTitle();
		titlebar.icon(new ItemSprite(item.image(), null));
		titlebar.label(Utils.capitalize(item.name()));
		titlebar.setRect(0, 0, WIDTH, 0);
		add(titlebar);

		if (mrd instanceof InactiveMrDestructo) {


			RenderedTextMultiline message = PixelScene.renderMultiline(TXT_MESSAGE1, 6);
			message.maxWidth(WIDTH);
			message.setPos(0, titlebar.bottom() + GAP);
			add(message);

			RedButton btnUpgrade = new RedButton(TXT_UPGRADE) {
				@Override
				protected void onClick() {
					selectUpgrade(tinkerer);
				}
			};
			btnUpgrade.setRect(0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT);
			add(btnUpgrade);

			RedButton btnRecharge = new RedButton(TXT_RECHARGE) {
				@Override
				protected void onClick() {
					selectRecharge(tinkerer);
				}
			};
			btnRecharge.setRect(0, btnUpgrade.bottom() + GAP, WIDTH, BTN_HEIGHT);
			add(btnRecharge);

			resize(WIDTH, (int) btnRecharge.bottom());


		} else if (mrd instanceof ActiveMrDestructo) {
			RenderedTextMultiline message = PixelScene.renderMultiline(TXT_MESSAGE2, 6);
			message.maxWidth(WIDTH);
			message.setPos(0, titlebar.bottom() + GAP);
			add(message);

			RedButton btnUpgrade = new RedButton(TXT_UPGRADE) {
				@Override
				protected void onClick() {
					selectUpgradePlus(tinkerer);
				}
			};
			btnUpgrade.setRect(0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT);
			add(btnUpgrade);

			resize(WIDTH, (int) btnUpgrade.bottom());

		} else {
			RenderedTextMultiline message = PixelScene.renderMultiline(TXT_MESSAGE3, 6);
			message.maxWidth(WIDTH);
			message.setPos(0, titlebar.bottom() + GAP);
			add(message);

			RedButton btnNew = new RedButton(TXT_NEW) {
				@Override
				protected void onClick() {
					selectNew(tinkerer);
				}
			};
			btnNew.setRect(0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT);
			add(btnNew);

			resize(WIDTH, (int) btnNew.bottom());
		}

	}


	private void selectUpgrade(Tinkerer2 tinkerer) {

		hide();

		Mushroom mushroom = Dungeon.hero.belongings.getItem(Mushroom.class);
		mushroom.detach(Dungeon.hero.belongings.backpack);

		InactiveMrDestructo inmrd = Dungeon.hero.belongings.getItem(InactiveMrDestructo.class);
		inmrd.detach(Dungeon.hero.belongings.backpack);

		ActiveMrDestructo2 mrd2 = new ActiveMrDestructo2();
		if (mrd2.doPickUp(Dungeon.hero)) {
			GLog.i(Messages.get(Hero.class, "have"), mrd2.name());
		} else {
			Dungeon.level.drop(mrd2, Dungeon.hero.pos).sprite.drop();
		}

		tinkerer.yell(Utils.format(TXT_FARAWELL, Dungeon.hero.givenName()));
		tinkerer.destroy();

		tinkerer.sprite.die();

		//Wandmaker.Quest.complete();
	}

	private void selectUpgradePlus(Tinkerer2 tinkerer) {

		hide();

		Mushroom mushroom = Dungeon.hero.belongings.getItem(Mushroom.class);
		mushroom.detach(Dungeon.hero.belongings.backpack);

		ActiveMrDestructo2 mrd2 = new ActiveMrDestructo2();
		if (mrd2.doPickUp(Dungeon.hero)) {
			GLog.i(Messages.get(Hero.class, "have"), mrd2.name());
		} else {
			Dungeon.level.drop(mrd2, Dungeon.hero.pos).sprite.drop();
		}

		tinkerer.yell(Utils.format(TXT_FARAWELL, Dungeon.hero.givenName()));
		tinkerer.destroy();

		tinkerer.sprite.die();

		//Wandmaker.Quest.complete();
	}

	private void selectRecharge(Tinkerer2 tinkerer) {

		hide();

		Mushroom mushroom = Dungeon.hero.belongings.getItem(Mushroom.class);
		mushroom.detach(Dungeon.hero.belongings.backpack);

		InactiveMrDestructo inmrd = Dungeon.hero.belongings.getItem(InactiveMrDestructo.class);
		inmrd.detach(Dungeon.hero.belongings.backpack);

		ActiveMrDestructo mrd = new ActiveMrDestructo();
		if (mrd.doPickUp(Dungeon.hero)) {
			GLog.i(Messages.get(Hero.class, "have"), mrd.name());
		} else {
			Dungeon.level.drop(mrd, Dungeon.hero.pos).sprite.drop();
		}

		ActiveMrDestructo mrds = new ActiveMrDestructo();
		if (mrds.doPickUp(Dungeon.hero)) {
			GLog.i(Messages.get(Hero.class, "have"), mrds.name());
		} else {
			Dungeon.level.drop(mrds, Dungeon.hero.pos).sprite.drop();
		}

		tinkerer.yell(Utils.format(TXT_FARAWELL, Dungeon.hero.givenName()));
		tinkerer.destroy();

		tinkerer.sprite.die();

		//Wandmaker.Quest.complete();
	}

	private void selectNew(Tinkerer2 tinkerer) {

		hide();

		Mushroom mushroom = Dungeon.hero.belongings.getItem(Mushroom.class);
		mushroom.detach(Dungeon.hero.belongings.backpack);

		ActiveMrDestructo mrd = new ActiveMrDestructo();
		if (mrd.doPickUp(Dungeon.hero)) {
			GLog.i(Messages.get(Hero.class, "have"), mrd.name());
		} else {
			Dungeon.level.drop(mrd, Dungeon.hero.pos).sprite.drop();
		}

		tinkerer.yell(Utils.format(TXT_FARAWELL, Dungeon.hero.givenName()));
		tinkerer.destroy();

		tinkerer.sprite.die();

		//Wandmaker.Quest.complete();
	}

}