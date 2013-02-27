package maxpowa.codebase.common;

public enum FormattingCode
{

	RANDOM('k'), BOLD('l'), STRIKE('m'), UNDERLINE('n'), ITALICS('o'), RESET('r');

	private FormattingCode(char code) {
		this.code = code;
	}

	char code;

	@Override
	public String toString() {
		return new StringBuilder().append(MoarReference.FORMATTING_CODE_CHAR).append(code).toString();
	}
}
