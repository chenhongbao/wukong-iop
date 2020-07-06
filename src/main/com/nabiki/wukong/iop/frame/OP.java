/*
 * Copyright (c) 2020 Hongbao Chen <chenhongbao@outlook.com>
 *
 * Licensed under the  GNU Affero General Public License v3.0 and you may not use
 * this file except in compliance with the  License. You may obtain a copy of the
 * License at
 *
 *                    https://www.gnu.org/licenses/agpl-3.0.txt
 *
 * Permission is hereby  granted, free of charge, to any  person obtaining a copy
 * of this software and associated  documentation files (the "Software"), to deal
 * in the Software  without restriction, including without  limitation the rights
 * to  use, copy,  modify, merge,  publish, distribute,  sublicense, and/or  sell
 * copies  of  the Software,  and  to  permit persons  to  whom  the Software  is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE  IS PROVIDED "AS  IS", WITHOUT WARRANTY  OF ANY KIND,  EXPRESS OR
 * IMPLIED,  INCLUDING BUT  NOT  LIMITED TO  THE  WARRANTIES OF  MERCHANTABILITY,
 * FITNESS FOR  A PARTICULAR PURPOSE AND  NONINFRINGEMENT. IN NO EVENT  SHALL THE
 * AUTHORS  OR COPYRIGHT  HOLDERS  BE  LIABLE FOR  ANY  CLAIM,  DAMAGES OR  OTHER
 * LIABILITY, WHETHER IN AN ACTION OF  CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE  OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.nabiki.wukong.iop.frame;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

// This file is taken from wukong project. It may be different with the original one.
public class OP {
    /**
     * Get a deep copy of the specified object. The method first serializes the
     * object to a byte array and then recover a new object from it.
     *
     * <p>The object to be copied must implement {@link Serializable}, or the method
     * fails and returns {@code null}.
     * </p>
     *
     * @param copied the specified object to be deeply copied
     * @param <T> generic type of a copied object
     * @return deep copying object
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T copied) {
        try (ByteArrayOutputStream bo = new ByteArrayOutputStream()) {
            new ObjectOutputStream(bo).writeObject(copied);
            return (T) new ObjectInputStream(
                    new ByteArrayInputStream(bo.toByteArray())).readObject();
        } catch (IOException | ClassNotFoundException ignored) {
            return null;
        }
    }

    /**
     * Clock-wise duration between the two specified local time. The {@code end} is
     * always assumed to be after the {@code start} in clock time. If the {@code end}
     * is smaller than the {@code start} numerically, the time crosses midnight.
     *
     * @param start local time start
     * @param end local time end
     * @return duration between the specified two local times
     */
    public static Duration between(LocalTime start, LocalTime end) {
        Objects.requireNonNull(start, "local time start null");
        Objects.requireNonNull(end, "local time end null");
        if (start.isBefore(end))
            return Duration.between(start, end);
        else if (start.isAfter(end))
            return Duration.between(start, LocalTime.MIDNIGHT.minusNanos(1))
                    .plus(Duration.between(LocalTime.MIDNIGHT, end)).plusNanos(1);
        else
            return Duration.ZERO;
    }

    // Instrument product ID pattern.
    private static final Pattern productPattern = Pattern.compile("[a-zA-Z]+");

    // GSON.
    private final static Gson gson;
    static {
        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .serializeNulls()
                .setPrettyPrinting()
                .create();
    }

    // Day and time.
    private static final String dayPatternStr = "yyyyMMdd";
    private static final String timePatternStr = "HH:mm:ss";
    private static final DateTimeFormatter dayPattern = DateTimeFormatter
            .ofPattern(dayPatternStr);
    private static final DateTimeFormatter timePattern = DateTimeFormatter
            .ofPattern(timePatternStr);

    /**
     * Parse the specified JSON string to object of the specified {@link Class}.
     *
     * @param json JSON string
     * @param clz {@link Class} of the object
     * @param <T> generic type of the object
     * @return object parsed from the specified JSON string
     * @throws IOException fail parsing JSON string
     */
    public static <T> T fromJson(String json, Class<T> clz) throws IOException {
        try {
            return gson.fromJson(json, clz);
        } catch (com.google.gson.JsonSyntaxException e) {
            throw new IOException("parse JSON string", e);
        }
    }

    /**
     * Encode the specified object into JSON string.
     *
     * @param obj object
     * @return JSON string representing the specified object
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * Read from the specified file and parse and content into a string using the
     * specified charset.
     *
     * @param file file to read from
     * @param charset charset for the returned string
     * @return string parsed from the content of the specified file
     * @throws IOException fail to read the file
     */
    public static String readText(File file, Charset charset) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            return new String(is.readAllBytes(), charset);
        }
    }

    /**
     * Write the specified string to the specified file, encoded into binary data
     * with the specified charset.
     *
     * @param text the string to be written
     * @param file file to be written to
     * @param charset charset of the string to decode
     * @param append if {@code true}, the content is appended to the end of the file
     *               rather than the beginning
     * @throws IOException if operation failed or file not found
     */
    public static void writeText(String text, File file, Charset charset,
                                 boolean append) throws IOException {
        Objects.requireNonNull(text);
        try (OutputStream os = new FileOutputStream(file, append)) {
            os.write(text.getBytes(charset));
            os.flush();
        }
    }

    /**
     * Get today's string representation of the specified pattern. The pattern
     * follows the convention of {@link DateTimeFormatter}.
     *
     * @param pattern pattern
     * @return today's string representation
     */
    public static String getToday(String pattern) {
        if (pattern == null || pattern.trim().length() == 0)
            pattern = dayPatternStr;
        if (pattern.compareTo(dayPatternStr) == 0)
            return LocalDate.now().format(dayPattern);
        else
            return LocalDate.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Get now' time representation of the specified pattern. The pattern
     * follows the convention of {@link DateTimeFormatter}.
     *
     * @param pattern pattern
     * @return today's string representation
     */
    public static String getTime(String pattern) {
        if (pattern == null || pattern.trim().length() == 0)
            pattern = timePatternStr;
        if (pattern.compareTo(timePatternStr) == 0)
            return LocalTime.now().format(timePattern);
        else
            return LocalTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Parse the specified day from string to {@link LocalDate} with the specified
     * pattern in the convention of {@link DateTimeFormatter}.
     *
     * @param day string representation of a day
     * @param pattern pattern
     * @return {@link LocalDate}
     */
    public static LocalDate parseDay(String day, String pattern) {
        if (day == null || day.trim().length() == 0)
            return null;
        if (pattern == null || pattern.trim().length() == 0)
            pattern = dayPatternStr;
        if (pattern.compareTo(dayPatternStr) == 0)
            return LocalDate.parse(day, dayPattern);
        else
            return LocalDate.parse(day, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Parse the specified time from string to {@link LocalTime} with the specified
     * pattern in the convention of {@link DateTimeFormatter}.
     *
     * @param time string representation of time
     * @param pattern pattern
     * @return {@link LocalTime}
     */
    public static LocalTime parseTime(String time, String pattern) {
        if (time == null || time.trim().length() == 0)
            return null;
        if (pattern == null || pattern.trim().length() == 0)
            pattern = timePatternStr;
        if (pattern.compareTo(timePatternStr) == 0)
            return LocalTime.parse(time, timePattern);
        else
            return LocalTime.parse(time, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Get random string generated with {@link UUID}.
     *
     * @return random string
     */
    public static String randomString() {
        return UUID.randomUUID().toString();
    }
}
