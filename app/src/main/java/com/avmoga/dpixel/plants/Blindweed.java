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
package com.avmoga.dpixel.plants;

import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Blindness;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Cripple;
import com.avmoga.dpixel.actors.mobs.Mob;
import com.avmoga.dpixel.effects.CellEmitter;
import com.avmoga.dpixel.effects.Speck;
import com.avmoga.dpixel.items.potions.PotionOfInvisibility;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Blindweed extends Plant {

	private static final String TXT_DESC = Messages.get(Blindweed.class, "desc");

	{
		image = 3;
		plantName = Messages.get(this, "name");
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);

		if (ch != null) {
			int len = Random.Int(5, 10);
			Buff.prolong(ch, Blindness.class, len);
			Buff.prolong(ch, Cripple.class, len);
			if (ch instanceof Mob) {
				((Mob) ch).state = ((Mob) ch).WANDERING;
				((Mob) ch).beckon(Dungeon.level.randomDestination());
			}
		}

		if (Dungeon.visible[pos]) {
			CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 4);
		}
	}

	@Override
	public String desc() {
		return TXT_DESC;
	}

	public static class Seed extends Plant.Seed {
		{
			plantName = Messages.get(Blindweed.class, "name");

			name = Messages.get(this, "name");
			image = ItemSpriteSheet.SEED_BLINDWEED;

			plantClass = Blindweed.class;
			alchemyClass = PotionOfInvisibility.class;
		}

		@Override
		public String desc() {
			return Messages.get(Plant.class, "seeddesc", plantName);
		}
	}
}
