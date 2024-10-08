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
package com.avmoga.dpixel.items.weapon;

import com.avmoga.dpixel.Badges;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.actors.hero.HeroClass;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.KindOfWeapon;
import com.avmoga.dpixel.items.rings.RingOfFuror;
import com.avmoga.dpixel.items.rings.RingOfSharpshooting;
import com.avmoga.dpixel.items.weapon.enchantments.BuzzSaw;
import com.avmoga.dpixel.items.weapon.enchantments.Death;
import com.avmoga.dpixel.items.weapon.enchantments.Fire;
import com.avmoga.dpixel.items.weapon.enchantments.Horror;
import com.avmoga.dpixel.items.weapon.enchantments.Instability;
import com.avmoga.dpixel.items.weapon.enchantments.Leech;
import com.avmoga.dpixel.items.weapon.enchantments.Luck;
import com.avmoga.dpixel.items.weapon.enchantments.Nomnom;
import com.avmoga.dpixel.items.weapon.enchantments.Paralysis;
import com.avmoga.dpixel.items.weapon.enchantments.Poison;
import com.avmoga.dpixel.items.weapon.enchantments.Shock;
import com.avmoga.dpixel.items.weapon.enchantments.Slow;
import com.avmoga.dpixel.items.weapon.melee.MeleeWeapon;
import com.avmoga.dpixel.items.weapon.missiles.MissileWeapon;
import com.avmoga.dpixel.sprites.ItemSprite;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.utils.Utils;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Weapon extends KindOfWeapon {

	private static final int HITS_TO_KNOW = 20;

	private static final String TXT_IDENTIFY = Messages.get(Weapon.class, "identify");
	//private static final String TXT_INCOMPATIBLE = "Interaction of different types of magic has negated the enchantment on this weapon!";
	private static final String TXT_TO_STRING = "%s :%d";

	public int STR = 10;
	public float ACU = 1; // Accuracy modifier
	public float DLY = 1f; // Speed modifier

	public enum Imbue {
		NONE, LIGHT, HEAVY
	}

	public Imbue imbue = Imbue.NONE;

	private int hitsToKnow = HITS_TO_KNOW;

	public Enchantment enchantment;

	@Override
	public void proc(Char attacker, Char defender, int damage) {

		if (enchantment != null) {
			enchantment.proc(this, attacker, defender, damage);
		}

		if (!levelKnown) {
			if (--hitsToKnow <= 0) {
				levelKnown = true;
				GLog.i(TXT_IDENTIFY, name(), toString());
				Badges.validateItemLevelAquired(this);
			}
		}
	}

	private static final String UNFAMILIRIARITY = "unfamiliarity";
	private static final String ENCHANTMENT = "enchantment";
	private static final String IMBUE = "imbue";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(UNFAMILIRIARITY, hitsToKnow);
		bundle.put(ENCHANTMENT, enchantment);
		bundle.put(IMBUE, imbue);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if ((hitsToKnow = bundle.getInt(UNFAMILIRIARITY)) == 0) {
			hitsToKnow = HITS_TO_KNOW;
		}
		enchantment = (Enchantment) bundle.get(ENCHANTMENT);
		imbue = bundle.getEnum(IMBUE, Imbue.class);
	}

	@Override
	public float acuracyFactor(Hero hero) {

		int encumbrance = STR - hero.STR();

		float ACU = this.ACU;

		if (this instanceof MissileWeapon) {
			switch (hero.heroClass) {
			case WARRIOR:
				encumbrance += 3;
				break;
			case HUNTRESS:
				encumbrance -= 2;
				break;
			default:
			}
			int bonus = 0;
			for (Buff buff : hero.buffs(RingOfSharpshooting.Aim.class)) {
				bonus += ((RingOfSharpshooting.Aim) buff).level;
			}
			ACU *= (float) (Math.pow(1.1, bonus));
		}

		return encumbrance > 0 ? (float) (ACU / Math.pow(1.5, encumbrance))
				: ACU;
	}

	@Override
	public float speedFactor(Hero hero) {

		int encumrance = STR - hero.STR();
		if (this instanceof MissileWeapon
				&& hero.heroClass == HeroClass.HUNTRESS) {
			encumrance -= 2;
		}

		float DLY = this.DLY
				* (imbue == Imbue.LIGHT ? 0.667f
						: (imbue == Imbue.HEAVY ? 1.667f : 1.0f));

		int bonus = 0;
		for (Buff buff : hero.buffs(RingOfFuror.Furor.class)) {
			bonus += ((RingOfFuror.Furor) buff).level;
		}

		DLY = (float) (0.25 + (DLY - 0.25) * Math.pow(0.8, bonus));

		return (encumrance > 0 ? (float) (DLY * Math.pow(1.2, encumrance))
				: DLY);
	}

	@Override
	public int damageRoll(Hero hero) {

		int damage = super.damageRoll(hero);

		if (this instanceof MeleeWeapon) {
			int exStr = hero.STR() - STR;
			if (exStr > 0) {
				damage += Random.IntRange(0, exStr);
			}
		}
		if (this instanceof MissileWeapon && hero.heroClass == HeroClass.HUNTRESS) {
			int exStr = Math.round((hero.STR() - STR)/5);
			int lvlBonus = Math.round(hero.lvl/5);
			int totBonus = exStr+lvlBonus+1;
			if (totBonus > 0) {
				damage += damage*Random.IntRange(lvlBonus, totBonus);
			}
		}
		if (this instanceof MissileWeapon && hero.heroClass != HeroClass.HUNTRESS) {
			int exStr = Math.round((hero.STR() - STR)/5);
			int lvlBonus = Math.round(hero.lvl/10);
			int totBonus = exStr+lvlBonus;
			if (totBonus > 0) {
				damage += damage*Random.IntRange(lvlBonus, totBonus);
			}
		}

		return Math.round(damage* (imbue == Imbue.LIGHT ? 0.7f : (imbue == Imbue.HEAVY ? 1.5f: 1f)));
	}

	public Item upgrade(boolean enchant) {
		
		if (enchant){
		   if (enchantment != null) {
				enchantAdv();
		   } else {
				enchant();
		   }
		}

		return super.upgrade();
	}

	@Override
	public String toString() {
		return levelKnown ? Utils.format(TXT_TO_STRING, super.toString(), STR)
				: super.toString();
	}

	@Override
	public String name() {
		return enchantment == null ? super.name() : enchantment.name(super.name());
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
		return this;
	}

	public Weapon enchant(Enchantment ench) {
		enchantment = ench;
		return this;
	}

	
	public Weapon enchant() {

		Class<? extends Enchantment> oldEnchantment = enchantment != null ? enchantment.getClass() : null;
		Enchantment ench = Enchantment.random();
		while (ench.getClass() == oldEnchantment) {
			ench = Enchantment.random();
		}
		
		return enchant(ench);
	}
	
	public Weapon enchantAdv() {

		Class<? extends Enchantment> oldEnchantment = enchantment != null ? enchantment.getClass() : null;
		Enchantment ench = Enchantment.randomAdv();
		while (ench.getClass() == oldEnchantment) {
			ench = Enchantment.randomAdv();
		}
		
		return enchant(ench);
	}

	public Weapon enchantNom() {

		Enchantment ench = Enchantment.randomNom();
		return enchant(ench);
	}
	
	public Weapon enchantBuzz() {

		Enchantment ench = Enchantment.randomBuzz();
		return enchant(ench);
	}
	
	public boolean isEnchanted() {
		return enchantment != null;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return enchantment != null ? enchantment.glowing() : null;
	}

	public static abstract class Enchantment implements Bundlable {

		private static final Class<?>[] enchants = new Class<?>[] { Fire.class,
				Poison.class, Death.class, Paralysis.class, Leech.class,
				Slow.class, Shock.class, Instability.class, Horror.class,
				Luck.class, Nomnom.class, BuzzSaw.class };
		private static final float[] chances = new float[] { 10, 10, 1, 2, 1,
				2, 6, 3, 2, 2, 0, 0 };
		
		private static final float[] chancesAdv = new float[] { 2, 2, 2, 2, 2,
			2, 2, 2, 2, 2, 0, 0 };
		
		private static final float[] chancesNom = new float[] { 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 1, 0 };
		
		private static final float[] chancesBuzz = new float[] { 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 1 };
		

		public abstract boolean proc(Weapon weapon, Char attacker,
				Char defender, int damage);

		public String name(String weaponName) {
			return weaponName;
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

		@SuppressWarnings("unchecked")
		public static Enchantment random() {
			try {
				return ((Class<Enchantment>) enchants[Random.chances(chances)])
						.newInstance();
			} catch (Exception e) {
				return null;
			}
		}
		@SuppressWarnings("unchecked")
		public static Enchantment randomAdv() {
			try {
				return ((Class<Enchantment>) enchants[Random.chances(chancesAdv)])
						.newInstance();
			} catch (Exception e) {
				return null;
			}
		}
		@SuppressWarnings("unchecked")
		public static Enchantment randomNom() {
			try {
				return ((Class<Enchantment>) enchants[Random.chances(chancesNom)])
						.newInstance();
			} catch (Exception e) {
				return null;
			}
		}
		@SuppressWarnings("unchecked")
		public static Enchantment randomBuzz() {
			try {
				return ((Class<Enchantment>) enchants[Random.chances(chancesBuzz)])
						.newInstance();
			} catch (Exception e) {
				return null;
			}
		}

	}
}
