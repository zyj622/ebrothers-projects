package com.ebrothers.forestrunner.data;

import java.io.InputStream;

public interface ParseStrategy {
	LevelData parse(InputStream is);
}
