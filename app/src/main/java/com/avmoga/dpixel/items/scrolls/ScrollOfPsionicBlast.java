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
package com.avmoga.dpixel.items.scrolls;

import com.avmoga.dpixel.Assets;
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.ResultDescriptions;
import com.avmoga.dpixel.actors.buffs.Blindness;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Invisibility;
import com.avmoga.dpixel.actors.buffs.Paralysis;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.actors.mobs.Mob;
import com.avmoga.dpixel.actors.mobs.Singularity;
import com.avmoga.dpixel.items.Heap;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.utils.Utils;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class ScrollOfPsionicBlast extends Scroll {

	{
		name = Messages.get(this, "name");

		bones = true;
		consumedValue = 10;
	}
	private final String TXT_DET = "火焰加剧了灵能爆炸的威力。";
	private final String TXT_UNBOUND = "失去了精神力引导这股力量，爆炸变得混沌而随机！";
	@Override
	public void detonate(Heap heap){
		GLog.w(TXT_UNBOUND);
		Singularity sing = new Singularity();
		sing.pos = heap.pos;
		GameScene.add(sing);
		
	}
	@Override
	public void detonateIn(Hero hero){
		GLog.w(TXT_DET);
		GameScene.flash(0xDC143C);
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])){
			if (Level.fieldOfView[mob.pos]){
				mob.damage(mob.HT, this);
			}
		}
		hero.damage(Math.max((curUser.HT / 2), (curUser.HT / 2)), this);
		Buff.prolong(hero, Paralysis.class, Random.Int(4, 6));
		Buff.prolong(hero, Blindness.class, Random.Int(6, 9));
	}

	@Override
	protected void doRead() {

		GameScene.flash(0xFFFFFF);

		Sample.INSTANCE.play(Assets.SND_BLAST);
		Invisibility.dispel();

		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (Level.fieldOfView[mob.pos]) {
				mob.damage(mob.HT, this);
			}
		}

		curUser.damage(Math.max(curUser.HT / 5, curUser.HP / 2), this);
		Buff.prolong(curUser, Paralysis.class, Random.Int(4, 6));
		Buff.prolong(curUser, Blindness.class, Random.Int(6, 9));
		Dungeon.observe();

		setKnown();

		curUser.spendAndNext(TIME_TO_READ);

		if (!curUser.isAlive()) {
			Dungeon.fail(Utils.format(ResultDescriptions.ITEM, name));
			GLog.n("The Psionic Blast tears your mind apart...");
		}
	}

	@Override
	public String desc() {
		return "这张卷轴蕴含着毁灭性的能量，一旦引导出来将撕裂视野内所有生物的心灵。\n\n" +
				"这股力量的释放也会对阅读者产生严重伤害，并导致暂时性的失明和眩晕。";
	}

	@Override
	public int price() {
		return isKnown() ? 80 * quantity : super.price();
	}
}
