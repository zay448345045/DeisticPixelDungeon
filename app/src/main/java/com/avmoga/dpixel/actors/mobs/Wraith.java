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
import com.avmoga.dpixel.actors.Actor;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.blobs.ToxicGas;
import com.avmoga.dpixel.actors.buffs.Amok;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Burning;
import com.avmoga.dpixel.actors.buffs.Charm;
import com.avmoga.dpixel.actors.buffs.Frost;
import com.avmoga.dpixel.actors.buffs.Paralysis;
import com.avmoga.dpixel.actors.buffs.Roots;
import com.avmoga.dpixel.actors.buffs.Silence;
import com.avmoga.dpixel.actors.buffs.Sleep;
import com.avmoga.dpixel.actors.buffs.Terror;
import com.avmoga.dpixel.actors.buffs.Vertigo;
import com.avmoga.dpixel.effects.particles.ShadowParticle;
import com.avmoga.dpixel.items.scrolls.ScrollOfMagicalInfusion;
import com.avmoga.dpixel.items.scrolls.ScrollOfPsionicBlast;
import com.avmoga.dpixel.items.scrolls.ScrollOfUpgrade;
import com.avmoga.dpixel.items.weapon.enchantments.Death;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.sprites.WraithSprite;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Wraith extends Mob {

	protected static final float SPAWN_DELAY = 2f;

	protected int level;

	{
		name = Messages.get(this, "name");
		spriteClass = WraithSprite.class;

		HP = HT = 1+level;
		EXP = 1+level;

		flying = true;
		
		loot = new ScrollOfMagicalInfusion();
		lootChance = 0.075f;
		
		lootOther = new ScrollOfUpgrade();
		lootChanceOther = 0.075f;
	}

	protected static final String LEVEL = "level";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEVEL, level);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		level = bundle.getInt(LEVEL);
		adjustStats(level);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(1, 3 + level);
	}

	@Override
	public int attackSkill(Char target) {
		return 10 + level;
	}

	public void adjustStats(int level) {
		this.level = level;
		defenseSkill = attackSkill(null) * 5;
		enemySeen = true;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(10) == 0) {
			Buff.affect(enemy, Vertigo.class, Vertigo.duration(enemy));
			Buff.affect(enemy, Terror.class, Terror.DURATION).object = enemy.id();
		}

		return damage;
	}
	
	//public void damage(int dmg, Object src) {
	//	if (enemySeen
	//			&& (src instanceof Wand || src instanceof LightningTrap.Electricity || src instanceof Char)) {
	//		GLog.n("The attack passes through the wraith.");
	//		sprite.showStatus(CharSprite.NEUTRAL, "missed");
	//	} else {
	//		super.damage(dmg, src);
	//	}
	//}


	@Override
	public String defenseVerb() {
		return Messages.get(this, "def");
	}

	@Override
	public boolean reset() {
		state = WANDERING;
		return true;
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	@Override
	public void notice() {
		super.notice();
		if (Dungeon.getMonth()==9) {yell("BOO!");}
	}
	@Override
	public void add (Buff buff){
		if (buff instanceof Silence){
			this.die(buff);
		} else {
			super.add(buff);
		}
	}

	public static void spawnAround(int pos) {
		for (int n : Level.NEIGHBOURS4) {
			int cell = pos + n;
			if (Level.passable[cell] && Actor.findChar(cell) == null) {
				spawnAt(cell);
			}
		}
	}

	public static Wraith spawnAt(int pos) {
		if (Level.passable[pos] && Actor.findChar(pos) == null) {
          
			Wraith w = new Wraith();
			w.adjustStats(Dungeon.depth);
			w.pos = pos;
			w.state = w.HUNTING;
			GameScene.add(w, SPAWN_DELAY);

			w.sprite.alpha(0);
			w.sprite.parent.add(new AlphaTweener(w.sprite, 1, 0.5f));

			w.sprite.emitter().burst(ShadowParticle.CURSE, 5);

			return w;
  			
		} else {
			return null;
		}
	}

	protected static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add(Death.class);
		IMMUNITIES.add(Terror.class);
		IMMUNITIES.add(Amok.class);
		IMMUNITIES.add(Charm.class);
		IMMUNITIES.add(Sleep.class);
		IMMUNITIES.add(ToxicGas.class);
		IMMUNITIES.add(ScrollOfPsionicBlast.class);
		IMMUNITIES.add(Vertigo.class);
		IMMUNITIES.add(Burning.class);
		IMMUNITIES.add(Paralysis.class);
		IMMUNITIES.add(Roots.class);
		IMMUNITIES.add(Frost.class);
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
