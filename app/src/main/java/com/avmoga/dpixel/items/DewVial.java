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

import com.avmoga.dpixel.Assets;
import com.avmoga.dpixel.Badges;
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.Statistics;
import com.avmoga.dpixel.actors.blobs.Blob;
import com.avmoga.dpixel.actors.blobs.Water;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Haste;
import com.avmoga.dpixel.actors.buffs.Invisibility;
import com.avmoga.dpixel.actors.buffs.Levitation;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.actors.hero.HeroClass;
import com.avmoga.dpixel.actors.hero.HeroRace;
import com.avmoga.dpixel.effects.Speck;
import com.avmoga.dpixel.effects.particles.ShadowParticle;
import com.avmoga.dpixel.items.bags.Bag;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.levels.Terrain;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.sprites.CharSprite;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.utils.Utils;
import com.avmoga.dpixel.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DewVial extends Item {

	private static final int MAX_VOLUME = 100;
	private static final int EXT_VOLUME = 300;
	private static final int BLESS_VOLUME = 10;
	
	
	private static final int MAX_VOLUME(){
		return Dungeon.wings ? EXT_VOLUME : MAX_VOLUME;
	}
	private static final String AC_SIP = Messages.get(DewVial.class, "ac_sip");
	private static final String AC_DRINK = Messages.get(DewVial.class, "ac_drink");
	private static final String AC_WATER = Messages.get(DewVial.class, "ac_water");
	private static final String AC_SPLASH = Messages.get(DewVial.class, "ac_splash");
	private static final String AC_BLESS = Messages.get(DewVial.class, "ac_bless");

	private static final float TIME_TO_DRINK = 1f;
	private static final float TIME_TO_WATER = 3f;

	private static final String TXT_VALUE = Messages.get(DewVial.class, "value");
	private static final String TXT_STATUS = "%d";

	private static final String TXT_COLLECTED = Messages.get(DewVial.class, "collected");
	private static final String TXT_WATERED = Messages.get(DewVial.class, "watered");
	private static final String TXT_REFRESHED = Messages.get(DewVial.class, "refreshed");
	private static final String TXT_BLESSED = Messages.get(DewVial.class, "blessed");
	private static final String TXT_FULL = Messages.get(DewVial.class, "full");
	private static final String TXT_EMPTY = Messages.get(DewVial.class, "empty");
	private static final String TXT_SELECT = Messages.get(DewVial.class, "select");

	private static final String TXT_PROCCED = "你的背包闪烁着净化的光芒，一股恶毒的魔力能量散去。";
	private static final String TXT_NOT_PROCCED = "你的背包闪烁着净化的光芒，但什么事情都没有发生！";

	private static final String TXT_LOOKS_BETTER = "你的 %s 现在看起来更好了";


	{
		name = "露珠瓶";
		image = ItemSpriteSheet.VIAL;

		defaultAction = AC_DRINK;

		unique = true;
	}

	private int volume = 0;
	
	public int checkVol () {
		return volume;
	}

	public void setVol (int vol) {
		volume=vol;		
	}
	
	private static final String VOLUME = "volume";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(VOLUME, volume);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		volume = bundle.getInt(VOLUME);
	}
	
	
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (volume > 99) {
			actions.add(AC_DRINK);
					
			if (Dungeon.dewWater){
				actions.add(AC_WATER);
			} else {
				actions.add(AC_SIP);
			}
			
			actions.add(AC_SPLASH);
			actions.add(AC_BLESS);
		}	
		else if (volume > 49) {
			actions.add(AC_DRINK);
			if (Dungeon.dewWater){
				actions.add(AC_WATER);
			} else {
				actions.add(AC_SIP);
			}
			actions.add(AC_SPLASH);
		}
		
		else if (volume > 29) {
			actions.add(AC_DRINK);
			actions.add(AC_SIP);
			actions.add(AC_SPLASH);
		}
		else if (volume > 2) {
			actions.add(AC_DRINK);
			actions.add(AC_SIP);
		} 
		else if (volume > 0) {
			actions.add(AC_SIP);
			
		}
		return actions;
	}

	@Override
	public void execute(final Hero hero, String action) {
		
		if (action.equals(AC_SIP)) {

			if (volume > 0) {

				int value = 1 + (Dungeon.depth - 1) / 5;
				if (hero.heroClass == HeroClass.HUNTRESS) {
					value++;
				}
				if (volume < 3){
				value *= volume;
				} else {
				value *= 3;	
				}
				int effect = Math.min(hero.HT - hero.HP, value);
				if (effect > 0) {
					hero.HP += effect;
					hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
					hero.sprite.showStatus(CharSprite.POSITIVE, TXT_VALUE, effect);
				}

				if (volume < 3) {

					volume = 0;

				} else {

					volume = volume - 3;
				}

				hero.spend(TIME_TO_DRINK);
				hero.busy();

				Sample.INSTANCE.play(Assets.SND_DRINK);
				hero.sprite.operate(hero.pos);

				updateQuickslot();

			} else {
				GLog.w(TXT_EMPTY);
			}

		
		
		}else if (action.equals(AC_DRINK)) {

			if (volume > 0) {

				int value = 1 + (Dungeon.depth - 1) / 5;
				if (hero.heroClass == HeroClass.HUNTRESS) {
					value++;
				}
				value *= volume;
				value = (int) Math.max(volume * volume * .01 * hero.HT, value);
				int effect = Math.min(hero.HT - hero.HP, value);
				if (effect > 0) {
					hero.HP += effect;
					hero.sprite.emitter().burst(Speck.factory(Speck.HEALING),
							volume > 5 ? 2 : 1);
					hero.sprite.showStatus(CharSprite.POSITIVE, TXT_VALUE,
							effect);
				}

				if (volume < 10) {

					volume = 0;

				} else {

					volume = volume - 10;
				}

				hero.spend(TIME_TO_DRINK);
				hero.busy();

				Sample.INSTANCE.play(Assets.SND_DRINK);
				hero.sprite.operate(hero.pos);

				updateQuickslot();

			} else {
				GLog.w(TXT_EMPTY);
			}

		} else if (action.equals(AC_WATER)) {
			
			Statistics.waters++;			
			int positive = 0;
			int negative = 0;

			int distance = 1 + positive + negative;

			if (distance <= 0) {
				level /= 2 - distance;
				distance = 1;
			}

			int cx = hero.pos % Level.getWidth();
			int cy = hero.pos / Level.getWidth();
			int ax = cx - distance;
			if (ax < 0) {
				ax = 0;
			}
			int bx = cx + distance;
			if (bx >= Level.getWidth()) {
				bx = Level.getWidth() - 1;
			}
			int ay = cy - distance;
			if (ay < 0) {
				ay = 0;
			}
			int by = cy + distance;
			if (by >= Level.HEIGHT) {
				by = Level.HEIGHT - 1;
			}

			
			for (int y = ay; y <= by; y++) {
				for (int x = ax, p = ax + y * Level.getWidth(); x <= bx; x++, p++) {

					if (Dungeon.visible[p]) {
						int c = Dungeon.level.map[p];
						
						if (c == Terrain.GRASS) {
							GameScene.add(Blob.seed(p, (2) * 20, Water.class));
						}
					}
				}
			}
			volume = volume - 2;
			GLog.i(TXT_WATERED);
			hero.sprite.operate(hero.pos);
			hero.busy();
			hero.spend(TIME_TO_WATER);
		
		} else if (action.equals(AC_SPLASH)) {	
			Buff.affect(hero, Haste.class, Haste.DURATION);
			Buff.affect(hero, Invisibility.class, Invisibility.DURATION);
			if(Dungeon.wings){
				Buff.affect(hero, Levitation.class, Levitation.DURATION);
			    GLog.i("你的身体悬浮在空中。");
			}
			GLog.i(TXT_REFRESHED);
			GLog.i(Messages.get(DewVial.class, "inv"));
			GLog.i(Messages.get(DewVial.class, "haste"));
			volume = volume - 10;
			
		} else if (action.equals(AC_BLESS) && !Dungeon.dewDraw) {	

			boolean procced = uncurse(hero, hero.belongings.backpack.items.toArray(new Item[0]));
			procced = uncurse(hero, hero.belongings.weapon,
					hero.belongings.armor, hero.belongings.misc1,
					hero.belongings.misc2)
					|| procced;
			if(hero.heroRace() == HeroRace.WRAITH){
				hero.damage(Random.Int(0, hero.lvl / 2), this);
			}
			if (procced) {
				GLog.p(TXT_PROCCED);
			} else {
				GLog.i(TXT_NOT_PROCCED);
			}
													
			volume = volume - 50;
		} else if (action.equals(AC_BLESS) && Dungeon.dewDraw) {	

			curUser = hero;
			GameScene.selectItem(itemSelector, WndBag.Mode.UPGRADEDEW,	TXT_SELECT);

		} else {

			super.execute(hero, action);

		}
	}

	private final WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect(Item item) {
			if (item != null) {
				upgrade(item);
				volume = volume - 90;
			}
		}
	};

	public static boolean uncurse(Hero hero, Item... items) {
		
		
        int levelLimit = Math.max(5, 5+Math.round(Statistics.deepestFloor/3));
        if (hero.heroClass == HeroClass.MAGE){levelLimit++;}
        
        float lvlchance = 0.33f;
        if (hero.heroClass == HeroClass.MAGE){lvlchance = 0.38f;}
        
        boolean procced = false;
		boolean proccedUp = false;
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			if (item != null && item.cursed) {
				item.uncurse();
				if(item.level<0){item.upgrade(-item.level);} //upgrade to even
				if (item.cursed==false) {procced = true;}
				hero.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
			}
			
			if (item != null && Random.Float()<lvlchance && item.isUpgradable() && item.level < levelLimit){
			    item.upgrade();
			    proccedUp = true;
			    hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
			    GLog.p(TXT_LOOKS_BETTER, item.name());
			    Badges.validateItemLevelAquired(item);
			}
			
			if (item instanceof Bag) {
				for (Item bagItem: ((Bag)item).items){
                   if (bagItem != null && bagItem.cursed) {
                	   bagItem.uncurse();
                	   if(bagItem.level<0){bagItem.upgrade(-bagItem.level);}
                	   if (bagItem.cursed==false) {procced = true;}
                   }
                   
                   if (bagItem != null && Random.Float()<lvlchance && bagItem.isUpgradable() && bagItem.level < levelLimit){
                	   bagItem.upgrade();
					    proccedUp = true;
					    hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
					    GLog.p(TXT_LOOKS_BETTER, bagItem.name());
					    Badges.validateItemLevelAquired(bagItem);
					}
				}   
			}			
		}
		
		if (proccedUp){GLog.i(TXT_BLESSED);}
					
		return procced;
	}
	
	private void upgrade(Item item) {

		GLog.w(TXT_LOOKS_BETTER, item.name());

		item.upgrade();
		
		curUser.sprite.operate(curUser.pos);
		curUser.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
		Badges.validateItemLevelAquired(item);
		
		curUser.busy();
		
	}

	
	public void empty() {
		volume = volume - 10;
		updateQuickslot();
	}
	
	public void sip() {
		volume = volume - 1;
		updateQuickslot();
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	public boolean isFullBless() {
		return volume >= BLESS_VOLUME;
	}
	

	public boolean isFull() {
		return volume >= MAX_VOLUME();
	}

	public void collectDew(Dewdrop dew, int amountToFill) {

		GLog.i(TXT_COLLECTED);
		volume += dew.quantity;
		if (volume >= MAX_VOLUME()) {
			volume = MAX_VOLUME();
			GLog.p(TXT_FULL);
		}

		updateQuickslot();
	}

	public void collectDew(RedDewdrop dew) {

		GLog.i(TXT_COLLECTED);
		volume += (dew.quantity*5);
		if (volume >= MAX_VOLUME()) {
			volume = MAX_VOLUME();
			GLog.p(TXT_FULL);
		}

		updateQuickslot();
	}
	
	public void collectDew(YellowDewdrop dew) {

		GLog.i(TXT_COLLECTED);
		volume += (dew.quantity*2);
		if (volume >= MAX_VOLUME()) {
			volume = MAX_VOLUME();
			GLog.p(TXT_FULL);
		}

		updateQuickslot();
	}

	public void collectDew(VioletDewdrop dew) {

		GLog.i(TXT_COLLECTED);
		volume += (dew.quantity*50);
		if (volume >= MAX_VOLUME()) {
			volume = MAX_VOLUME();
			GLog.p(TXT_FULL);
		}

		updateQuickslot();
	}
	
	
	public void fill() {
		volume = volume + 50;
		if (volume >= MAX_VOLUME()) {
			volume = MAX_VOLUME();
			GLog.p(TXT_FULL);
		}
		updateQuickslot();
	}

	// removed as people need a bigger distinction to realize the dew vial
	// doesn't revive.
	/*
	 * private static final Glowing WHITE = new Glowing( 0xFFFFCC );
	 * 
	 * @Override public Glowing glowing() { return isFull() ? WHITE : null; }
	 */

	@Override
	public String status() {
		return Utils.format(TXT_STATUS, volume, MAX_VOLUME);
	}

	@Override
	public String info() {
		return Messages.get(this, "desc");
	}

	@Override
	public String toString() {
		return super.toString() + " (" + status() + (MAX_VOLUME() > 1000 ? "" : Utils.format("/%d", MAX_VOLUME())) + ")";
	}
}
