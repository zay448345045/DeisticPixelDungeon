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
import com.avmoga.dpixel.actors.blobs.ToxicGas;
import com.avmoga.dpixel.actors.buffs.Burning;
import com.avmoga.dpixel.actors.buffs.Frost;
import com.avmoga.dpixel.actors.buffs.Invisibility;
import com.avmoga.dpixel.actors.buffs.Paralysis;
import com.avmoga.dpixel.actors.buffs.Roots;
import com.avmoga.dpixel.items.ConchShell;
import com.avmoga.dpixel.items.Generator;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.food.Meat;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.sprites.AlbinoPiranhaSprite;
import com.avmoga.dpixel.utils.GLog;
import com.watabou.utils.Random;

import java.util.HashSet;

public class AlbinoPiranha extends Mob {

	private static final String TXT_KILLCOUNT = Messages.get(AlbinoPiranha.class, "count");

	{
		name = Messages.get(AlbinoPiranha.class, "name");
		spriteClass = AlbinoPiranhaSprite.class;

		baseSpeed = 2f;

		EXP = 0;
		
		loot = new Meat();
		lootChance = 0.1f;
	}

	public AlbinoPiranha() {
		super();

		HP = HT = 10 + Dungeon.depth * 5;
		defenseSkill = 10 + Dungeon.depth * 2;
	}

	protected boolean checkwater(int cell){
		return Level.water[cell];		
	}
	
		
	@Override
	protected boolean act() {
		
		if (!Level.water[pos]) {
			damage(HT, this);
			//die(null);
			return true;
					
				
		} else {
			// this causes pirahna to move away when a door is closed on them.
			Dungeon.level.updateFieldOfView(this);
			enemy = chooseEnemy();
			if (state == this.HUNTING
					&& !(enemy.isAlive() 
					&& Level.fieldOfView[enemy.pos] 
					&& Level.water[enemy.pos])) {
				state = this.WANDERING;
				int oldPos = pos;
				int i = 0;
				do {
					i++;
					target = Dungeon.level.randomDestination();
					if (i == 100)
						return true;
				} while (!getCloser(target));
				moveSprite(oldPos, pos);
				return true;
			}
			
			if (enemy.invisible>1){
				enemy.remove(Invisibility.class);
				GLog.w("当所有的鱼都失明了的时候，看不见是没有意义的！");
			}
			
			if (!Level.water[enemy.pos] || enemy.flying){
				enemy.invisible = 1;
			}	else {enemy.invisible = 0;}	
						
			return super.act();
		}
	}

	
	
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange(Dungeon.depth, 4 + Dungeon.depth * 2);
	}

	@Override
	public int attackSkill(Char target) {
		return 20 + Dungeon.depth * 2;
	}

	@Override
	public int dr() {
		return Dungeon.depth;
	}

	@Override
	public void die(Object cause) {
		explodeDew(pos);
		if(Random.Int(105-Math.min(Statistics.albinoPiranhasKilled,100))==0){
		  Item mushroom = Generator.random(Generator.Category.MUSHROOM);
		  Dungeon.level.drop(mushroom, pos).sprite.drop();	
		}
		
		if(!Dungeon.limitedDrops.conchshell.dropped() && Statistics.albinoPiranhasKilled > 50 && Random.Int(10)==0) {
			Dungeon.limitedDrops.conchshell.drop();
			Dungeon.level.drop(new ConchShell(), pos).sprite.drop();
		}
		
		super.die(cause);

		Statistics.albinoPiranhasKilled++;
		GLog.w(TXT_KILLCOUNT, Statistics.albinoPiranhasKilled);
		//Badges.validatePiranhasKilled();
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	protected boolean getCloser(int target) {

		if (rooted) {
			return false;
		}

		int step = Dungeon.findPath(this, pos, target, Level.water,
				Level.fieldOfView);
		if (step != -1) {
			move(step);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean getFurther(int target) {
		int step = Dungeon.flee(this, pos, target, Level.water,
				Level.fieldOfView);
		if (step != -1) {
			move(step);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add(Burning.class);
		IMMUNITIES.add(Paralysis.class);
		IMMUNITIES.add(ToxicGas.class);
		IMMUNITIES.add(Roots.class);
		IMMUNITIES.add(Frost.class);
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
