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
package com.avmoga.dpixel.items.armor.glyphs;

import com.avmoga.dpixel.Badges;
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.ResultDescriptions;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.items.armor.Armor;
import com.avmoga.dpixel.items.armor.Armor.Glyph;
import com.avmoga.dpixel.sprites.CharSprite;
import com.avmoga.dpixel.sprites.ItemSprite;
import com.avmoga.dpixel.sprites.ItemSprite.Glowing;
import com.avmoga.dpixel.ui.BuffIndicator;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.utils.Utils;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Viscosity extends Glyph {

	private static final String TXT_VISCOSITY = Messages.get(Viscosity.class, "name");

	private static ItemSprite.Glowing PURPLE = new ItemSprite.Glowing(0x8844CC);

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {

		if (damage == 0) {
			return 0;
		}

		int level = Math.max(0, armor.level);

		if (Random.Int(level + 7) >= 6) {

			DeferedDamage debuff = defender.buff(DeferedDamage.class);
			if (debuff == null) {
				debuff = new DeferedDamage();
				debuff.attachTo(defender);
			}
			debuff.prolong(damage);
			defender.sprite.showStatus(CharSprite.WARNING, Messages.get(this, "deferred"),
					damage);

			return 0;

		} else {
			return damage;
		}
	}

	@Override
	public String name(String weaponName) {
		return String.format(TXT_VISCOSITY, weaponName);
	}

	@Override
	public Glowing glowing() {
		return PURPLE;
	}

	public static class DeferedDamage extends Buff {

		protected int damage = 0;

		private static final String DAMAGE = "damage";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(DAMAGE, damage);

		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			damage = bundle.getInt(DAMAGE);
		}

		@Override
		public boolean attachTo(Char target) {
			if (super.attachTo(target)) {
				postpone(TICK);
				return true;
			} else {
				return false;
			}
		}

		public void prolong(int damage) {
			this.damage += damage;
		};

		@Override
		public int icon() {
			return BuffIndicator.DEFERRED;
		}

		@Override
		public String toString() {
			return Utils.format(Messages.get(Viscosity.class, "bname"), damage);
		}

		@Override
		public String desc() {
			return Messages.get(Viscosity.class, "bdesc", damage);
		}

		@Override
		public boolean act() {
			if (target.isAlive()) {

				target.damage(1, this);
				if (target == Dungeon.hero && !target.isAlive()) {

					Glyph glyph = new Viscosity();
					Dungeon.fail(Utils.format(ResultDescriptions.GLYPH,
							glyph.name()));
					GLog.n(Messages.get(Viscosity.class, "bkill"), glyph.name());

					Badges.validateDeathFromGlyph();
				}
				spend(TICK);

				if (--damage <= 0) {
					detach();
				}

			} else {

				detach();

			}

			return true;
		}
	}
}
