package elw.dao;

import elw.vo.*;
import org.akraievoy.gear.G4Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class Ctx {
	private static final Logger log = LoggerFactory.getLogger(Ctx.class);

	public static final String STATE_NONE = "";
	public static final String STATE_GS = "gs";
	public static final String STATE_ECG = "ecg";
	public static final String STATE_ECGS = "ecgs";
	public static final String STATE_C = "c";
	public static final String STATE_CBAV = "cbav";
	public static final String STATE_EGSCBAV = "egscbav";

	public static final char ELEM_ENR = 'e';
	public static final char ELEM_GROUP = 'g';
	public static final char ELEM_STUD = 's';
	public static final char ELEM_COURSE = 'c';
	public static final char ELEM_BUNDLE = 'b';
	public static final char ELEM_ASS = 'a';
	public static final char ELEM_VER = 'v';

	protected static final String order = "egscbav";
	protected static final Map<Character, Integer> elemToOrder = createElemToOrderMap();

	public static final String SEP = "--";

	protected final String enrId;
	protected final String courseId;
	protected final String groupId;
	protected final String studId;
	protected int bundleIdx;
	protected final String assId;
	protected final String verId;
	protected final String initState;

	//	required elements resolved
	protected Enrollment enr;
	protected Student student;
	protected AssignmentBundle bundle;
	protected Assignment ass;
	protected Version ver;
	protected String resolveState;

	//	any optional/intermediate elements
	protected Course course;
	protected Group group;

	public Ctx(
			final String initState,
			String enrId, String groupId, String studId,
			String courseId, int bundleIdx, String assId, final String verId
	) {
		this.assId = assId;
		this.studId = studId;
		this.enrId = enrId;
		this.bundleIdx = bundleIdx;
		this.verId = verId;
		this.initState = initState;
		this.courseId = courseId;
		this.groupId = groupId;
	}

	public static Ctx fromString(final String path) {
		if (path == null || path.trim().length() == 0) {
			return new Ctx(STATE_NONE, null, null, null, null, -1, null, null);
		}

		final String[] comp = path.split(SEP);
		if (comp.length <= 1) {
			return new Ctx(STATE_NONE, null, null, null, null, -1, null, null);
		}

		final String format = comp[0];
		if (format.length() + 1 != comp.length) {
			log.warn("format does not match content, NOT parsing: {}", path);
			return new Ctx(STATE_NONE, null, null, null, null, -1, null, null);
		}

		final String initState = reorder(format);

		final String enrId;
		if (format.indexOf(ELEM_ENR) >= 0) {
			enrId = comp[format.indexOf(ELEM_ENR) + 1];
		} else {
			enrId = null;
		}

		final String courseId;
		if (format.indexOf(ELEM_COURSE) >= 0) {
			courseId = comp[format.indexOf(ELEM_COURSE) + 1];
		} else {
			courseId = null;
		}

		final String groupId;
		if (format.indexOf(ELEM_GROUP) >= 0) {
			groupId = comp[format.indexOf(ELEM_GROUP) + 1];
		} else {
			groupId = null;
		}

		final String studId;
		if (format.indexOf(ELEM_STUD) >= 0) {
			studId = comp[format.indexOf(ELEM_STUD) + 1];
		} else {
			studId = null;
		}

		final int bundleIdx;
		if (format.indexOf(ELEM_BUNDLE) >= 0) {
			bundleIdx = G4Parse.parse(comp[format.indexOf(ELEM_BUNDLE) + 1], -1);
			if (bundleIdx < 0) {
				log.warn("path[{}] must be an integer: {}", format.indexOf(ELEM_BUNDLE) + 1, path);
			}
		} else {
			bundleIdx = -1;
		}

		final String assId;
		if (format.indexOf(ELEM_ASS) >= 0) {
			assId = comp[format.indexOf(ELEM_ASS) + 1];
		} else {
			assId = null;
		}

		final String verId;
		if (format.indexOf(ELEM_VER) >= 0) {
			verId = comp[format.indexOf(ELEM_VER) + 1];
		} else {
			verId = null;
		}

		return new Ctx(initState, enrId, groupId, studId, courseId, bundleIdx, assId, verId);
	}

	public Ctx resolve(EnrollDao enrDao, GroupDao groupDao, CourseDao courseDao) {
		final StringBuilder resolved = new StringBuilder();

		if (inited(ELEM_ENR)) {
			enr = enrDao.findEnrollment(enrId);
			if (enr != null) {
				resolved.append(ELEM_ENR);

				course = courseDao.findCourse(enr.getCourseId());
				if (course != null) {
					resolved.append(ELEM_COURSE);
				} else {
					log.warn("course {} not found: {}", enr.getCourseId(), dump());
				}

				group = groupDao.findGroup(enr.getGroupId());
				if (group != null) {
					resolved.append(ELEM_GROUP);
				} else {
					log.warn("group {} not found: {}", enr.getGroupId(), dump());
				}
			}

		}

		if (inited(ELEM_COURSE) && !has(resolved, ELEM_COURSE)) {
			course = courseDao.findCourse(courseId);
			if (course != null) {
				resolved.append(ELEM_COURSE);
			} else {
				log.warn("course {} not found: {}", courseId, dump());
			}
		}

		if (inited(ELEM_GROUP) && !has(resolved, ELEM_GROUP)) {
			group = groupDao.findGroup(groupId);
			if (group != null) {
				resolved.append(ELEM_GROUP);
			} else {
				log.warn("group {} not found: {}", groupId, dump());
			}
		}

		if (has(resolved, ELEM_GROUP) && inited(ELEM_STUD)) {
			student = IdName.findById(group.getStudents(), studId);
			if (student != null) {
				resolved.append(ELEM_STUD);
			} else {
				log.warn("student {} not found: {}", studId, dump());
			}
		}


		if (has(resolved, ELEM_COURSE) && inited(ELEM_BUNDLE)) {
			if (bundleIdx >= 0 && bundleIdx < course.getAssBundles().length) {
				bundle = course.getAssBundles()[bundleIdx];
				resolved.append(ELEM_BUNDLE);
			} else {
				log.warn("bundle not found: {}", bundleIdx, dump());
			}
		}

		if (has(resolved, ELEM_BUNDLE) && inited(ELEM_ASS)) {
			ass = IdName.findById(bundle.getAssignments(), assId);
			if (ass != null) {
				resolved.append(ELEM_ASS);
			} else {
				log.warn("assignment not found: {}", dump());
			}
		}

		if (has(resolved, ELEM_ASS) && inited(ELEM_VER)) {
			ver = IdName.findById(ass.getVersions(), verId);
			if (ver != null && (student == null || !isVersionIncorrect(student, ass, ver))) {
				resolved.append(ELEM_VER);
			} else {
				log.warn("version not found: {}", verId, this);
			}
		}

		resolveState = resolved.toString();
		return this;
	}

	public static boolean isVersionIncorrect(Student student, Assignment ass, Version ver) {
		final int studId = Integer.parseInt(student.getId());
		final int verIdx = IdName.indexOfId(ass.getVersions(), ver.getId());

		return !ass.isShared() && (studId) % ass.getVersions().length != verIdx;
	}

	public String toString() {
		final String format = norm(getResolveState());

		if (format.isEmpty()) {
			return "";
		}

		final StringBuilder res = new StringBuilder();
		res.append(format);

		for (int i = 0; i < format.length(); i++) {
			res.append(SEP);
			final char comp = res.charAt(i);
			if (comp == ELEM_ENR) {
				res.append(getEnr().getId());
			} else if (comp == ELEM_GROUP) {
				res.append(getGroup().getId());
			} else if (comp == ELEM_STUD) {
				res.append(getStudent().getId());
			} else if (comp == ELEM_COURSE) {
				res.append(getCourse().getId());
			} else if (comp == ELEM_BUNDLE) {
				res.append(bundleIdx);
			} else if (comp == ELEM_ASS) {
				res.append(getAss().getId());
			} else if (comp == ELEM_VER) {
				res.append(getVer().getId());
			}
		}

		return res.toString();
	}

	public String dump() {
		return
				"e:" + enrId + " " +
				"g:" + groupId + " " +
				"s:" + studId + " " +
				"c:" + courseId + " " +
				"b:" + bundleIdx + " " +
				"a:" + assId + " " +
				"v:" + verId;
	}

	public String getInitState() {
		return initState;
	}

	public String getResolveState() {
		return resolveState;
	}

	public Assignment getAss() {
		return ass;
	}

	public AssignmentBundle getBundle() {
		return bundle;
	}

	public Course getCourse() {
		return course;
	}

	public Enrollment getEnr() {
		return enr;
	}

	public Group getGroup() {
		return group;
	}

	public Student getStudent() {
		return student;
	}

	public Version getVer() {
		return ver;
	}

	public Ctx extendEnr(final Enrollment enr) {
		final Ctx ctx = copy();

		if (enr != null) {
			ctx.enr = enr;
			if (ctx.resolveState.indexOf(ELEM_ENR) < 0) {
				ctx.resolveState += ELEM_ENR;
			}
		} else {
			log.warn("extending with no enrollment");
		}

		return ctx;
	}

	public Ctx extendGroup(final Group group) {
		final Ctx ctx = copy();

		if (group != null) {
			if (enr == null || enr.getGroupId().equals(group.getId())) {
				ctx.group = group;
				if (ctx.resolveState.indexOf(ELEM_GROUP) < 0) {
					ctx.resolveState += ELEM_GROUP;
				}
			} else {
				log.warn("extending with wrong group");
			}
		} else {
			log.warn("extending with no group");
		}

		return ctx;
	}

	public Ctx extendCourse(final Course course) {
		final Ctx ctx = copy();

		if (course != null) {
			if (enr == null || enr.getCourseId().equals(course.getId())) {
				ctx.course = course;
				if (ctx.resolveState.indexOf(ELEM_COURSE) < 0) {
					ctx.resolveState += ELEM_COURSE;
				}
			} else {
				log.warn("extending with wrong course");
			}
		} else {
			log.warn("extending with no course");
		}

		return ctx;
	}

	public Ctx extendStudent(final Student student) {
		final Ctx ctx = copy();

		if (student != null) {
			if (group != null && IdName.findById(group.getStudents(), student.getId()) != null) {
				ctx.student = student;
				if (ctx.resolveState.indexOf(ELEM_STUD) < 0) {
					ctx.resolveState += ELEM_STUD;
				}
			} else {
				log.warn("extending with wrong student (or no group)");
			}
		} else {
			log.warn("extending with no student");
		}

		return ctx;
	}

	public Ctx extendBundleIdx(final int bundleIdx) {
		final Ctx ctx = copy();

		if (bundleIdx >= 0) {
			if (course != null && course.getAssBundles().length > bundleIdx) {
				ctx.bundleIdx = bundleIdx;
				ctx.bundle = course.getAssBundles()[bundleIdx];
				if (ctx.resolveState.indexOf(ELEM_BUNDLE) < 0) {
					ctx.resolveState += ELEM_BUNDLE;
				}
			} else {
				log.warn("extending with wrong bundleIdx (or no course resolved)");
			}
		} else {
			log.warn("extending with negative bundleIdx");
		}

		return ctx;
	}

	public Ctx extendAss(final Assignment ass) {
		final Ctx ctx = copy();

		if (ass != null) {
			if (bundle != null && IdName.findById(bundle.getAssignments(), ass.getId()) != null) {
				ctx.ass = ass;
				if (ctx.resolveState.indexOf(ELEM_ASS) < 0) {
					ctx.resolveState += ELEM_ASS;
				}
			} else {
				log.warn("extending with wrong assignment (or no bundle)");
			}
		} else {
			log.warn("extending with no assignment");
		}

		return ctx;
	}

	public Ctx extendVer(final Version ver) {
		final Ctx ctx = copy();

		if (ver != null) {
			if (ass != null && IdName.findById(ass.getVersions(), ver.getId()) != null) {
				ctx.ver = ver;
				if (ctx.resolveState.indexOf(ELEM_VER) < 0) {
					ctx.resolveState += ELEM_VER;
				}
			} else {
				log.warn("extending with wrong version (or no assignment)");
			}
		} else {
			log.warn("extending with no version");
		}

		return ctx;
	}

	public Ctx extendBAV(int bunI, Assignment ass, Version ver) {
		return extendBundleIdx(bunI).extendAss(ass).extendVer(ver);
	}

	protected File getCodeRoot(File uploadsDir) {
		return new File(
				getStudentRoot(uploadsDir),
				getAssDirName()
		);
	}

	protected File getReportRoot(File uploadsDir) {
		return new File(
				getStudentRoot(uploadsDir),
				getAssDirName() + "-doc"
		);
	}

	protected File getScoreRoot(File uploadsDir) {
		return new File(
				getStudentRoot(uploadsDir),
				getAssDirName() + "-scores"
		);
	}

	protected String getAssDirName() {
		return bundleIdx + "." + getAss().getId() + "." + getVer().getId();
	}

	protected File getStudentRoot(File uploadsDir) {
		return new File(
				uploadsDir,
				"" + getCourse().getId() + "." + getGroup().getId() + "/"
				+ getStudent().getId() + "." + getStudent().getName() + "/"
		);
	}

	public Ctx copy() {
		final Ctx copy = new Ctx(initState, enrId, groupId, studId, courseId, bundleIdx, assId, verId);

		copy.resolveState = resolveState;
		copy.enr = enr;
		copy.group = group;
		copy.student = student;
		copy.course = course;
		copy.bundle = bundle;
		copy.ass = ass;
		copy.ver = ver;

		return copy;
	}

	public boolean resolved(final String state) {
		for (int i = 0, stateLength = state.length(); i < stateLength; i++) {
			if (!resolved(state.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	public boolean resolved(char c) {
		return has(resolveState, c);
	}

	public boolean inited(char c) {
		return has(initState, c);
	}

	protected boolean has(StringBuilder resolved, final char elem) {
		return has(resolved.toString(), elem);
	}

	public static boolean has(final String state, char elem) {
		for (int i = 0, stateLen = state.length(); i < stateLen; i++) {
			final char c1 = state.charAt(i);
			if (c1 == elem) {
				return true;
			}
		}

		return false;
	}

	protected static TreeMap<Character, Integer> createElemToOrderMap() {
		final TreeMap<Character, Integer> elemToOrder = new TreeMap<Character, Integer>();

		for (int pos = 0; pos < order.length(); pos++) {
			elemToOrder.put(order.charAt(pos), pos);
		}

		return elemToOrder;
	}

	protected static boolean isRedundant(char elemBefore, char elemAfter) {
		return elemBefore == ELEM_ENR && (elemAfter == ELEM_COURSE || elemAfter == ELEM_GROUP);
	}

	protected static String removeRedundant(final String format) {
		boolean anyRedundant = false;
		boolean[] redundant = new boolean[format.length()];
		for (int beforePos = 0; beforePos < format.length(); beforePos++) {
			char before = format.charAt(beforePos);
			for (int afterPos = beforePos + 1; afterPos < format.length(); afterPos++) {
				char after = format.charAt(afterPos);
				if (isRedundant(before, after)) {
					anyRedundant = redundant[afterPos] = true;
				}
			}
		}

		if (!anyRedundant) {
			return format;
		}

		final StringBuilder result = new StringBuilder();
		result.append(format);
		for (int pos = redundant.length - 1; pos >= 0; pos--) {
			if (redundant[pos]) {
				result.deleteCharAt(pos);
			}
		}
		return result.toString();
	}

	protected static String reorder(final String format) {
		char[] result = null;
		for (int count = 0; count < format.length(); count++) {
			boolean reordered = false;
			for (int pos = 0; pos < format.length() - 1; pos++) {
				char cur;
				char next;
				if (result == null) {
					cur = format.charAt(pos);
					next = format.charAt(pos + 1);
				} else {
					cur = result[pos];
					next = result[pos + 1];
				}

				if (elemToOrder.get(cur) > elemToOrder.get(next)) {
					reordered = true;
					if (result == null) {
						result = format.toCharArray();
					}

					result[pos] = next;
					result[pos + 1] = cur;
				}
			}
			if (!reordered) {
				break;
			}
		}

		return result != null ? String.valueOf(result) : format;
	}

	public static String norm(final String state) {
		return removeRedundant(reorder(state));
	}
}