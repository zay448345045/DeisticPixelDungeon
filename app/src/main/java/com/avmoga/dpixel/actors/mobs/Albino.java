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
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Bleeding;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.items.food.Meat;
import com.avmoga.dpixel.sprites.AlbinoSprite;
import com.watabou.utils.Random;

public class Albino extends Rat {

	{
		name = Messages.get(this, "name");
		spriteClass = AlbinoSprite.class;

		HP = HT = 10+(Dungeon.depth*Random.NormalIntRange(1, 3));

		loot = new Meat();
		lootChance = 1f;
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		Badges.validateRare(this);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(2) == 0) {
			Buff.affect(enemy, Bleeding.class).set(damage);
		}

		return damage;
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}
}
