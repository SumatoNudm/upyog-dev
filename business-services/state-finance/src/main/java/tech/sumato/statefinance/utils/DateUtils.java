package tech.sumato.statefinance.utils;


import com.google.common.collect.ImmutableMap;
import tech.sumato.statefinance.exception.ApplicationRuntimeException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static tech.sumato.statefinance.config.core.LocalizationSettings.*;
import static tech.sumato.statefinance.utils.NumberToWordConverter.numberToWords;

public final class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    private static final String DEFAULT_YEAR_PATTERN = "yyyy";
    private static final String FILE_NAME_DATE_PATTERN = "yyyyMMddHHmm";

    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

    private static final ZoneId ZONE_ID = ZoneId.of(jodaTimeZone().getId());

    private static final String[] DATE_IN_WORDS = {
            "First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth", "Ninth", "Tenth",
            "Eleventh", "Twelfth", "Thirteenth", "Fourteenth", "Fifteenth", "Sixteenth", "Seventeenth",
            "Eighteenth", "Nineteenth", "Twentieth", "Twenty first", "Twenty second", "Twenty third",
            "Twenty fourth", "Twenty fifth", "Twenty sixth", "Twenty seventh", "Twenty eighth",
            "Twenty ninth", "Thirtieth", "Thirty first"
    };

    private static final Map<Integer, String> MONTH_SHORT_NAMES = ImmutableMap.<Integer, String>builder()
            .put(1, "Jan").put(2, "Feb").put(3, "Mar").put(4, "Apr")
            .put(5, "May").put(6, "Jun").put(7, "Jul").put(8, "Aug")
            .put(9, "Sep").put(10, "Oct").put(11, "Nov").put(12, "Dec")
            .build();

    private static final Map<Integer, String> MONTH_FULL_NAMES = ImmutableMap.<Integer, String>builder()
            .put(1, "January").put(2, "February").put(3, "March").put(4, "April")
            .put(5, "May").put(6, "June").put(7, "July").put(8, "August")
            .put(9, "September").put(10, "October").put(11, "November").put(12, "December")
            .build();

    private static final Map<Integer, String> FIN_MONTH_NAMES = ImmutableMap.<Integer, String>builder()
            .put(1, "April").put(2, "May").put(3, "June").put(4, "July")
            .put(5, "August").put(6, "September").put(7, "October").put(8, "November")
            .put(9, "December").put(10, "January").put(11, "February").put(12, "March")
            .build();

    private DateUtils() {}

    /* -------------------- FORMATTERS -------------------- */

    private static DateTimeFormatter formatter(String pattern) {
        return FORMATTER_CACHE.computeIfAbsent(
                pattern,
                p -> DateTimeFormatter.ofPattern(p, locale())
        );
    }

    /* -------------------- CURRENT DATE -------------------- */

    public static String currentYear() {
        return LocalDate.now(ZONE_ID).format(formatter(DEFAULT_YEAR_PATTERN));
    }

    public static String currentDateToDefaultDateFormat() {
        return LocalDate.now(ZONE_ID).format(formatter(datePattern()));
    }

    public static String currentDateToGivenFormat(String format) {
        return ZonedDateTime.now(ZONE_ID).format(formatter(format));
    }

    public static String currentDateToFileNameFormat() {
        return ZonedDateTime.now(ZONE_ID).format(formatter(FILE_NAME_DATE_PATTERN));
    }

    /* -------------------- DATE CONVERSIONS -------------------- */

    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZONE_ID).toInstant());
    }

    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZONE_ID).toLocalDate();
    }

    public static Date endOfDay(Date date) {
        return Date.from(
                toLocalDate(date)
                        .atTime(LocalTime.MAX)
                        .atZone(ZONE_ID)
                        .toInstant()
        );
    }

    public static Date startOfDay(Date date) {
        return Date.from(
                toLocalDate(date)
                        .atStartOfDay(ZONE_ID)
                        .toInstant()
        );
    }

    /* -------------------- DATE DIFFERENCE -------------------- */

    public static int daysBetween(Date start, Date end) {
        return (int) ChronoUnit.DAYS.between(toLocalDate(start), toLocalDate(end));
    }

    public static int monthsBetween(Date start, Date end) {
        return (int) ChronoUnit.MONTHS.between(toLocalDate(start), toLocalDate(end));
    }

    public static int yearsBetween(Date start, Date end) {
        return (int) ChronoUnit.YEARS.between(toLocalDate(start), toLocalDate(end));
    }

    /* -------------------- UTILITIES -------------------- */

    public static Date getDate(String date, String pattern) {
        try {
            return isNotBlank(date)
                    ? new SimpleDateFormat(pattern, locale()).parse(date)
                    : null;
        } catch (ParseException e) {
            throw new ApplicationRuntimeException("Invalid date or pattern", e);
        }
    }

    public static Date today() {
        return toDate(LocalDate.now(ZONE_ID));
    }

    public static Date tomorrow() {
        return toDate(LocalDate.now(ZONE_ID).plusDays(1));
    }

    public static boolean between(Date date, Date from, Date to) {
        return !date.before(from) && !date.after(to);
    }

    public static String convertToWords(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String day = DATE_IN_WORDS[cal.get(Calendar.DAY_OF_MONTH) - 1];
        String month = MONTH_FULL_NAMES.get(cal.get(Calendar.MONTH) + 1);
        String year = numberToWords(BigDecimal.valueOf(cal.get(Calendar.YEAR)), false, false);

        return day + " " + month + " " + year;
    }

    public static Map<Integer, String> getAllMonths() {
        return MONTH_SHORT_NAMES;
    }

    public static Map<Integer, String> getAllMonthsWithFullNames() {
        return MONTH_FULL_NAMES;
    }

    public static Map<Integer, String> getAllFinancialYearMonthsWithFullNames() {
        return FIN_MONTH_NAMES;
    }



    public static String getFormattedDate(Date date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }

        return Instant.ofEpochMilli(date.getTime())
                .atZone(zoneId())   // replaces jodaTimeZone()
                .format(DateTimeFormatter.ofPattern(pattern, locale()));
    }

    public static ZoneId zoneId() {
        return timeZone().toZoneId();
    }

}
