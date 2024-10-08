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
package com.avmoga.dpixel.items;

import com.avmoga.dpixel.Assets;
import com.avmoga.dpixel.Badges;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.buffs.Blindness;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Fury;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.actors.hero.HeroSubClass;
import com.avmoga.dpixel.effects.Speck;
import com.avmoga.dpixel.effects.SpellSprite;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.utils.Utils;
import com.avmoga.dpixel.windows.WndChooseWay;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class TomeOfMastery extends Item {

	private static final String TXT_BLINDED = Messages.get(TomeOfMastery.class, "blinded");

	public static final float TIME_TO_READ = 10;

	public static final String AC_READ = Messages.get(TomeOfMastery.class, "ac_read");

	{
		stackable = false;
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.MASTERY;

		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_READ);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_READ)) {

			if (hero.buff(Blindness.class) != null) {
				GLog.w(TXT_BLINDED);
				return;
			}

			curUser = hero;

			HeroSubClass way1 = null;
			HeroSubClass way2 = null;
			switch (hero.heroClass) {
			case WARRIOR:
				way1 = HeroSubClass.GLADIATOR;
				way2 = HeroSubClass.BERSERKER;
				break;
			case MAGE:
				way1 = HeroSubClass.BATTLEMAGE;
				way2 = HeroSubClass.WARLOCK;
				break;
			case ROGUE:
				way1 = HeroSubClass.FREERUNNER;
				way2 = HeroSubClass.ASSASSIN;
				break;
			case HUNTRESS:
				way1 = HeroSubClass.SNIPER;
				way2 = HeroSubClass.WARDEN;
				break;
			}
			GameScene.show(new WndChooseWay(this, way1, way2));

		} else {

			super.execute(hero, action);

		}
	}

	@Override
	public boolean doPickUp(Hero hero) {
		Badges.validateMastery();
		return super.doPickUp(hero);
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public String info() {
		return Messages.get(this, "desc");
	}

	public void choose(HeroSubClass way) {

		detach(curUser.belongings.backpack);

		curUser.spend(TomeOfMastery.TIME_TO_READ);
		curUser.busy();

		curUser.subClass = way;

		curUser.sprite.operate(curUser.pos);
		Sample.INSTANCE.play(Assets.SND_MASTERY);

		SpellSprite.show(curUser, SpellSprite.MASTERY);
		curUser.sprite.emitter().burst(Speck.factory(Speck.MASTERY), 12);
		GLog.w("你选择了专精上了 %s 的道路!",
				Utils.capitalize(way.title()));

		if (way == HeroSubClass.BERSERKER
				&& curUser.HP <= curUser.HT * Fury.LEVEL) {
			Buff.affect(curUser, Fury.class);
		}
	}
}
