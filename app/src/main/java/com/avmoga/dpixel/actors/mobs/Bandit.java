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
package com.avmoga.dpixel.actors.mobs;

import com.avmoga.dpixel.Badges;
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.buffs.Blindness;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Cripple;
import com.avmoga.dpixel.actors.buffs.Poison;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.sprites.BanditSprite;
import com.watabou.utils.Random;

public class Bandit extends Thief {

	public Item item;

	{
		name = Messages.get(this, "name");
		spriteClass = BanditSprite.class;

		// 1 in 30 chance to be a crazy bandit, equates to overall 1/90 chance.
		lootChance = 0.333f;
	}

	@Override
	protected boolean steal(Hero hero) {
		if (super.steal(hero)) {

			Buff.prolong(hero, Blindness.class, Random.Int(5, 12));
			Buff.affect(hero, Poison.class).set(
					Random.Int(5, 7) * Poison.durationFactor(enemy));
			Buff.prolong(hero, Cripple.class, Cripple.DURATION);
			Dungeon.observe();

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		Badges.validateRare(this);
	}
}
