package elw.dao;

import elw.vo.*;
import elw.vo.Class;
import org.akraievoy.gear.G4Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Dealing with persisting, enumerating and retrieving scoring objects.
 */
public class ScoreDao extends Dao<Score> {
	private static final Logger log = LoggerFactory.getLogger(ScoreDao.class);

	public ScoreDao() {
	}

	@Override
	public Path pathFromMeta(Score score) {
		return new Path(score.getPath());
	}

	public Stamp createScore(Ctx ctx, final String slotId, final String fileId, Score score) throws IOException {
		final Path path = toPath(ctx, slotId, fileId);
		score.setPath(path.getPath());
		return create(path, score, null, null);
	}

	public SortedMap<Stamp, Entry<Score>> findAllScores(Ctx ctx, final String slotId, final String fileId) {
		return findAll(toPath(ctx, slotId, fileId), false, false);
	}

	public Entry<Score> findScoreByStamp(Ctx ctx, Stamp stamp, final String slotId, final String fileId) {
		return findByStamp(toPath(ctx, slotId, fileId), stamp, false, false);
	}

	public Entry<Score> findLastScore(Ctx ctx, final String slotId, final String fileId) {
		return findLast(toPath(ctx, slotId, fileId), false, false);
	}

	protected Path toPath(Ctx ctx, final String slotId, final String fileId) {
		if (ctx == null || !ctx.resolved(Ctx.STATE_EGSCIV)) {
			throw new IllegalStateException("context not fully set up: " + String.valueOf(ctx));
		}

		final Path path = new Path(new String[]{
				ctx.getGroup().getId(),
				ctx.getStudent().getId(),
				ctx.getCourse().getId(),
				String.valueOf(ctx.getIndex()) + "-" +
				ctx.getAssTypeId() + "-" +
				ctx.getAss().getId() + "-" +
				ctx.getVer().getId(),
				slotId,
				fileId
		});

		return path;
	}

	//	LATER move this to some ScoreManager class
	public SortedMap<Stamp, Entry<Score>> findAllScoresAuto(Ctx ctx, FileSlot slot, Entry<FileMeta> file) {
		final Stamp newStamp = createStamp();
		final Score newScore = updateAutos(ctx, slot.getId(), file, null);
		final Entry<Score> newEntry = new Entry<Score>(null, null, null, newScore, newStamp.getTime());
		final SortedMap<Stamp, Entry<Score>> allScores = findAllScores(ctx, slot.getId(), file.getMeta().getId());
		allScores.put(newStamp, newEntry);
		return allScores;
	}

	public static Score updateAutos(Ctx ctx, final String slotId, Entry<FileMeta> file, final Score score) {
		final Map<String, Integer> dueMap = ctx.getIndexEntry().getClassDue();
		final Class classDue;
		if (dueMap == null) {
			classDue = ctx.getEnr().getClasses().get(ctx.getEnr().getClasses().size() - 1);
		} else {
			classDue = ctx.getEnr().getClasses().get(dueMap.get(slotId));
		}
		final FileSlot slot = ctx.getAssType().findSlotById(slotId);

		final Map<String, Double> vars = new TreeMap<String, Double>();
		vars.put("$overdue", (double) classDue.computeDaysOverdue(file.getMeta().getCreateStamp()));
		if (slot.getValidator() != null && file.getMeta().isValidated()) {
			vars.put("$passratio", (double) classDue.computeDaysOverdue(file.getMeta().getCreateStamp()));
		}

		final Score res = score == null ? new Score() : score.copy();
		for (Criteria c : slot.getCriterias()) {
			final Double ratio;
			final Integer powDef;

			if (res.contains(slot, c)) {
				continue;
			}

			if (!c.isAuto()) {
				ratio = G4Parse.parse(c.getRatio(), (Double) null);
				powDef = G4Parse.parse(c.getPowDef(), 0);
			} else {
				ratio = c.resolveRatio(vars);
				powDef = c.resolvePowDef(vars);
			}
			if (ratio != null && powDef != null) {
				final String id = res.idFor(slot, c);
				res.getPows().put(id, powDef);
				res.getRatios().put(id, ratio);
			}
		}

		return res;
	}
}