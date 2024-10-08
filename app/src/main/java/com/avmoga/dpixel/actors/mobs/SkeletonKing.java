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

import static com.avmoga.dpixel.Dungeon.hero;

import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Burning;
import com.avmoga.dpixel.actors.buffs.Terror;
import com.avmoga.dpixel.actors.buffs.Weakness;
import com.avmoga.dpixel.items.AdamantWeapon;
import com.avmoga.dpixel.items.Gold;
import com.avmoga.dpixel.items.potions.PotionOfLiquidFlame;
import com.avmoga.dpixel.items.wands.WandOfFirebolt;
import com.avmoga.dpixel.items.weapon.enchantments.Fire;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.sprites.SkeletonKingSprite;
import com.watabou.utils.Random;

import java.util.HashSet;

public class SkeletonKing extends Mob {

	{
		name = "骷髅王";
		spriteClass = SkeletonKingSprite.class;

		HP = HT = 800 + hero.HT;
		defenseSkill = 30;

		EXP = 10;
		maxLvl = 20;

		flying = true;

		loot = new PotionOfLiquidFlame();
		lootChance = 0.1f;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(20, 40);
	}

	@Override
	public int attackSkill(Char target) {
		return 25;
	}

	@Override
	public int dr() {
		return 25;
	}

	@Override
	protected boolean act() {
		boolean result = super.act();

		if (state == FLEEING && buff(Terror.class) == null && enemy != null
				&& enemySeen && enemy.buff(Weakness.class) == null) {
			state = HUNTING;
		}
		return result;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(2) == 0) {
			if(enemy == hero){
			 Buff.prolong(enemy, Weakness.class, Weakness.duration(enemy));
			state = FLEEING;
			}
		}

		return damage;
	}
	
	
	@Override
	public void die(Object cause) {

		GameScene.bossSlain();
		Dungeon.level.drop(new Gold(Random.Int(1900, 4000)), pos).sprite.drop();
		Dungeon.level.drop(new AdamantWeapon(), pos).sprite.drop();
		Dungeon.skeletonkingkilled=true;
			
		super.die(cause);

		yell(Messages.get(this, "die"));
					
	}
	

	@Override
	public void notice() {
		super.notice();
		yell(Messages.get(this, "notice", Dungeon.hero.givenName()));
	}


	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add(Burning.class);
		IMMUNITIES.add(Fire.class);
		IMMUNITIES.add(WandOfFirebolt.class);
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
