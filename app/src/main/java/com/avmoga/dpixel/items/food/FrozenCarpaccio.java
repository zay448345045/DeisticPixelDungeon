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

import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.buffs.Barkskin;
import com.avmoga.dpixel.actors.buffs.Bleeding;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Cripple;
import com.avmoga.dpixel.actors.buffs.Drowsy;
import com.avmoga.dpixel.actors.buffs.Hunger;
import com.avmoga.dpixel.actors.buffs.Invisibility;
import com.avmoga.dpixel.actors.buffs.Poison;
import com.avmoga.dpixel.actors.buffs.Slow;
import com.avmoga.dpixel.actors.buffs.Vertigo;
import com.avmoga.dpixel.actors.buffs.Weakness;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.effects.Speck;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;
import com.avmoga.dpixel.utils.GLog;
import com.watabou.utils.Random;

public class FrozenCarpaccio extends Food {

	{
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.CARPACCIO;
		energy = Hunger.STARVING - Hunger.HUNGRY;
		hornValue = 1;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_EAT)) {

			switch (Random.Int(5)) {
				case 0:
					if (Dungeon.depth != 29) {
						GLog.i(Messages.get(this, "invis"));
						Buff.affect(hero, Invisibility.class, Invisibility.DURATION);
					}
					break;
				case 1:
					GLog.i(Messages.get(this, "hard"));
					Buff.affect(hero, Barkskin.class).level(hero.HT / 4);
					break;
				case 2:
					GLog.i(Messages.get(this, "refresh"));
					Buff.detach(hero, Poison.class);
					Buff.detach(hero, Cripple.class);
					Buff.detach(hero, Weakness.class);
					Buff.detach(hero, Bleeding.class);
					Buff.detach(hero, Drowsy.class);
					Buff.detach(hero, Slow.class);
					Buff.detach(hero, Vertigo.class);
					break;
				case 3:
					GLog.i(Messages.get(this, "better"));
					if (hero.HP < hero.HT) {
						hero.HP = Math.min(hero.HP + hero.HT / 4, hero.HT);
						hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
					}
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
		return 10 * quantity;
	};

	public static Food cook(MysteryMeat ingredient) {
		FrozenCarpaccio result = new FrozenCarpaccio();
		result.quantity = ingredient.quantity();
		return result;
	}
	public static Food cook(Meat ingredient) {
		FrozenCarpaccio result = new FrozenCarpaccio();
		result.quantity = ingredient.quantity();
		return result;
	}
}
