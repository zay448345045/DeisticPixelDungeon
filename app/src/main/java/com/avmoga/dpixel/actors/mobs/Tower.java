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
import com.avmoga.dpixel.Badges;
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.ResultDescriptions;
import com.avmoga.dpixel.actors.Actor;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.blobs.ToxicGas;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Terror;
import com.avmoga.dpixel.effects.CellEmitter;
import com.avmoga.dpixel.effects.Speck;
import com.avmoga.dpixel.effects.particles.BlastParticle;
import com.avmoga.dpixel.effects.particles.SmokeParticle;
import com.avmoga.dpixel.effects.particles.SparkParticle;
import com.avmoga.dpixel.items.Gold;
import com.avmoga.dpixel.items.RedDewdrop;
import com.avmoga.dpixel.items.keys.SkeletonKey;
import com.avmoga.dpixel.items.scrolls.ScrollOfPsionicBlast;
import com.avmoga.dpixel.items.weapon.enchantments.Death;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.levels.Terrain;
import com.avmoga.dpixel.levels.traps.LightningTrap;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.sprites.TowerSprite;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.utils.Utils;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Tower extends Mob implements Callback {

	{
		name = Messages.get(this, "name");
		spriteClass = TowerSprite.class;

		HP = HT = 500+(Dungeon.depth*Random.NormalIntRange(10, 12));
		defenseSkill = 0;

		EXP = 25;
		
		hostile = false;
		state = PASSIVE;
		
		loot = new RedDewdrop();
		lootChance = 1f;
		
	}
	
	@Override
	public void beckon(int cell) {
		// Do nothing
	}
	
	private int bossAlive = 0;

	@Override
	public int damageRoll() {
		return 0;
	}

	@Override
	public void damage(int dmg, Object src) {

		for (Mob mob : Dungeon.level.mobs) {
			mob.beckon(Dungeon.hero.pos);
		}

		GLog.w("警报：系统正在受损！！！");
		CellEmitter.center(pos).start(
				Speck.factory(Speck.SCREAM), 0.3f, 3);
		Sample.INSTANCE.play(Assets.SND_ALERT);

		super.damage(dmg, src);
	}
	
	@Override
	public int attackSkill(Char target) {
		return 0;
	}

	@Override
	public int dr() {
		return 10;
	}
	
	@Override
	protected boolean act() {
		
		switch (Random.Int(4)) {
		case 1:
		for (Mob mob : Dungeon.level.mobs) {
			if (mob instanceof Tower && mob != this) {
				mob.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
				mob.sprite.flash();
			}
		}
		break;
		case 2:
			if (Dungeon.level.mobs.size()<10){
		 BrokenRobot.spawnAround(pos);
				yell("警报：发现未知人员入侵！！！");
			}
		break;
		}
		
		return super.act();
	}
	
	@Override
	public void call() {
		next();
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}
	
	public void explode(int cell) {
		// We're blowing up, so no need for a fuse anymore.
	
		Sample.INSTANCE.play(Assets.SND_BLAST, 2);

		if (Dungeon.visible[cell]) {
			CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30);
		}

		boolean terrainAffected = false;
		for (int n : Level.NEIGHBOURS8) {
			int c = cell + n;
			if (c >= 0 && c < Level.getLength()) {
				if (Dungeon.visible[c]) {
					CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
				}

				if (Level.flamable[c]) {
					Level.set(c, Terrain.EMBERS);
					GameScene.updateMap(c);
					terrainAffected = true;
				}

				Char ch = Actor.findChar(c);
				if (ch != null) {
					// those not at the center of the blast take damage less
					// consistently.
					int minDamage = c == cell ? Dungeon.depth + 5 : 1;
					int maxDamage = 10 + Dungeon.depth * 2;

					int dmg = Random.NormalIntRange(minDamage, maxDamage)
							- Random.Int(ch.dr());
					if (dmg > 0) {
						ch.damage(dmg, this);
					}

					if (ch == Dungeon.hero && !ch.isAlive())
						// constant is used here in the rare instance a player
						// is killed by a double bomb.
						Dungeon.fail(Utils.format(ResultDescriptions.ITEM,
								"bomb"));
				}
						
			}
		}

		if (terrainAffected) {
			Dungeon.observe();
		}
	}
	
	
	@Override
	public void add(Buff buff) {
	}
	
	@Override
	public void die(Object cause) {

		super.die(cause);
		
		explode(pos);

		for (Mob mob : Dungeon.level.mobs) {
			
			if (mob instanceof Tower || mob instanceof DM300){
				   bossAlive++;
				 }
			
			}
			
			 if(bossAlive==0){
				 
					GameScene.bossSlain();
					Dungeon.level.drop(new SkeletonKey(Dungeon.depth), pos).sprite.drop();
					Dungeon.level.drop(new Gold(Random.Int(3000, 6000)), pos).sprite.drop();

					Badges.validateBossSlain();
			 }
			 explodeDew(pos);
	}

	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add(Death.class);
		RESISTANCES.add(ScrollOfPsionicBlast.class);
		RESISTANCES.add(LightningTrap.Electricity.class);
	}

	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add(ToxicGas.class);
		IMMUNITIES.add(Terror.class);
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}

	
}
