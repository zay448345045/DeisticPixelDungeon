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
package com.avmoga.dpixel.scenes;

import com.avmoga.dpixel.Assets;
import com.avmoga.dpixel.Badges;
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.DungeonTilemap;
import com.avmoga.dpixel.FogOfWar;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.ShatteredPixelDungeon;
import com.avmoga.dpixel.Statistics;
import com.avmoga.dpixel.actors.Actor;
import com.avmoga.dpixel.actors.blobs.Blob;
import com.avmoga.dpixel.actors.mobs.Mob;
import com.avmoga.dpixel.effects.BannerSprites;
import com.avmoga.dpixel.effects.BlobEmitter;
import com.avmoga.dpixel.effects.EmoIcon;
import com.avmoga.dpixel.effects.Flare;
import com.avmoga.dpixel.effects.FloatingText;
import com.avmoga.dpixel.effects.Ripple;
import com.avmoga.dpixel.effects.SpellSprite;
import com.avmoga.dpixel.items.Heap;
import com.avmoga.dpixel.items.Honeypot;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.bags.PotionBandolier;
import com.avmoga.dpixel.items.bags.ScrollHolder;
import com.avmoga.dpixel.items.bags.SeedPouch;
import com.avmoga.dpixel.items.bags.WandHolster;
import com.avmoga.dpixel.items.potions.Potion;
import com.avmoga.dpixel.items.wands.WandOfBlink;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.levels.RegularLevel;
import com.avmoga.dpixel.levels.features.Chasm;
import com.avmoga.dpixel.music.BGMPlayer;
import com.avmoga.dpixel.plants.Plant;
import com.avmoga.dpixel.sprites.CharSprite;
import com.avmoga.dpixel.sprites.DiscardedItemSprite;
import com.avmoga.dpixel.sprites.HeroSprite;
import com.avmoga.dpixel.sprites.ItemSprite;
import com.avmoga.dpixel.sprites.PlantSprite;
import com.avmoga.dpixel.ui.AttackIndicator;
import com.avmoga.dpixel.ui.Banner;
import com.avmoga.dpixel.ui.BusyIndicator;
import com.avmoga.dpixel.ui.GameLog;
import com.avmoga.dpixel.ui.HealthIndicator;
import com.avmoga.dpixel.ui.LootIndicator;
import com.avmoga.dpixel.ui.QuickSlotButton;
import com.avmoga.dpixel.ui.ResumeIndicator;
import com.avmoga.dpixel.ui.StatusPane;
import com.avmoga.dpixel.ui.Toast;
import com.avmoga.dpixel.ui.Toolbar;
import com.avmoga.dpixel.ui.Window;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.windows.WndBag;
import com.avmoga.dpixel.windows.WndBag.Mode;
import com.avmoga.dpixel.windows.WndGame;
import com.avmoga.dpixel.windows.WndStory;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.SkinnedBlock;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Random;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class GameScene extends PixelScene {
	public static float density = 1;
	private static final String TXT_WELCOME = Messages.get(GameScene.class, "welcome");
	private static final String TXT_WELCOME_BACK = Messages.get(GameScene.class, "welcome_back");

	private static final String TXT_CHASM = Messages.get(GameScene.class, "chasm");
	private static final String TXT_WATER = Messages.get(GameScene.class, "water");
	private static final String TXT_GRASS = Messages.get(GameScene.class, "grass");
	private static final String TXT_DARK = Messages.get(GameScene.class, "dark");
	private static final String TXT_SECRETS = Messages.get(GameScene.class, "secrets");

	static GameScene scene;

	private SkinnedBlock water;
	private DungeonTilemap tiles;
	private FogOfWar fog;
	private HeroSprite hero;

	private GameLog log;

	private BusyIndicator busy;

	private static CellSelector cellSelector;

	private Group terrain;
	private Group ripples;
	private Group plants;
	private Group heaps;
	private Group mobs;
	private Group emitters;
	private Group effects;
	private Group gases;
	private Group spells;
	private Group statuses;
	private Group emoicons;

	private Toolbar toolbar;
	private Toast prompt;

	private AttackIndicator attack;
	private LootIndicator loot;
	private ResumeIndicator resume;

	@Override
	public void create() {

		BGMPlayer.playBGMWithDepth();

		ShatteredPixelDungeon.lastClass(Dungeon.hero.heroClass.ordinal());
		ShatteredPixelDungeon.lastRace(Dungeon.hero.heroRace.ordinal());

		super.create();
		Camera.main.zoom(defaultZoom + ShatteredPixelDungeon.zoom());

		scene = this;

		terrain = new Group();
		add(terrain);

		water = new SkinnedBlock(Level.getWidth() * DungeonTilemap.SIZE,
				Level.HEIGHT * DungeonTilemap.SIZE, Dungeon.level.waterTex());
		terrain.add(water);

		ripples = new Group();
		terrain.add(ripples);

		tiles = new DungeonTilemap();
		terrain.add(tiles);

		Dungeon.level.addVisuals(this);

		plants = new Group();
		add(plants);

		int size = Dungeon.level.plants.size();
		for (int i = 0; i < size; i++) {
			addPlantSprite(Dungeon.level.plants.valueAt(i));
		}

		heaps = new Group();
		add(heaps);

		size = Dungeon.level.heaps.size();
		for (int i = 0; i < size; i++) {
			addHeapSprite(Dungeon.level.heaps.valueAt(i));
		}

		emitters = new Group();
		effects = new Group();
		emoicons = new Group();

		mobs = new Group();
		add(mobs);

		for (Mob mob : Dungeon.level.mobs) {
			addMobSprite(mob);
			if (Statistics.amuletObtained) {
				mob.beckon(Dungeon.hero.pos);
			}
		}

		add(emitters);
		add(effects);

		gases = new Group();
		add(gases);

		for (Blob blob : Dungeon.level.blobs.values()) {
			blob.emitter = null;
			addBlobSprite(blob);
		}

		fog = new FogOfWar(Level.getWidth(), Level.HEIGHT);
		fog.updateVisibility(Dungeon.visible, Dungeon.level.visited,
				Dungeon.level.mapped);
		add(fog);

		spells = new Group();
		add(spells);

		statuses = new Group();
		add(statuses);

		add(emoicons);

		hero = new HeroSprite();
		hero.place(Dungeon.hero.pos);
		hero.updateArmor();
		mobs.add(hero);

		add(new HealthIndicator());

		add(cellSelector = new CellSelector(tiles));

		StatusPane sb = new StatusPane();
		sb.camera = uiCamera;
		sb.setSize(uiCamera.width, 0);
		add(sb);

		toolbar = new Toolbar();
		toolbar.camera = uiCamera;
		toolbar.setRect(0, uiCamera.height - toolbar.height(), uiCamera.width,
				toolbar.height());
		add(toolbar);

		attack = new AttackIndicator();
		attack.camera = uiCamera;
		attack.setPos(uiCamera.width - attack.width(),
				toolbar.top() - attack.height());
		add(attack);

		loot = new LootIndicator();
		loot.camera = uiCamera;
		add(loot);

		resume = new ResumeIndicator();
		resume.camera = uiCamera;
		add(resume);

		layoutTags();

		log = new GameLog();
		log.camera = uiCamera;
		log.setRect(0, toolbar.top(), attack.left(), 0);
		add(log);

		if (Dungeon.depth < Statistics.deepestFloor)
			GLog.i(TXT_WELCOME_BACK, Dungeon.depth);
		else
			GLog.i(TXT_WELCOME, Dungeon.depth);

		Sample.INSTANCE.play(Assets.SND_DESCEND);
		switch (Dungeon.level.feeling) {
		case CHASM:
			GLog.w(TXT_CHASM);
			break;
		case WATER:
			GLog.w(TXT_WATER);
			break;
		case GRASS:
			GLog.w(TXT_GRASS);
			break;
		case DARK:
			GLog.w(TXT_DARK);
			break;
		default:
		}
		if (Dungeon.level instanceof RegularLevel
				&& ((RegularLevel) Dungeon.level).secretDoors > Random
						.IntRange(3, 4)) {
			GLog.w(TXT_SECRETS);
		}

		busy = new BusyIndicator();
		busy.camera = uiCamera;
		busy.x = 1;
		busy.y = sb.bottom() + 1;
		add(busy);

		switch (InterlevelScene.mode) {
		case RESURRECT:
			WandOfBlink.appear(Dungeon.hero, Dungeon.level.entrance);
			new Flare(8, 32).color(0xFFFF66, true).show(hero, 2f);
			break;
		case RETURN:
			WandOfBlink.appear(Dungeon.hero, Dungeon.hero.pos);
			break;
		case FALL:
			Chasm.heroLand();
			break;
		case DESCEND:
			switch (Dungeon.depth) {
			case 1:
				WndStory.showChapter(WndStory.ID_SEWERS);
				break;
			case 6:
				WndStory.showChapter(WndStory.ID_PRISON);
				break;
			case 11:
				WndStory.showChapter(WndStory.ID_CAVES);
				break;
			case 16:
				WndStory.showChapter(WndStory.ID_METROPOLIS);
				break;
			case 22:
				WndStory.showChapter(WndStory.ID_HALLS);
				break;
			}
			if (Dungeon.hero.isAlive() && Dungeon.depth != 22) {
				Badges.validateNoKilling();
			}
			break;
		default:
		}

		ArrayList<Item> dropped = Dungeon.droppedItems.get(Dungeon.depth);
		if (dropped != null) {
			for (Item item : dropped) {
				int pos = Dungeon.level.randomRespawnCell();
				if (item instanceof Potion) {
					((Potion) item).shatter(pos);
				} else if (item instanceof Plant.Seed) {
					Dungeon.level.plant((Plant.Seed) item, pos);
				} else if (item instanceof Honeypot) {
					Dungeon.level.drop(((Honeypot) item).shatter(null, pos),
							pos);
				} else {
					Dungeon.level.drop(item, pos);
				}
			}
			Dungeon.droppedItems.remove(Dungeon.depth);
		}

		Camera.main.target = hero;
		fadeIn();
	}

	@Override
	public void destroy() {

		freezeEmitters = false;

		scene = null;
		Badges.saveGlobal();

		super.destroy();
	}

	@Override
	public synchronized void pause() {
		try {
			Dungeon.saveAll();
			Badges.saveGlobal();
		} catch (IOException e) {
			//
		}
	}

	@Override
	public synchronized void update() {
		if (Dungeon.hero == null) {
			return;
		}

		super.update();

		if (!freezeEmitters)
			water.offset(0, -5 * Game.elapsed);

		Actor.process();

		if (Dungeon.hero.ready && !Dungeon.hero.paralysed) {
			log.newLine();
		}

		if (tagAttack != attack.active || tagLoot != loot.visible
				|| tagResume != resume.visible) {

			boolean atkAppearing = attack.active && !tagAttack;
			boolean lootAppearing = loot.visible && !tagLoot;
			boolean resAppearing = resume.visible && !tagResume;

			tagAttack = attack.active;
			tagLoot = loot.visible;
			tagResume = resume.visible;

			if (atkAppearing || lootAppearing || resAppearing)
				layoutTags();
		}

		cellSelector.enable(Dungeon.hero.ready);
	}

	private boolean tagAttack = false;
	private boolean tagLoot = false;
	private boolean tagResume = false;



	private void layoutTags() {

		float pos = tagAttack ? attack.top() : toolbar.top();

		if (tagLoot) {
			loot.setPos(uiCamera.width - loot.width(), pos - loot.height());
			pos = loot.top();
		}

		if (tagResume) {
			resume.setPos(uiCamera.width - resume.width(),
					pos - resume.height());
		}
	}

	@Override
	protected void onBackPressed() {
		if (!cancel()) {
			add(new WndGame());
		}
	}

	@Override
	protected void onMenuPressed() {
		if (Dungeon.hero.ready) {
			selectItem(null, WndBag.Mode.ALL, null);
		}
	}

	public void brightness(boolean value) {
		water.rm = water.gm = water.bm = tiles.rm = tiles.gm = tiles.bm = value ? 1.5f
				: 1.0f;
		if (value) {
			fog.am = +2f;
			fog.aa = -1f;
		} else {
			fog.am = +1f;
			fog.aa = 0f;
		}
	}

	private void addHeapSprite(Heap heap) {
		ItemSprite sprite = heap.sprite = (ItemSprite) heaps
				.recycle(ItemSprite.class);
		sprite.revive();
		sprite.link(heap);
		heaps.add(sprite);
	}

	private void addDiscardedSprite(Heap heap) {
		heap.sprite = (DiscardedItemSprite) heaps
				.recycle(DiscardedItemSprite.class);
		heap.sprite.revive();
		heap.sprite.link(heap);
		heaps.add(heap.sprite);
	}

	private void addPlantSprite(Plant plant) {
		(plant.sprite = (PlantSprite) plants.recycle(PlantSprite.class))
				.reset(plant);
	}

	private void addBlobSprite(final Blob gas) {
		if (gas.emitter == null) {
			gases.add(new BlobEmitter(gas));
		}
	}

	private void addMobSprite(Mob mob) {
		CharSprite sprite = mob.sprite();
		sprite.visible = Dungeon.visible[mob.pos];
		mobs.add(sprite);
		sprite.link(mob);
	}

	private void prompt(String text) {

		if (prompt != null) {
			prompt.killAndErase();
			prompt = null;
		}

		if (text != null) {
			prompt = new Toast(text) {
				@Override
				protected void onClose() {
					cancel();
				}
			};
			prompt.camera = uiCamera;
			prompt.setPos((uiCamera.width - prompt.width()) / 2,
					uiCamera.height - 60);
			add(prompt);
		}
	}

	private void showBanner(Banner banner) {
		banner.camera = uiCamera;
		banner.x = align(uiCamera, (uiCamera.width - banner.width) / 2);
		banner.y = align(uiCamera, (uiCamera.height - banner.height) / 3);
		add(banner);
	}

	// -------------------------------------------------------

	public static void add(Plant plant) {
		if (scene != null) {
			scene.addPlantSprite(plant);
		}
	}

	public static void add(Blob gas) {
		Actor.add(gas);
		if (scene != null) {
			scene.addBlobSprite(gas);
		}
	}

	public static void add(Heap heap) {
		if (scene != null) {
			scene.addHeapSprite(heap);
		}
	}

	public static void discard(Heap heap) {
		if (scene != null) {
			scene.addDiscardedSprite(heap);
		}
	}

	public static void add(Mob mob) {
		Dungeon.level.mobs.add(mob);
		Actor.add(mob);
		Actor.occupyCell(mob);
		scene.addMobSprite(mob);
	}

	public static void add(Mob mob, float delay) {
		Dungeon.level.mobs.add(mob);
		Actor.addDelayed(mob, delay);
		Actor.occupyCell(mob);
		scene.addMobSprite(mob);
	}

	public static void add(EmoIcon icon) {
		scene.emoicons.add(icon);
	}

	public static void effect(Visual effect) {
		scene.effects.add(effect);
	}

	public static Ripple ripple(int pos) {
		Ripple ripple = (Ripple) scene.ripples.recycle(Ripple.class);
		ripple.reset(pos);
		return ripple;
	}

	public static SpellSprite spellSprite() {
		return (SpellSprite) scene.spells.recycle(SpellSprite.class);
	}

	public static Emitter emitter() {
		if (scene != null) {
			Emitter emitter = (Emitter) scene.emitters.recycle(Emitter.class);
			emitter.revive();
			return emitter;
		} else {
			return null;
		}
	}

	public static FloatingText status() {
		return scene != null ? (FloatingText) scene.statuses
				.recycle(FloatingText.class) : null;
	}

	public static void pickUp(Item item) {
		scene.toolbar.pickup(item);
	}

	public static void updateMap() {
		if (scene != null) {
			scene.tiles.updateMap();
		}
	}

	public static void updateMap(int cell) {
		if (scene != null) {
			scene.tiles.updateMapCell(cell);
		}
	}

	public static void discoverTile(int pos, int oldValue) {
		if (scene != null) {
			scene.tiles.discover(pos, oldValue);
		}
	}

	public static void show(Window wnd) {
		cancelCellSelector();
		scene.add(wnd);
	}

	public static void afterObserve() {
		if (scene != null) {
			scene.fog.updateVisibility(Dungeon.visible, Dungeon.level.visited,
					Dungeon.level.mapped);

			for (Mob mob : Dungeon.level.mobs) {
				mob.sprite.visible = Dungeon.visible[mob.pos];
			}
		}
	}

	public static void flash(int color) {
		scene.fadeIn(0xFF000000 | color, true);
	}

	public static void gameOver() {
		Banner gameOver = new Banner(
				BannerSprites.get(BannerSprites.Type.GAME_OVER));
		gameOver.show(0x000000, 1f);
		scene.showBanner(gameOver);

		Sample.INSTANCE.play(Assets.SND_DEATH);
	}

	public static void bossSlain() {
		if (Dungeon.hero.isAlive()) {
			Banner bossSlain = new Banner(
					BannerSprites.get(BannerSprites.Type.BOSS_SLAIN));
			bossSlain.show(0xFFFFFF, 0.3f, 5f);
			scene.showBanner(bossSlain);

			Sample.INSTANCE.play(Assets.SND_BOSS);
		}
	}
	public static void levelCleared() {
		 if (Dungeon.hero.isAlive()) {
		 			Banner levelCleared = new Banner(
		 					BannerSprites.get(BannerSprites.Type.CLEARED));
		 			levelCleared.show(0xFFFFFF, 0.3f, 5f);
		 			scene.showBanner(levelCleared);
		 
		 			Sample.INSTANCE.play(Assets.SND_BADGE);
		 		}
		 	}

	public static void handleCell(int cell) {
		cellSelector.select(cell);
	}

	public static void selectCell(CellSelector.Listener listener) {
		cellSelector.listener = listener;
		scene.prompt(listener.prompt());
	}

	private static boolean cancelCellSelector() {
		if (cellSelector.listener != null
				&& cellSelector.listener != defaultCellListener) {
			cellSelector.cancel();
			return true;
		} else {
			return false;
		}
	}

	public static WndBag selectItem(WndBag.Listener listener, WndBag.Mode mode,
			String title) {
		cancelCellSelector();

		WndBag wnd = mode == Mode.SEED ? WndBag.getBag(SeedPouch.class,
				listener, mode, title) : mode == Mode.SCROLL ? WndBag.getBag(
				ScrollHolder.class, listener, mode, title)
				: mode == Mode.POTION ? WndBag.getBag(PotionBandolier.class,
						listener, mode, title) : mode == Mode.WAND ? WndBag
						.getBag(WandHolster.class, listener, mode, title)
						: WndBag.lastBag(listener, mode, title);

		scene.add(wnd);

		return wnd;
	}
	
	public static WndBag selectItem(HashSet<? extends Item> items, WndBag.Listener listener, WndBag.Mode mode, 
			String title) {
		cancelCellSelector();

		WndBag wnd = new WndBag(items, listener, mode, title);

		scene.add(wnd);

		return wnd;
	}

	static boolean cancel() {
		if (Dungeon.hero.curAction != null || Dungeon.hero.restoreHealth) {

			Dungeon.hero.curAction = null;
			Dungeon.hero.restoreHealth = false;
			return true;

		} else {

			return cancelCellSelector();

		}
	}

	public static void ready() {
		selectCell(defaultCellListener);
		QuickSlotButton.cancel();
	}

	private static final CellSelector.Listener defaultCellListener = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (Dungeon.hero.handle(cell)) {
				Dungeon.hero.next();
			}
		}

		@Override
		public String prompt() {
			return null;
		}
	};
}
