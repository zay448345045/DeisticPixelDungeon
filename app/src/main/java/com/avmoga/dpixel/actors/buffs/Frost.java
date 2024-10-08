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
package com.avmoga.dpixel.actors.buffs;

import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.actors.mobs.Thief;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.food.FrozenCarpaccio;
import com.avmoga.dpixel.items.food.MysteryMeat;
import com.avmoga.dpixel.items.potions.Potion;
import com.avmoga.dpixel.items.rings.RingOfElements.Resistance;
import com.avmoga.dpixel.ui.BuffIndicator;
import com.avmoga.dpixel.utils.GLog;

public class Frost extends FlavourBuff {

	private static final String TXT_FREEZES = "%s freezes!";

	private static final float DURATION = 5f;

	@Override
	public boolean attachTo(Char target) {
		if (super.attachTo(target)) {

			target.paralysed = true;
			Buff.detach(target, Burning.class);

			if (target instanceof Hero) {

				Hero hero = (Hero) target;
				Item item = hero.belongings.randomUnequipped();
				if (item instanceof Potion) {

					item = item.detach(hero.belongings.backpack);
					GLog.w(TXT_FREEZES, item.toString());
					((Potion) item).shatter(hero.pos);

				} else if (item instanceof MysteryMeat) {

					item = item.detach(hero.belongings.backpack);
					FrozenCarpaccio carpaccio = new FrozenCarpaccio();
					if (!carpaccio.collect(hero.belongings.backpack)) {
						Dungeon.level.drop(carpaccio, target.pos).sprite.drop();
					}
					GLog.w(TXT_FREEZES, item.toString());

				}
			} else if (target instanceof Thief
					&& ((Thief) target).item instanceof Potion) {

				((Potion) ((Thief) target).item).shatter(target.pos);
				((Thief) target).item = null;

			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void detach() {
		super.detach();
		Paralysis.unfreeze(target);
	}

	@Override
	public int icon() {
		return BuffIndicator.FROST;
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}

	public static float duration(Char ch) {
		Resistance r = ch.buff(Resistance.class);
		return r != null ? r.durationFactor() * DURATION : DURATION;
	}
}
