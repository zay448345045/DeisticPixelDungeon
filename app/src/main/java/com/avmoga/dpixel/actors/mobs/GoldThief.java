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
import com.avmoga.dpixel.Statistics;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Terror;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.items.AncientCoin;
import com.avmoga.dpixel.items.CityKey;
import com.avmoga.dpixel.items.Gold;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.weapon.missiles.Shuriken;
import com.avmoga.dpixel.sprites.CharSprite;
import com.avmoga.dpixel.sprites.GoldThiefSprite;
import com.avmoga.dpixel.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class GoldThief extends Mob {

	protected static final String TXT_STOLE = Messages.get(GoldThief.class, "steal");
	private static final String TXT_KILLCOUNT = Messages.get(GoldThief.class, "count");

	public Item item;

	{
		name = Messages.get(this, "name");
		spriteClass = GoldThiefSprite.class;

		HP = HT = 30+Statistics.goldThievesKilled;
		defenseSkill = 26;

		EXP = 1;
		
		loot = new Shuriken(3);
		lootChance = 1f;
		
		//lootOther = new Cloudberry();
		//lootChanceOther = 0.1f; // by default, see die()

		FLEEING = new Fleeing();
	}

	private int goldtodrop = 0;
	
	private static final String GOLDTODROP = "goldtodrop";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(GOLDTODROP, goldtodrop);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		goldtodrop = bundle.getInt(GOLDTODROP);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(30, 50);
	}

	@Override
	protected float attackDelay() {
		return 0.5f;
	}

	@Override
	public void die(Object cause) {
		
		if (!Dungeon.limitedDrops.citykey.dropped() && Dungeon.depth<27) {
			Dungeon.limitedDrops.citykey.drop();
			Dungeon.level.drop(new CityKey(), pos).sprite.drop();
			explodeDew(pos);				
		} else {
			explodeDew(pos);
		}
		
		if(!Dungeon.limitedDrops.ancientcoin.dropped() && Statistics.goldThievesKilled > 50 && Random.Int(10)==0) {
			Dungeon.limitedDrops.ancientcoin.drop();
			Dungeon.level.drop(new AncientCoin(), pos).sprite.drop();
		}

		Statistics.goldThievesKilled++;
		GLog.w(TXT_KILLCOUNT, Statistics.goldThievesKilled);
		super.die(cause);

		if (item != null) {
			Dungeon.level.drop(item, pos).sprite.drop();
		}
	}

	@Override
	protected Item createLoot() {
		return new Gold(Random.NormalIntRange(goldtodrop+50, goldtodrop+100));
	}

	@Override
	public int attackSkill(Char target) {
		return 32;
	}

	@Override
	public int dr() {
		return 13+Math.round((Statistics.goldThievesKilled+1/10)+1);
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

							
			Gold gold = new Gold(Random.Int(100, 500));
			if (gold.quantity() > 0) {
				goldtodrop = Math.min((gold.quantity()+100),Dungeon.gold);
				Dungeon.gold -= goldtodrop;
				GLog.w(TXT_STOLE, this.name, gold.quantity());
				return true;
			} else {
			   return false;
		}
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
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
