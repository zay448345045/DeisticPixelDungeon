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
package com.avmoga.dpixel.items.armor;

import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.actors.hero.HeroClass;
import com.avmoga.dpixel.actors.mobs.Mob;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.weapon.missiles.Shuriken;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;
import com.avmoga.dpixel.sprites.MissileSprite;
import com.avmoga.dpixel.utils.GLog;
import com.watabou.utils.Callback;

import java.util.HashMap;

public class HuntressArmor extends ClassArmor {

	private static final String TXT_NO_ENEMIES = Messages.get(HuntressArmor.class, "no_enemies");
	private static final String TXT_NOT_HUNTRESS = Messages.get(HuntressArmor.class, "not_huntress");

	private static final String AC_SPECIAL = Messages.get(HuntressArmor.class, "ac_special");

	{
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.ARMOR_HUNTRESS;
	}

	private HashMap<Callback, Mob> targets = new HashMap<Callback, Mob>();

	@Override
	public String special() {
		return AC_SPECIAL;
	}

	@Override
	public void doSpecial() {

		Item proto = new Shuriken();

		for (Mob mob : Dungeon.level.mobs) {
			if (Level.fieldOfView[mob.pos]) {

				Callback callback = new Callback() {
					@Override
					public void call() {
						curUser.attack(targets.get(this));
						targets.remove(this);
						if (targets.isEmpty()) {
							curUser.spendAndNext(curUser.attackDelay());
						}
					}
				};

				((MissileSprite) curUser.sprite.parent
						.recycle(MissileSprite.class)).reset(curUser.pos,
						mob.pos, proto, callback);

				targets.put(callback, mob);
			}
		}

		if (targets.size() == 0) {
			GLog.w(TXT_NO_ENEMIES);
			return;
		}

		curUser.HP -= (curUser.HP / 3);

		curUser.sprite.zap(curUser.pos);
		curUser.busy();
	}

	@Override
	public boolean doEquip(Hero hero) {
		if (hero.heroClass == HeroClass.HUNTRESS) {
			return super.doEquip(hero);
		} else {
			GLog.w(TXT_NOT_HUNTRESS);
			return false;
		}
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}
}