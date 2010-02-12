package elw.dp.mips.asm;

import elw.dp.mips.Instruction;
import elw.dp.mips.Reg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Noop extends InstructionAssembler {
	static final String RE = "^(" + RE_OP + ")$";
	static final Pattern PATTERN = Pattern.compile(RE);

	protected void assembleInternal(final Matcher matcher, final Instruction template) {
		//  just do nothing, template is set up already
	}

	protected Pattern getPattern() {
		return PATTERN;
	}

	protected Reg reg(final String regGroup) {
		return Reg.fromString(regGroup);
	}
}
