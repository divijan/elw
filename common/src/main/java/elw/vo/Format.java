/*
 * ELW : e-learning workspace
 * Copyright (C) 2010  Anton Kraievoy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package elw.vo;

import java.util.Date;

//  LATER this smells, presentations belong to web module
public interface Format {
    String formatSize(long size);
    String format(Date date);
    String format(long dateLong);
    String format(Date date, String pattern);
    String format(long dateLong, String pattern);
    String format(Date date, String pattern, String patternWeek, String patternToday);
    String format(long dateLong, String pattern, String patternWeek, String patternToday);
    String formatAge(Date time);
    String formatAge(long timeMillis);
    String formatDuration(long timeMillis);
    String json(Object o);
    String esc(String str);
    String lines(String text);
    String format2(Double num);
}
