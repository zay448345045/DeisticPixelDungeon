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

import com.avmoga.dpixel.Assets;
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.ResultDescriptions;
import com.avmoga.dpixel.Statistics;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.items.Bone;
import com.avmoga.dpixel.items.PrisonKey;
import com.avmoga.dpixel.items.RedDewdrop;
import com.avmoga.dpixel.items.YellowDewdrop;
import com.avmoga.dpixel.items.weapon.enchantments.Death;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.sprites.MossySkeletonSprite;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.utils.Utils;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.HashSet;

public class MossySkeleton extends Mob {

	private static final String TXT_HERO_KILLED = Messages.get(MossySkeleton.class, "kill");
	private static final String TXT_KILLCOUNT = Messages.get(MossySkeleton.class, "count");

	{
		name = Messages.get(this, "name");
		spriteClass = MossySkeletonSprite.class;

		HP = HT = 35+(10*Random.NormalIntRange(7, 10));
		defenseSkill = 15;

		EXP = 1;
		maxLvl = 10;
		
		baseSpeed = 0.5f+(Math.min(2.5f, Statistics.skeletonsKilled/50));

		loot = new YellowDewdrop();
		lootChance = 0.5f; // by default, see die()
			
		lootThird= new RedDewdrop();
		lootChanceThird = 0.1f; // by default, see die()
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(20+Math.round(Statistics.skeletonsKilled/10), 45+Math.round(Statistics.skeletonsKilled/5));
		
	}

	@Override
	protected float attackDelay() {
		return 2f-(Statistics.skeletonsKilled/100);
	}
	
	@Override
	public void die(Object cause) {

		super.die(cause);
		
		Statistics.skeletonsKilled++;
		GLog.w(TXT_KILLCOUNT, Statistics.skeletonsKilled);
		
		if (!Dungeon.limitedDrops.prisonkey.dropped() && Dungeon.depth<27) {
			Dungeon.limitedDrops.prisonkey.drop();
			Dungeon.level.drop(new PrisonKey(), pos).sprite.drop();
			explodeDew(pos);				
		} else {
			explodeDew(pos);
		}
		
		if(!Dungeon.limitedDrops.bone.dropped() && Statistics.skeletonsKilled > 50 && Random.Int(10)==0) {
			Dungeon.limitedDrops.bone.drop();
			Dungeon.level.drop(new Bone(), pos).sprite.drop();
		}

		boolean heroKilled = false;
		for (int i = 0; i < Level.NEIGHBOURS8.length; i++) {
			Char ch = findChar(pos + Level.NEIGHBOURS8[i]);
			if (ch != null && ch.isAlive()) {
				int damage = Math.max(0,
						Random.NormalIntRange(3, 8) - Random.IntRange(0, ch.dr() / 2));
				ch.damage(damage, this);
				if (ch == Dungeon.hero && !ch.isAlive()) {
					heroKilled = true;
				}
			}
		}

		if (Dungeon.visible[pos]) {
			Sample.INSTANCE.play(Assets.SND_BONES);
		}

		if (heroKilled) {
			Dungeon.fail(Utils.format(ResultDescriptions.MOB,
					Utils.indefinite(name)));
			GLog.n(TXT_HERO_KILLED);
		}
	}

	
	

	@Override
	public int attackSkill(Char target) {
		return 28;
	}

	@Override
	public int dr() {
		return 27;
	}

	@Override
	public String defenseVerb() {
		return Messages.get(this, "def");
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add(Death.class);
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
