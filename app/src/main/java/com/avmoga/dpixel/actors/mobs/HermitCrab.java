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
import com.avmoga.dpixel.ResultDescriptions;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.effects.particles.SparkParticle;
import com.avmoga.dpixel.items.Generator;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.levels.traps.LightningTrap;
import com.avmoga.dpixel.mechanics.Ballistica;
import com.avmoga.dpixel.sprites.CharSprite;
import com.avmoga.dpixel.sprites.HermitCrabSprite;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.utils.Utils;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.HashSet;

public class HermitCrab extends Mob implements Callback {

	private static final float TIME_TO_ZAP = 2f;

	private static final String TXT_LIGHTNING_KILLED = Messages.get(HermitCrab.class, "kill");
	private static final String TXT_SHELL_ABSORB = Messages.get(HermitCrab.class, "absorb");
	private static final String TXT_SHELL_CHARGE = Messages.get(HermitCrab.class, "charge");

	{
		name = Messages.get(this, "name");
		spriteClass = HermitCrabSprite.class;

		HP = HT = 20;
		defenseSkill = 22;

		EXP = 6;

		loot = Generator.Category.BERRY;
		lootChance = 0.33f;
		
	
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(10, 20);
	}

	@Override
	public int attackSkill(Char target) {
		return 25;
	}

	@Override
	public int dr() {
		return 4;
	}

	@Override
	public void damage(int dmg, Object src) {
		
		if (dmg>HT/4 && src != LightningTrap.LIGHTNING){
            for (Mob mob : Dungeon.level.mobs) {
			if (mob instanceof Shell && mob.isAlive()){
				Dungeon.shellCharge+=dmg;
				GLog.n(TXT_SHELL_ABSORB);
				GLog.n(TXT_SHELL_CHARGE, dmg);
				dmg=1;
				
			    }			
			}
		}			
		super.damage(dmg, src);
	}

	
	@Override
	protected boolean canAttack(Char enemy) {
		if (!this.isSilenced()){
			return Ballistica.cast(pos, enemy.pos, false, true) == enemy.pos;
		} else {
			return false;
		}
	}

	@Override
	protected boolean doAttack(Char enemy) {

		if (Level.distance(pos, enemy.pos) <= 1) {

			return super.doAttack(enemy);

		} else {

			boolean visible = Level.fieldOfView[pos]
					|| Level.fieldOfView[enemy.pos];
			if (visible) {
				((HermitCrabSprite) sprite).zap(enemy.pos);
			}

			spend(TIME_TO_ZAP);

			if (hit(this, enemy, true)) {
				int dmg = Random.Int(15, 30);
				if (Level.water[enemy.pos] && !enemy.flying) {
					dmg *= 1.5f;
				}
				enemy.damage(dmg, LightningTrap.LIGHTNING);

				enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
				enemy.sprite.flash();

				if (enemy == Dungeon.hero) {

					Camera.main.shake(2, 0.3f);

					if (!enemy.isAlive()) {
						Dungeon.fail(Utils.format(ResultDescriptions.MOB,
								Utils.indefinite(name)));
						GLog.n(TXT_LIGHTNING_KILLED, name);
					}
				}
			} else {
				enemy.sprite
						.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
			}

			return !visible;
		}
	}

	@Override
	public void call() {
		next();
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add(LightningTrap.Electricity.class);
	}

	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
}
