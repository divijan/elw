package ua.iasa.pathsim.domain.asm;

import ua.iasa.pathsim.domain.*;

import java.util.regex.*;

public class RType_3 extends InstructionAssembler {
    static final String RE = "^(" + RE_OP + ") \\$(" + RE_REG + "), \\$(" + RE_REG + "), \\$(" + RE_REG + ")$";
    static final Pattern PATTERN = Pattern.compile(RE);

    protected void assembleInternal(final Matcher matcher, final Instruction template) throws AssemblyException {
        template.setD(reg(matcher.group(2)));

        final String name = template.getOpName();

        if ("sllv".equals(name) || "srlv".equals(name)) {
            template.setT(reg(matcher.group(3)));
            template.setS(reg(matcher.group(4)));
        } else {
            template.setS(reg(matcher.group(3)));
            template.setT(reg(matcher.group(4)));
        }
    }

    protected Pattern getPattern() {
        return PATTERN;
    }
}
