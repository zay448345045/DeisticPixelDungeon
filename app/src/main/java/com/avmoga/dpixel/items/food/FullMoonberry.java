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
package com.avmoga.dpixel.items.food;

import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.buffs.Barkskin;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.FullMoonStrength;
import com.avmoga.dpixel.actors.buffs.Hunger;
import com.avmoga.dpixel.actors.buffs.Light;
import com.avmoga.dpixel.actors.buffs.Strength;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;
import com.avmoga.dpixel.utils.GLog;
import com.watabou.utils.Random;

public class FullMoonberry extends Food {

	{
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.SEED_FULLMOONBERRY;
		energy = (Hunger.STARVING - Hunger.HUNGRY)/10;
		message = Messages.get(Blackberry.class, "eat");
		hornValue = 1;
		bones = false;
	}
		
	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_EAT)) {

			switch (Random.Int(2)) {
				case 0:
					GLog.p(Messages.get(this, "effect"));
					Buff.affect(hero, Strength.class);
					Buff.affect(hero, FullMoonStrength.class);
					Buff.affect(hero, Light.class, Light.DURATION);
					break;
				case 1:
					GLog.p(Messages.get(this, "effect"));
					Buff.affect(hero, Strength.class);
					Buff.affect(hero, FullMoonStrength.class);
					Buff.affect(hero, Barkskin.class).level(hero.HT * 2);
					Buff.affect(hero, Light.class, Light.DURATION);
					break;
			}
		}
	}

	@Override
	public String info() {
		return Messages.get(this, "desc");
	}

	@Override
	public int price() {
		return 20 * quantity;
	}
	
	public FullMoonberry() {
		this(1);
	}

	public FullMoonberry(int value) {
		this.quantity = value;
	}
}
