package maxpowa.codebase.common;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

public class MoarReference {

	public static final String VERSION = "1.0.6";

	public static final int CORNER_TEXT_ENTRY_SIZE = 11;

	public static final char FORMATTING_CODE_CHAR = '\u00a7';

	public static File cacheFile;
	public static Set<String> loadedMpMods = new TreeSet();

}
