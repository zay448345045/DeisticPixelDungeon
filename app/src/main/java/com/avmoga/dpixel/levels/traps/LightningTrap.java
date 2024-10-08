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
package com.avmoga.dpixel.levels.traps;

import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.ResultDescriptions;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.effects.CellEmitter;
import com.avmoga.dpixel.effects.Lightning;
import com.avmoga.dpixel.effects.particles.SparkParticle;
import com.avmoga.dpixel.items.Heap;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.utils.Utils;
import com.watabou.noosa.Camera;
import com.watabou.utils.Random;

public class LightningTrap {

	private static final String name = Messages.get(LightningTrap.class, "name");

	// 00x66CCEE

	public static void trigger(int pos, Char ch) {

		if (ch != null) {
			ch.damage(Math.max(1, Random.Int(ch.HP / 3, 2 * ch.HP / 3)),
					LIGHTNING);
			if (ch == Dungeon.hero) {

				Camera.main.shake(2, 0.3f);

				if (!ch.isAlive()) {
					Dungeon.fail(Utils.format(ResultDescriptions.TRAP, name));
					GLog.n(Messages.get(LightningTrap.class, "ondeath"));
				} else {
					((Hero) ch).belongings.charge(false);
				}
			}

			int[] points = new int[2];

			points[0] = pos - Level.getWidth();
			points[1] = pos + Level.getWidth();
			ch.sprite.parent.add(new Lightning(points, 2, null));

			points[0] = pos - 1;
			points[1] = pos + 1;
			ch.sprite.parent.add(new Lightning(points, 2, null));
		}

		CellEmitter.center(pos).burst(SparkParticle.FACTORY,
				Random.IntRange(3, 4));
		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap != null) {heap.lit();}

	}

	public static final Electricity LIGHTNING = new Electricity();

	public static class Electricity {
	}
}
