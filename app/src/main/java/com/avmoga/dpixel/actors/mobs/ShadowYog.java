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
import com.avmoga.dpixel.Statistics;
import com.avmoga.dpixel.actors.Actor;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.blobs.ToxicGas;
import com.avmoga.dpixel.actors.buffs.Amok;
import com.avmoga.dpixel.actors.buffs.Burning;
import com.avmoga.dpixel.actors.buffs.Charm;
import com.avmoga.dpixel.actors.buffs.Sleep;
import com.avmoga.dpixel.actors.buffs.Terror;
import com.avmoga.dpixel.actors.buffs.Vertigo;
import com.avmoga.dpixel.effects.CellEmitter;
import com.avmoga.dpixel.effects.Speck;
import com.avmoga.dpixel.items.Amulet;
import com.avmoga.dpixel.items.scrolls.ScrollOfPsionicBlast;
import com.avmoga.dpixel.items.weapon.enchantments.Death;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.levels.Terrain;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.sprites.ShadowYogSprite;
import com.avmoga.dpixel.utils.GLog;
import com.watabou.utils.Random;

import java.util.HashSet;

public class ShadowYog extends Mob  {
	
	{
		name = Messages.get(this, "name");
		spriteClass = ShadowYogSprite.class;
		//END,PLAYS YOU CAN DIED END BOSS?
		HP = HT = 200* hero.lvl+Dungeon.depth+100;
		
		baseSpeed = 2f;
		defenseSkill = 32+hero.lvl/5;

		EXP = 100;

		state = HUNTING;
	}
	
	private int yogsAlive = 0;

	private static final String TXT_DESC = Messages.get(ShadowYog.class, "desc");

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(60+hero.lvl/5, 140+hero.lvl/2);
	}

	@Override
	public int attackSkill(Char target) {
		return 50+hero.lvl;
	}

	public ShadowYog() {
		super();
	}

	@Override
	public int dr() {
		return (Dungeon.level.mobs.size());
	}
	
	@Override
	public void damage(int dmg, Object src) {

			//for (Mob mob : Dungeon.level.mobs) {
			 //	mob.beckon(pos);
			//	}
			
			for (int i = 0; i < 4; i++) {
				int trapPos;
				do {
					trapPos = Random.Int(Level.getLength());
				} while (!Level.fieldOfView[trapPos] || !Level.passable[trapPos]);

				if (Dungeon.level.map[trapPos] == Terrain.INACTIVE_TRAP) {
					Level.set(trapPos, Terrain.SECRET_SUMMONING_TRAP);
					GameScene.updateMap(trapPos);
				}
			}
			
			if (HP<(HT/8) && Random.Float() < 0.5f){
				int newPos = -1;
					for (int i = 0; i < 20; i++) {
					newPos = Dungeon.level.randomRespawnCellMob();
					if (newPos != -1) {
						break;
					}
				}
				if (newPos != -1) {
					Actor.freeCell(pos);
					CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
					pos = newPos;
					sprite.place(pos);
					sprite.visible = Dungeon.visible[pos];
					GLog.n(Messages.get(this, "vanish"));
				}		
				if (Dungeon.level.mobs.size()< hero.lvl*6){
				SpectralRat.spawnAroundChance(newPos);
				}
			}
			
			super.damage(dmg, src);
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
                return super.defenseProc(enemy, damage);
	}
	

	
	@Override
	public void beckon(int cell) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void die(Object cause) {
		super.die(cause);
		
		Statistics.shadowYogsKilled++;

      for (Mob mob : Dungeon.level.mobs) {
			
			if (mob instanceof ShadowYog){
				   yogsAlive++;
				 }
			}
			
		 if(yogsAlive==0){
			GameScene.bossSlain();
			Dungeon.shadowyogkilled=true;
			
			//Dungeon.level.drop(new OrbOfZot(), pos).sprite.drop();
			 Dungeon.level.drop(new Amulet(), pos).sprite.drop();
			
			for (Mob mob : (Iterable<Mob>) Dungeon.level.mobs.clone()) {
				if (mob instanceof Rat || mob instanceof GreyOni || mob instanceof SpectralRat || mob instanceof Eye|| mob instanceof BrokenRobot|| mob instanceof Shaman|| mob instanceof DemonGoo|| mob instanceof Yog) {
					mob.die(cause);
				}
			}
			
			yell("结束了...");
		 }
	}

	@Override
	public void notice() {
		super.notice();
		yell(Messages.get(this, "notice"));
	}

	@Override
	public String description() {
		return TXT_DESC;

	}
	

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {

		IMMUNITIES.add(Death.class);
		IMMUNITIES.add(Terror.class);
		IMMUNITIES.add(Amok.class);
		IMMUNITIES.add(Charm.class);
		IMMUNITIES.add(Sleep.class);
		IMMUNITIES.add(Burning.class);
		IMMUNITIES.add(ToxicGas.class);
		IMMUNITIES.add(ScrollOfPsionicBlast.class);
		IMMUNITIES.add(Vertigo.class);
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}

	}
