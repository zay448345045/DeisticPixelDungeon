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
package com.avmoga.dpixel.items.rings;

import java.util.HashSet;

import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.blobs.ToxicGas;
import com.avmoga.dpixel.actors.buffs.Burning;
import com.avmoga.dpixel.actors.buffs.Poison;
import com.avmoga.dpixel.actors.mobs.Eye;
import com.avmoga.dpixel.actors.mobs.Warlock;
import com.avmoga.dpixel.actors.mobs.Yog;
import com.avmoga.dpixel.levels.traps.LightningTrap;
import com.watabou.utils.Random;

public class RingOfElements extends Ring {

	{
		name = Messages.get(this, "name");
	}

	@Override
	protected RingBuff buff() {
		return new Resistance();
	}

	@Override
	public String desc() {
		return isKnown() ? Messages.get(this, "desc") : super.desc();
	}

	private static final HashSet<Class<?>> EMPTY = new HashSet<Class<?>>();
	private static final HashSet<Class<?>> FULL;
	static {
		FULL = new HashSet<Class<?>>();
		FULL.add(Burning.class);
		FULL.add(ToxicGas.class);
		FULL.add(Poison.class);
		FULL.add(LightningTrap.Electricity.class);
		FULL.add(Warlock.class);
		FULL.add(Eye.class);
		FULL.add(Yog.BurningFist.class);
	}

	public class Resistance extends RingBuff {

		public HashSet<Class<?>> resistances() {
			if (Random.Int(level + 2) >= 2) {
				return FULL;
			} else {
				return EMPTY;
			}
		}

		public float durationFactor() {
			return level < 0 ? 1 : (1 + 0.5f * level) / (1 + level);
		}
	}
}
