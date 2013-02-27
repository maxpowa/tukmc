package maxpowa.codebase.common;

public enum EnumMaxpowaMods
{

	MOARCORE("core", "MoarCore", "vc"), TUKMC("tukmc", "TukMC", "tm");

	private EnumMaxpowaMods(String fileName, String displayName, String acronym) {
		this.fileName = fileName;
		this.displayName = displayName;
		this.acronym = acronym;
	}

	private String fileName;
	private String displayName;
	private String acronym;

	public String getFileName() {
		return fileName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getAcronym() {
		return acronym;
	}

}
