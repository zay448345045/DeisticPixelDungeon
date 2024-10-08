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
import com.avmoga.dpixel.actors.Actor;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.blobs.ToxicGas;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Poison;
import com.avmoga.dpixel.effects.CellEmitter;
import com.avmoga.dpixel.effects.Speck;
import com.avmoga.dpixel.effects.particles.ElmoParticle;
import com.avmoga.dpixel.items.TenguKey;
import com.avmoga.dpixel.items.scrolls.ScrollOfPsionicBlast;
import com.avmoga.dpixel.items.weapon.enchantments.Death;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.mechanics.Ballistica;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.sprites.TenguSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.HashSet;

public class TenguEscape extends Mob {

	private static final int JUMP_DELAY = 5;
	private static final int JUMPS_TO_ESCAPE = 5;
	protected static final float SPAWN_DELAY = 2f;
	
	{
		name = Messages.get(Tengu.class, "name");
		spriteClass = TenguSprite.class;
		baseSpeed = 1f;

		HP = HT = 100;
		EXP = 0;
		defenseSkill = 30;
	}

	private int timeToJump = JUMP_DELAY;
	private int escapeCount = JUMPS_TO_ESCAPE;
	private int jumps=0;
	
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange(15, 18);
	}

	@Override
	public int attackSkill(Char target) {
		return 20;
	}

	@Override
	public int dr() {
		return 15;
	}

	
	
	
	@Override
	public void die(Object cause) {

		//super.die(cause);

		if (jumps >= JUMPS_TO_ESCAPE) {
			yell(Messages.get(TenguEscape.class, "escape"));
		} else {
			yell(Messages.get(this, "e2", Dungeon.hero.givenName()));
		    if(!Dungeon.limitedDrops.tengukey.dropped()) {
			Dungeon.limitedDrops.tengukey.drop();
			Dungeon.level.drop(new TenguKey(), pos).sprite.drop();
		}
	    	     }
		destroy();
		sprite.killAndErase();
		CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6);	
					
	}

	@Override
	protected boolean getCloser(int target) {
		if (Level.fieldOfView[target] && jumps<JUMPS_TO_ESCAPE) {
			jump();
			return true;
		} else {
			return super.getCloser(target);
		}
	}

	@Override
	protected boolean canAttack(Char enemy) {
		if(!this.isSilenced()){
		return Ballistica.cast(pos, enemy.pos, false, true) == enemy.pos;
		} else {
			return false;
			
		}
	}

	@Override
	protected boolean doAttack(Char enemy) {
		timeToJump--;
		if (timeToJump <= 0 && jumps<JUMPS_TO_ESCAPE) {
			jump();
			return true;
		} else {
			return super.doAttack(enemy);
		}
	}

	private void jump() {
		timeToJump = JUMP_DELAY;
		escapeCount = JUMPS_TO_ESCAPE;
		
		
		//GLog.i("%s! ",(JUMPS_TO_ESCAPE-jumps));

		int newPos;
		do {
			newPos = Random.Int(Level.getLength());
		} while (!Level.passable[newPos]
				|| Level.adjacent(newPos, Dungeon.hero.pos)
				|| Actor.findChar(newPos) != null);

		sprite.move(pos, newPos);
		move(newPos);

		if (Dungeon.visible[newPos]) {
			CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6);
			Sample.INSTANCE.play(Assets.SND_PUFF);
		}

		spend(1 / speed());
		
		jumps++;
		
		if (jumps>=JUMPS_TO_ESCAPE){
			HP=1;
			Buff.affect(this,Poison.class).set(Poison.durationFactor(this) * 2);
		}
		
	}

	public static TenguEscape spawnAt(int pos) {
		if (Level.passable[pos] && Actor.findChar(pos) == null) {
          
			TenguEscape w = new TenguEscape();
			w.pos = pos;
			w.state = w.HUNTING;
			GameScene.add(w, SPAWN_DELAY);

			//w.sprite.alpha(0);
			//w.sprite.parent.add(new AlphaTweener(w.sprite, 1, 0.5f));

			return w;
  			
		} else {
			return null;
		}
	}

	@Override
	public void notice() {
		super.notice();
		yell(Messages.get(TenguEscape.class, "notice"));
	}

	@Override
	public String description() {
		return Messages.get(Tengu.class, "desc");
	}

	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add(ToxicGas.class);
		RESISTANCES.add(Poison.class);
		RESISTANCES.add(Death.class);
		RESISTANCES.add(ScrollOfPsionicBlast.class);
	}

	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
}
