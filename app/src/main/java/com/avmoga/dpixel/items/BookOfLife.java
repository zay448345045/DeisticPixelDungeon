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
import com.avmoga.dpixel.Statistics;
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

public class BookOfLife extends Item {

	private static final String TXT_PREVENTING = "强大的魔法阻止了你使用该物品！";
	private static final String TXT_PREVENTING2 = "日志被荆藤封住了！你需要找到带有三相之力碎片的祭台来阅读它...";
	
	public static final float TIME_TO_USE = 1;

	public static final String AC_PORT = "阅读";

	private int specialLevel = 32;
	private int returnDepth = -1;
	private int returnPos;

	{
		name = "生命之书";
		image = ItemSpriteSheet.BOOKOFLIFE;

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
			
			if (Dungeon.depth==specialLevel && hero.pos != Dungeon.level.exit) {
				hero.spend(TIME_TO_USE);
				GLog.w(TXT_PREVENTING2);
				return;
			}
			
			if (Dungeon.depth!=specialLevel && Dungeon.depth>26) {
				hero.spend(TIME_TO_USE);
				GLog.w(TXT_PREVENTING2);
				return;
			}



		}

		if (action == AC_PORT) {

			if (Dungeon.depth==specialLevel){
				this.doDrop(hero);
			}
			Buff buff = Dungeon.hero
						.buff(TimekeepersHourglass.timeFreeze.class);
				if (buff != null)
					buff.detach();

				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
					if (mob instanceof DriedRose.GhostHero)
						mob.destroy();
              if (Dungeon.depth<27){
            	returnDepth = Dungeon.depth;
       			returnPos = hero.pos;
				InterlevelScene.mode = InterlevelScene.Mode.PORT2;
			} else {
				InterlevelScene.mode = InterlevelScene.Mode.RETURN;	
			}
               
				InterlevelScene.returnDepth = returnDepth;
				InterlevelScene.returnPos = returnPos;
				Game.switchScene(InterlevelScene.class);
					
		} else {

			super.execute(hero, action);

		}
	}

	@Override
	public int price() {
		if (!Statistics.amuletObtained){return 9000 * quantity;}
		else {return 500*quantity;}
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

	private static final Glowing GREEN = new Glowing(0x00FF00);

	@Override
	public Glowing glowing() {
		return GREEN;
	}
	
	@Override
	public String info() {
		return "这个书页看起来与周围的环境融为一体。";
	}

}
