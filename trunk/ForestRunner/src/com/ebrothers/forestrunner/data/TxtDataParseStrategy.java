package com.ebrothers.forestrunner.data;

import java.io.InputStream;
import java.util.Scanner;

import com.ebrothers.forestrunner.data.LevelData.SpriteData;
import com.ebrothers.forestrunner.sprites.SpriteType;

public class TxtDataParseStrategy implements ParseStrategy {

	@Override
	public LevelData parse(InputStream is) {
		assert (is != null);
		final LevelData level = new LevelData();
		Scanner scanner = new Scanner(is);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] splites = line.split(" ");
			SpriteData data = new SpriteData();
			if (line.startsWith("Ground M")) {
				data.type = SpriteType.GROUND_M;
				data.width = Float.parseFloat(splites[2]);
			} else if (line.startsWith("Ground H")) {
				data.type = SpriteType.GROUND_H;
				data.width = Float.parseFloat(splites[2]);
			} else if (line.startsWith("Ground L")) {
				data.type = SpriteType.GROUND_L;
				data.width = Float.parseFloat(splites[2]);
			} else if (line.startsWith("Gosign")) {
				data.type = SpriteType.GO_SIGN;
				data.rx = Float.parseFloat(splites[1]);
			} else if (line.startsWith("Fire")) {
				data.type = SpriteType.FIRE;
				data.rx = Float.parseFloat(splites[1]);
			} else if (line.startsWith("Gap")) {
				data.type = SpriteType.GAP;
				data.width = Float.parseFloat(splites[1]);
			} else if (line.startsWith("Box")) {
				data.type = SpriteType.BOX;
				data.rx = Float.parseFloat(splites[1]);
			} else if (line.startsWith("Flower")) {
				data.type = SpriteType.FLOWER;
				data.rx = Float.parseFloat(splites[1]);
			} else if (line.startsWith("Bridge")) {
				data.type = SpriteType.BRIDGE;
			} else if (line.startsWith("Dinosaur2")) {
				data.type = SpriteType.DINORSAUR_2;
				data.rx = Float.parseFloat(splites[1]);
			} else if (line.startsWith("Dinosaur3")) {
				data.type = SpriteType.DINORSAUR_3;
				data.rx = Float.parseFloat(splites[1]);
			} else if (line.startsWith("Dinosaur")) {
				data.type = SpriteType.DINORSAUR_1;
				data.rx = Float.parseFloat(splites[1]);
			} else if (line.startsWith("Stopsign")) {
				data.type = SpriteType.STOP_SIGN;
				data.rx = Float.parseFloat(splites[1]);
			} else if (line.startsWith("Trap")) {
				data.type = SpriteType.TRAP;
				data.rx = Float.parseFloat(splites[1]);
			} else if (line.startsWith("Stone")) {
				data.type = SpriteType.STONE;
			} else if (line.startsWith("End")) {
				break;
			} else {
				continue;
			}
			level.addSpriteData(data);
		}
		scanner.close();
		return level;
	}

}
