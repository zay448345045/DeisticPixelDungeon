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
package com.avmoga.dpixel.items.potions;

import com.avmoga.dpixel.Assets;
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Invisibility;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;

public class PotionOfInvisibility extends Potion {

	private static final float ALPHA = 0.4f;

	{
		name = Messages.get(this, "name");
	}

	private static final String TXT_PREVENTING = Messages.get(PotionOfInvisibility.class, "prevent");
	
	@Override
	public void apply(Hero hero) {
		setKnown();
		Buff.affect(hero, Invisibility.class, Invisibility.DURATION);
		GLog.i(Messages.get(this, "invisible"));
		Sample.INSTANCE.play(Assets.SND_MELD);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}
	
	@Override
	public void execute(final Hero hero, String action) {
		if (action.equals(AC_DRINK)) {
			
		  if (Dungeon.depth==29) {
				GLog.w(TXT_PREVENTING);
				return;
		
		   } 
		}
		
	   super.execute(hero, action);
		 	
	}

	@Override
	public int price() {
		return isKnown() ? 40 * quantity : super.price();
	}

	public static void melt(Char ch) {
		if (ch.sprite.parent != null) {
			ch.sprite.parent.add(new AlphaTweener(ch.sprite, ALPHA, 0.4f));
		} else {
			ch.sprite.alpha(ALPHA);
		}
	}
}
