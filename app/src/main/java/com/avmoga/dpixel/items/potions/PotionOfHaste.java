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
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Haste;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;

public class PotionOfHaste extends Potion {

	private static final float ALPHA = 0.4f;

	{
		name = Messages.get(this, "name");
	}

	@Override
	public void apply(Hero hero) {
		setKnown();
		Buff.affect(hero, Haste.class, Haste.DURATION);
		GLog.i(Messages.get(this, "effect"));
		Sample.INSTANCE.play(Assets.SND_MELD);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
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
