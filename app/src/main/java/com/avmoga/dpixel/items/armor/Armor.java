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
import com.avmoga.dpixel.ResultDescriptions;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.items.EquipableItem;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.armor.glyphs.Affection;
import com.avmoga.dpixel.items.armor.glyphs.AntiEntropy;
import com.avmoga.dpixel.items.armor.glyphs.Bounce;
import com.avmoga.dpixel.items.armor.glyphs.Displacement;
import com.avmoga.dpixel.items.armor.glyphs.Entanglement;
import com.avmoga.dpixel.items.armor.glyphs.Metabolism;
import com.avmoga.dpixel.items.armor.glyphs.Multiplicity;
import com.avmoga.dpixel.items.armor.glyphs.Potential;
import com.avmoga.dpixel.items.armor.glyphs.Stench;
import com.avmoga.dpixel.items.armor.glyphs.Viscosity;
import com.avmoga.dpixel.sprites.HeroSprite;
import com.avmoga.dpixel.sprites.ItemSprite;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.utils.Utils;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Armor extends EquipableItem {

	private static final int HITS_TO_KNOW = 10;

	private static final float TIME_TO_EQUIP = 1f;

	private static final String TXT_EQUIP_CURSED = Messages.get(Armor.class, "equip_cursed");

	private static final String TXT_IDENTIFY = Messages.get(Armor.class, "identify");

	private static final String TXT_TO_STRING = "%s :%d";

	private static final String TXT_INCOMPATIBLE = Messages.get(Armor.class, "incompatible");

	public int tier;

	public int STR;
	public int DR;

	private int hitsToKnow = HITS_TO_KNOW;

	public Glyph glyph;

	public Armor(int tier) {

		this.tier = tier;

		STR = typicalSTR();
		DR = typicalDR();
	}

	private static final String UNFAMILIRIARITY = "unfamiliarity";
	private static final String GLYPH = "glyph";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(UNFAMILIRIARITY, hitsToKnow);
		bundle.put(GLYPH, glyph);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if ((hitsToKnow = bundle.getInt(UNFAMILIRIARITY)) == 0) {
			hitsToKnow = HITS_TO_KNOW;
		}
		inscribe((Glyph) bundle.get(GLYPH));
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(isEquipped(hero) ? AC_UNEQUIP : AC_EQUIP);
		return actions;
	}

	@Override
	public boolean doEquip(Hero hero) {

		detach(hero.belongings.backpack);

		if (hero.belongings.armor == null
				|| hero.belongings.armor.doUnequip(hero, true, false)) {

			hero.belongings.armor = this;

			cursedKnown = true;
			if (cursed) {
				equipCursed(hero);
				GLog.n(TXT_EQUIP_CURSED, toString());
			}

			((HeroSprite) hero.sprite).updateArmor();

			hero.spendAndNext(TIME_TO_EQUIP);
			return true;

		} else {

			collect(hero.belongings.backpack);
			return false;

		}
	}

	@Override
	protected float time2equip(Hero hero) {
		return 2;
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)) {

			hero.belongings.armor = null;
			((HeroSprite) hero.sprite).updateArmor();

			return true;

		} else {

			return false;

		}
	}

	@Override
	public boolean isEquipped(Hero hero) {
		return hero.belongings.armor == this;
	}

	@Override
	public Item upgrade() {
		return upgrade(false);
	}

	public Item upgrade(boolean inscribe) {

		if (inscribe) {
			inscribe(Glyph.random());
		}

		DR += tier;
		STR--;

		return super.upgrade();
	}

	@Override
	public Item degrade() {
		DR -= tier;
		STR++;

		return super.degrade();
	}

	public int proc(Char attacker, Char defender, int damage) {

		if (glyph != null) {
			damage = glyph.proc(this, attacker, defender, damage);
		}

		if (!levelKnown) {
			if (--hitsToKnow <= 0) {
				levelKnown = true;
				GLog.w(TXT_IDENTIFY, name(), toString());
			}
		}

		return damage;
	}

	@Override
	public String toString() {
		return levelKnown ? Utils.format(TXT_TO_STRING, super.toString(), STR)
				: super.toString();
	}

	@Override
	public String name() {
		return glyph == null ? super.name() : glyph.name(super.name());
	}

	@Override
	public String info() {
		String name = name();
		StringBuilder info = new StringBuilder(desc());

		if (levelKnown) {
			info.append(Messages.get(this, "curr_absorb", Math.max(DR, 0)));

			if (STR > Dungeon.hero.STR()) {

				info.append(Messages.get(this, "too_heavy"));

			}
		} else {
			info.append(Messages.get(this, "avg_absorb", typicalDR()));
			if (typicalSTR() > Dungeon.hero.STR()) {
				info.append(Messages.get(this, "probably_too_heavy"));
			}
		}

		if (glyph != null) {
			info.append(Messages.get(this, "inscribed", glyph.name()));
			info.append(glyph.desc());
		}

		if (reinforced) {
			info.append(Messages.get(this, "reinforced"));
		}

		if (isEquipped(Dungeon.hero)) {
			if (cursed) {
				info.append(Messages.get(this, "wearing", name) + Messages.get(this, "cursed_worn"));
			} else {
				info.append(Messages.get(this, "wearing", name));
			}
		} else {
			if (cursedKnown && cursed) {
				info.append(Messages.get(this, "cursed"));
			}
		}

		return info.toString();
	}

	@Override
	public Item random() {
		if (Random.Float() < 0.4) {
			int n = 1;
			if (Random.Int(3) == 0) {
				n++;
				if (Random.Int(3) == 0) {
					n++;
				}
			}
			if (Random.Int(2) == 0) {
				upgrade(n);
			} else {
				degrade(n);
				cursed = true;
			}
		}

		if (Random.Int(10) == 0) {
			inscribe();
		}

		return this;
	}

	public int typicalSTR() {
		return 7 + tier * 2;
	}

	public int typicalDR() {
		return tier * 2;
	}

	@Override
	public int price() {
		int price = 10 * (1 << (tier - 1));
		if (glyph != null) {
			price *= 1.5;
		}
		if (cursed && cursedKnown) {
			price /= 2;
		}
		if (levelKnown) {
			if (level > 0) {
				price *= (level + 1);
			} else if (level < 0) {
				price /= (1 - level);
			}
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

	public Armor inscribe(Glyph glyph) {

		if (glyph != null && this.glyph == null) {
			DR += tier;
		} else if (glyph == null && this.glyph != null) {
			DR -= tier;
		}

		this.glyph = glyph;

		return this;
	}

	public Armor inscribe() {

		Class<? extends Glyph> oldGlyphClass = glyph != null ? glyph.getClass()
				: null;
		Glyph gl = Glyph.random();
		while (gl.getClass() == oldGlyphClass) {
			gl = Armor.Glyph.random();
		}

		return inscribe(gl);
	}

	public boolean isInscribed() {
		return glyph != null;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return glyph != null ? glyph.glowing() : null;
	}

	public static abstract class Glyph implements Bundlable {

		private static final Class<?>[] glyphs = new Class<?>[]{Bounce.class,
				Affection.class, AntiEntropy.class, Multiplicity.class,
				Potential.class, Metabolism.class, Stench.class,
				Viscosity.class, Displacement.class, Entanglement.class};

		private static final float[] chances = new float[]{1, 1, 1, 1, 1, 1,
				1, 1, 1, 1};

		private static final float[] chancesAdv = new float[]{1, 1, 1, 1, 1, 1,
				1, 1, 1, 1};

		public abstract int proc(Armor armor, Char attacker, Char defender,
								 int damage);

		public String name() {
			return name(Messages.get(Armor.class, "gname"));
		}

		public String name(String armorName) {
			return armorName;
		}

		public String desc() {
			return Messages.get(this, "desc");
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
		}

		@Override
		public void storeInBundle(Bundle bundle) {
		}

		public ItemSprite.Glowing glowing() {
			return ItemSprite.Glowing.WHITE;
		}

		public boolean checkOwner(Char owner) {
			if (!owner.isAlive() && owner instanceof Hero) {

				Dungeon.fail(Utils.format(ResultDescriptions.GLYPH, name()));
				GLog.n(Messages.get(Armor.class, "gkilled"), name());

				return true;

			} else {
				return false;
			}
		}

		@SuppressWarnings("unchecked")
		public static Glyph random() {
			try {
				return ((Class<Glyph>) glyphs[Random.chances(chances)])
						.newInstance();
			} catch (Exception e) {
				return null;
			}
		}

		@SuppressWarnings("unchecked")
		public static Glyph randomAdv() {
			try {
				return ((Class<Glyph>) glyphs[Random.chances(chancesAdv)])
						.newInstance();
			} catch (Exception e) {
				return null;
			}
		}

	}
}
