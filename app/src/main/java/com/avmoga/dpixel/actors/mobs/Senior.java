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
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Paralysis;
import com.avmoga.dpixel.sprites.SeniorSprite;
import com.watabou.utils.Random;

public class Senior extends Monk {

	{
		name = Messages.get(this, "name");
		spriteClass = SeniorSprite.class;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(32, 56+adj(0));
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(10) == 0) {
			Buff.prolong(enemy, Paralysis.class, 1.1f);
		}
		return super.attackProc(enemy, damage);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		Badges.validateRare(this);
	}
}
