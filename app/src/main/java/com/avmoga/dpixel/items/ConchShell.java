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
package com.avmoga.dpixel.items;

import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.actors.mobs.Mob;
import com.avmoga.dpixel.items.artifacts.DriedRose;
import com.avmoga.dpixel.items.artifacts.TimekeepersHourglass;
import com.avmoga.dpixel.scenes.InterlevelScene;
import com.avmoga.dpixel.sprites.ItemSprite.Glowing;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;
import com.avmoga.dpixel.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class ConchShell extends Item {

	private static final String TXT_PREVENTING = "这里强大的魔法气息让你无法使用上古钥匙！";
	private static final String TXT_PREVENTING2 = "你需要首先击败螃蟹王！";
		
	
	public static final float TIME_TO_USE = 1;

	public static final String AC_PORT = "传送";

	private int specialLevel = 38;
	private int returnDepth = -1;
	private int returnPos;

	{
		name = "海滩之王";
		image = ItemSpriteSheet.SHELL;

		stackable = false;
		unique = true;
	}
	
	private static final String DEPTH = "depth";
	private static final String POS = "pos";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DEPTH, returnDepth);
		if (returnDepth != -1) {
			bundle.put(POS, returnPos);
		}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		returnDepth = bundle.getInt(DEPTH);
		returnPos = bundle.getInt(POS);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_PORT);
		
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		if (action == AC_PORT) {

			if (Dungeon.bossLevel()) {
				hero.spend(TIME_TO_USE);
				GLog.w(TXT_PREVENTING);
				return;
			}
			
			if (Dungeon.depth>25 && Dungeon.depth!=specialLevel) {
				hero.spend(TIME_TO_USE);
				GLog.w(TXT_PREVENTING);
				return;
			}
			
			if (Dungeon.depth==specialLevel && !Dungeon.crabkingkilled && !Dungeon.level.reset) {
				hero.spend(TIME_TO_USE);
				GLog.w(TXT_PREVENTING2);
				return;
			}
			
			if (Dungeon.depth==1) {
				hero.spend(TIME_TO_USE);
				GLog.w(TXT_PREVENTING);
				return;
			}


		}

		if (action == AC_PORT) {

				Buff buff = Dungeon.hero
						.buff(TimekeepersHourglass.timeFreeze.class);
				if (buff != null)
					buff.detach();

				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
					if (mob instanceof DriedRose.GhostHero)
						mob.destroy();
              if (Dungeon.depth<25 && !Dungeon.bossLevel()){
            	
            	returnDepth = Dungeon.depth;
       			returnPos = hero.pos;
				InterlevelScene.mode = InterlevelScene.Mode.PORTCRAB;
			} else {						 
											
				this.doDrop(hero);
																
				InterlevelScene.mode = InterlevelScene.Mode.RETURN;	
			}
               	InterlevelScene.returnDepth = returnDepth;
				InterlevelScene.returnPos = returnPos;
				Game.switchScene(InterlevelScene.class);
					
		} else {

			super.execute(hero, action);

		}
	}
	
	public void reset() {
		returnDepth = -1;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}


	private static final Glowing BLACK = new Glowing(0x009999);

	@Override
	public Glowing glowing() {
		return BLACK;
	}

	@Override
	public String info() {
		return "这个书页会使你传送至螃蟹王的所在地。";
	}
}
