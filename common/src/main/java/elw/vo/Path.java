package elw.vo;

import java.util.Arrays;

public class Path {
	public static final Path EMPTY = new Path();
	protected final String[] path;

	public Path(String[] path) {
		this.path = path;
	}

	public Path() {
		this.path = new String[0];
	}

	public Path(final String singleLevel) {
		this.path = new String[] {singleLevel};
	}

	public Path(final String outerLevel, final String innerLevel) {
		this.path = new String[] {outerLevel, innerLevel};
	}

	public String[] getPath() {
		return path.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Path path1 = (Path) o;

		return Arrays.equals(path, path1.path);

	}

	@Override
	public int hashCode() {
		return path != null ? Arrays.hashCode(path) : 0;
	}

	@Override
	public String toString() {
		return "{" + Arrays.deepToString(path) + "}";
	}

	public boolean intersects(final Path that) {
		final int len = Math.min(this.path.length, that.path.length);

		for (int i = 0; i < len; i++) {
			final String elemThis = this.path[i];
			final String elemThat = that.path[i];
			if (elemThis != null && elemThat != null && !elemThis.equals(elemThat)) {
				return false;
			}
		}

		return true;
	}

	public Path set(final int index, final String value) {
		final String[] newPath = this.path.clone();

		newPath[index] = value;

		return new Path(newPath);
	}

	public Path setLast(final String value) {
		return set(this.path.length - 1, value);
	}
}