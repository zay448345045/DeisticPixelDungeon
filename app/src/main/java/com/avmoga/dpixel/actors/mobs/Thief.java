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

import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Terror;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.items.Generator;
import com.avmoga.dpixel.items.Gold;
import com.avmoga.dpixel.items.Honeypot;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.artifacts.MasterThievesArmband;
import com.avmoga.dpixel.sprites.CharSprite;
import com.avmoga.dpixel.sprites.ThiefSprite;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.utils.Utils;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Thief extends Mob {

	protected static final String TXT_STOLE = Messages.get(Thief.class, "stole");
	protected static final String TXT_CARRIES = Messages.get(Thief.class, "carries");
	protected static final String TXT_RATCHECK1 = "Spork is avail";
	protected static final String TXT_RATCHECK2 = "Spork is not avail";

	public Item item;

	{
		name = Messages.get(this, "name");
		spriteClass = ThiefSprite.class;

		HP = HT = 20+(adj(0)*Random.NormalIntRange(3, 5));
		defenseSkill = 8+adj(0);

		EXP = 5;

		loot = new MasterThievesArmband().identify();
		lootChance = 0.01f;

		lootOther = Generator.Category.BERRY;
		lootChanceOther = 0.1f; // by default, see die()

		FLEEING = new Fleeing();
	}

	private static final String ITEM = "item";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(ITEM, item);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		item = (Item) bundle.get(ITEM);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(1, 7+adj(0));
	}

	@Override
	protected float attackDelay() {
		return 0.5f;
	}

	@Override
	public void die(Object cause) {

		super.die(cause);

		if (item != null) {
			Dungeon.level.drop(item, pos).sprite.drop();
		}
	}

	@Override
	protected Item createLoot() {
		if (!Dungeon.limitedDrops.armband.dropped()) {
			Dungeon.limitedDrops.armband.drop();
			return super.createLoot();
		} else
			return new Gold(Random.NormalIntRange(100, 250));
	}

	@Override
	public int attackSkill(Char target) {
		return 12;
	}

	@Override
	public int dr() {
		return 3;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (item == null && enemy instanceof Hero && steal((Hero) enemy)) {
			state = FLEEING;
		}

		return damage;
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (state == FLEEING) {
			Dungeon.level.drop(new Gold(), pos).sprite.drop();
		}

		return damage;
	}

	protected boolean steal(Hero hero) {

		Item item = hero.belongings.randomUnequipped();
		if (item != null) {

			item.updateQuickslot();

			GLog.w(TXT_STOLE, this.name, item.name());

			if (item instanceof Honeypot) {
				this.item = ((Honeypot) item).shatter(this, this.pos);
				item.detach(hero.belongings.backpack);
			} else {
				this.item = item;
				if (item instanceof Honeypot.ShatteredPot)
					((Honeypot.ShatteredPot) item).setHolder(this);
				item.detachAll(hero.belongings.backpack);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public String description() {
		String desc = Messages.get(this, "desc");

		if (item != null) {
			desc += String.format(TXT_CARRIES, Utils.capitalize(this.name),
					item.name());
		}

		return desc;
	}

	private class Fleeing extends Mob.Fleeing {
		@Override
		protected void nowhereToRun() {
			if (buff(Terror.class) == null) {
				sprite.showStatus(CharSprite.NEGATIVE, TXT_RAGE);
				state = HUNTING;
			} else {
				super.nowhereToRun();
			}
		}
	}
}
