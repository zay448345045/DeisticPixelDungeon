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
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.actors.hero.HeroClass;
import com.avmoga.dpixel.effects.Speck;
import com.avmoga.dpixel.sprites.CharSprite;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;
import com.avmoga.dpixel.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class RedDewdrop extends Dewdrop {

	private static final String TXT_VALUE = "%+dHP";
	public int amountToFill = 5;
	{
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.REDDEWDROP;

		stackable = true;
	}

	@Override
	public boolean doPickUp(Hero hero) {

		DewVial vial = hero.belongings.getItem(DewVial.class);

		if (vial == null || vial.isFull()) {
			if (!(hero.HP >= hero.HT)) {
				int value = 1 + (Dungeon.depth - 1) / 5;
				if (hero.heroClass == HeroClass.HUNTRESS) {
					value++;
				}

				int effect = Math.min(hero.HT - hero.HP, value * quantity);
				if (effect > 0) {
					hero.HP += effect;
					hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
					hero.sprite.showStatus(CharSprite.POSITIVE, TXT_VALUE, effect);
				}
			} else {
				if (vial == null) {
					GLog.w("你的生命值已满！");
				} else {
					GLog.w("你的生命值和露珠瓶已满，你不能拾取露珠了！");
				}
				return false;
			}
		} else {
			vial.collectDew(this);
		}

		Sample.INSTANCE.play(Assets.SND_DEWDROP);
		hero.spendAndNext(TIME_TO_PICK_UP);

		return true;
	}

	@Override
	public String info() {
		return Messages.get(this, "desc");
	}
}
