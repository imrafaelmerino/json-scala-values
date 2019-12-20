package com.dslplatform.json;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import static com.dslplatform.json.NumberConverter.numberException;

/**
 dsl-json number deserializers accept numbers wrapped in strings, which is not
 a desirable behaviour from my point of view. That's why this class was created,
 to overwrite that behaviour.
 */
public abstract class MyNumberConverter
{

    private final static int[] DIFF = {111, 222, 444, 888, 1776};
    private final static int[] ERROR = {50, 100, 200, 400, 800};
    private final static int[] SCALE_10 = {10000, 1000, 100, 10, 1};
    private final static double[] POW_10 = {
    1e1, 1e2, 1e3, 1e4, 1e5, 1e6, 1e7, 1e8, 1e9,
    1e10, 1e11, 1e12, 1e13, 1e14, 1e15, 1e16, 1e17, 1e18, 1e19,
    1e20, 1e21, 1e22, 1e23, 1e24, 1e25, 1e26, 1e27, 1e28, 1e29,
    1e30, 1e31, 1e32, 1e33, 1e34, 1e35, 1e36, 1e37, 1e38, 1e39,
    1e40, 1e41, 1e42, 1e43, 1e44, 1e45, 1e46, 1e47, 1e48, 1e49,
    1e50, 1e51, 1e52, 1e53, 1e54, 1e55, 1e56, 1e57, 1e58, 1e59,
    1e60, 1e61, 1e62, 1e63, 1e64, 1e65
    };

    private static BigDecimal parseNumberGeneric(final char[] buf,
                                                 final int len,
                                                 final JsonReader reader
                                                ) throws ParsingException
    {
        int end = len;
        while (end > 0 && Character.isWhitespace(buf[end - 1]))
        {
            end--;
        }
        if (end > reader.maxNumberDigits)
        {
            throw reader.newParseErrorWith("Too many digits detected in number",
                                           len,
                                           "",
                                           "Too many digits detected in number",
                                           end,
                                           ""
                                          );
        }
        try
        {
            return new BigDecimal(buf,
                                  0,
                                  end
            );
        }
        catch (NumberFormatException nfe)
        {
            throw reader.newParseErrorAt("Error parsing number",
                                         len,
                                         nfe
                                        );
        }
    }

    private static class NumberInfo
    {
        final char[] buffer;
        final int length;

        NumberInfo(final char[] buffer,
                   final int length
                  )
        {
            this.buffer = buffer;
            this.length = length;
        }
    }

    private static NumberInfo readLongNumber(final JsonReader reader,
                                             final int start
                                            ) throws IOException
    {
        int i = reader.length() - start;
        char[] tmp = reader.prepareBuffer(start,
                                          i
                                         );
        while (!reader.isEndOfStream())
        {
            while (i < tmp.length)
            {
                final char ch = (char) reader.read();
                tmp[i++] = ch;
                if (reader.isEndOfStream() || !(ch >= '0' && ch <= '9' || ch == '-' || ch == '+' || ch == '.' || ch == 'e' || ch == 'E'))
                {
                    return new NumberInfo(tmp,
                                          i
                    );
                }
            }
            final int newSize = tmp.length * 2;
            if (newSize > reader.maxNumberDigits)
            {
                throw reader.newParseErrorFormat("Too many digits detected in number",
                                                 tmp.length,
                                                 "Number of digits larger than %d. Unable to read number",
                                                 reader.maxNumberDigits
                                                );
            }
            tmp = Arrays.copyOf(tmp,
                                newSize
                               );
        }
        return new NumberInfo(tmp,
                              i
        );
    }

    public static double deserializeDouble(final JsonReader reader) throws IOException
    {
        final int start = reader.scanNumber();
        final int end = reader.getCurrentIndex();
        final byte[] buf = reader.buffer;
        final byte ch = buf[start];
        if (ch == '-')
        {
            return -parseDouble(buf,
                                reader,
                                start,
                                end,
                                1
                               );
        }
        return parseDouble(buf,
                           reader,
                           start,
                           end,
                           0
                          );
    }

    private static double parseDouble(final byte[] buf,
                                      final JsonReader reader,
                                      final int start,
                                      final int end,
                                      final int offset
                                     ) throws IOException
    {
        if (end - start - offset > reader.doubleLengthLimit)
        {
            if (end == reader.length())
            {
                final NumberInfo tmp = readLongNumber(reader,
                                                      start + offset
                                                     );
                return parseDoubleGeneric(tmp.buffer,
                                          tmp.length,
                                          reader
                                         );
            }
            return parseDoubleGeneric(reader.prepareBuffer(start + offset,
                                                           end - start - offset
                                                          ),
                                      end - start - offset,
                                      reader
                                     );
        }
        long value = 0;
        byte ch = ' ';
        int i = start + offset;
        for (; i < end; i++)
        {
            ch = buf[i];
            if (ch == '.' || ch == 'e' || ch == 'E') break;
            final int ind = buf[i] - 48;
            if (ind < 0 || ind > 9)
            {
                if (i > start + offset && reader.allWhitespace(i,
                                                               end
                                                              )) return value;
                numberException(reader,
                                start,
                                end,
                                "Unknown digit",
                                (char) ch
                               );
            }
            value = (value << 3) + (value << 1) + ind;
        }
        if (i == start + offset) numberException(reader,
                                                 start,
                                                 end,
                                                 "Digit not found"
                                                );
        else if (i == end) return value;
        else if (ch == '.')
        {
            i++;
            if (i == end) numberException(reader,
                                          start,
                                          end,
                                          "Number ends with a dot"
                                         );
            final int maxLen;
            final double preciseDividor;
            final int expDiff;
            final int decPos = i;
            final int decOffset;
            if (value == 0)
            {
                maxLen = i + 15;
                ch = buf[i];
                if (ch == '0' && end > maxLen)
                {
                    return parseDoubleGeneric(reader.prepareBuffer(start + offset,
                                                                   end - start - offset
                                                                  ),
                                              end - start - offset,
                                              reader
                                             );
                } else if (ch < '8')
                {
                    preciseDividor = 1e14;
                    expDiff = -1;
                    decOffset = 1;
                } else
                {
                    preciseDividor = 1e15;
                    expDiff = 0;
                    decOffset = 0;
                }
            } else
            {
                maxLen = start + offset + 16;
                if (buf[start + offset] < '8')
                {
                    preciseDividor = 1e14;
                    expDiff = i - maxLen + 14;
                    decOffset = 1;
                } else
                {
                    preciseDividor = 1e15;
                    expDiff = i - maxLen + 15;
                    decOffset = 0;
                }
            }
            final int numLimit = maxLen < end ? maxLen : end;
            //TODO zeros
            for (; i < numLimit; i++)
            {
                ch = buf[i];
                if (ch == 'e' || ch == 'E') break;
                final int ind = ch - 48;
                if (ind < 0 || ind > 9)
                {
                    if (reader.allWhitespace(i,
                                             end
                                            )) return value / POW_10[i - decPos - 1];
                    numberException(reader,
                                    start,
                                    end,
                                    "Unknown digit",
                                    (char) buf[i]
                                   );
                }
                value = (value << 3) + (value << 1) + ind;
            }
            if (i == end) return value / POW_10[i - decPos - 1];
            else if (ch == 'e' || ch == 'E')
            {
                return doubleExponent(reader,
                                      value,
                                      i - decPos,
                                      0,
                                      buf,
                                      start,
                                      end,
                                      offset,
                                      i
                                     );
            }
            if (reader.doublePrecision == JsonReader.DoublePrecision.HIGH)
            {
                return parseDoubleGeneric(reader.prepareBuffer(start + offset,
                                                               end - start - offset
                                                              ),
                                          end - start - offset,
                                          reader
                                         );
            }
            int decimals = 0;
            final int decLimit = start + offset + 18 < end ? start + offset + 18 : end;
            final int remPos = i;
            for (; i < decLimit; i++)
            {
                ch = buf[i];
                if (ch == 'e' || ch == 'E') break;
                final int ind = ch - 48;
                if (ind < 0 || ind > 9)
                {
                    if (reader.allWhitespace(i,
                                             end
                                            ))
                    {
                        return approximateDouble(decimals,
                                                 value / preciseDividor,
                                                 i - remPos - decOffset
                                                );
                    }
                    numberException(reader,
                                    start,
                                    end,
                                    "Unknown digit",
                                    (char) buf[i]
                                   );
                }
                decimals = (decimals << 3) + (decimals << 1) + ind;
            }
            final double number = approximateDouble(decimals,
                                                    value / preciseDividor,
                                                    i - remPos - decOffset
                                                   );
            while (i < end && ch >= '0' && ch <= '9')
            {
                ch = buf[i++];
            }
            if (ch == 'e' || ch == 'E')
            {
                return doubleExponent(reader,
                                      0,
                                      expDiff,
                                      number,
                                      buf,
                                      start,
                                      end,
                                      offset,
                                      i
                                     );
            } else if (expDiff > 0)
            {
                return number * POW_10[expDiff - 1];
            } else if (expDiff < 0)
            {
                return number / POW_10[-expDiff - 1];
            } else
            {
                return number;
            }
        } else if (ch == 'e' || ch == 'E')
        {
            return doubleExponent(reader,
                                  value,
                                  0,
                                  0,
                                  buf,
                                  start,
                                  end,
                                  offset,
                                  i
                                 );
        }
        return value;
    }

    private static double approximateDouble(final int decimals,
                                            final double precise,
                                            final int digits
                                           )
    {
        final long bits = Double.doubleToRawLongBits(precise);
        final int exp = (int) (bits >> 52) - 1022;
        final int missing = (decimals * SCALE_10[digits + 1] + ERROR[exp]) / DIFF[exp];
        return Double.longBitsToDouble(bits + missing);
    }

    private static double doubleExponent(JsonReader reader,
                                         final long whole,
                                         final int decimals,
                                         double fraction,
                                         byte[] buf,
                                         int start,
                                         int end,
                                         int offset,
                                         int i
                                        ) throws IOException
    {
        if (reader.doublePrecision == JsonReader.DoublePrecision.EXACT)
        {
            return parseDoubleGeneric(reader.prepareBuffer(start + offset,
                                                           end - start - offset
                                                          ),
                                      end - start - offset,
                                      reader
                                     );
        }
        byte ch;
        ch = buf[++i];
        final int exp;
        if (ch == '-')
        {
            exp = parseNegativeInt(buf,
                                   reader,
                                   i,
                                   end
                                  ) - decimals;
        } else if (ch == '+')
        {
            exp = parsePositiveInt(buf,
                                   reader,
                                   i,
                                   end,
                                   1
                                  ) - decimals;
        } else
        {
            exp = parsePositiveInt(buf,
                                   reader,
                                   i,
                                   end,
                                   0
                                  ) - decimals;
        }
        if (fraction == 0)
        {
            if (exp == 0 || whole == 0) return whole;
            else if (exp > 0 && exp < POW_10.length) return whole * POW_10[exp - 1];
            else if (exp < 0 && -exp < POW_10.length) return whole / POW_10[-exp - 1];
            else if (reader.doublePrecision != JsonReader.DoublePrecision.HIGH)
            {
                if (exp > 0 && exp < 300) return whole * Math.pow(10,
                                                                  exp
                                                                 );
                else if (exp > -300 && exp < 0) return whole / Math.pow(10,
                                                                        exp
                                                                       );
            }
        } else
        {
            if (exp == 0) return whole + fraction;
            else if (exp > 0 && exp < POW_10.length) return fraction * POW_10[exp - 1] + whole * POW_10[exp - 1];
            else if (exp < 0 && -exp < POW_10.length) return fraction / POW_10[-exp - 1] + whole / POW_10[-exp - 1];
            else if (reader.doublePrecision != JsonReader.DoublePrecision.HIGH)
            {
                if (exp > 0 && exp < 300) return whole * Math.pow(10,
                                                                  exp
                                                                 );
                else if (exp > -300 && exp < 0) return whole / Math.pow(10,
                                                                        exp
                                                                       );
            }
        }
        return parseDoubleGeneric(reader.prepareBuffer(start + offset,
                                                       end - start - offset
                                                      ),
                                  end - start - offset,
                                  reader
                                 );
    }

    private static double parseDoubleGeneric(final char[] buf,
                                             final int len,
                                             final JsonReader reader
                                            ) throws IOException
    {
        int end = len;
        while (end > 0 && Character.isWhitespace(buf[end - 1]))
        {
            end--;
        }
        if (end > reader.maxNumberDigits)
        {
            throw reader.newParseErrorWith("Too many digits detected in number",
                                           len,
                                           "",
                                           "Too many digits detected in number",
                                           end,
                                           ""
                                          );
        }
        try
        {
            return Double.parseDouble(new String(buf,
                                                 0,
                                                 end
            ));
        }
        catch (NumberFormatException nfe)
        {
            throw reader.newParseErrorAt("Error parsing number",
                                         len,
                                         nfe
                                        );
        }
    }


    public static int deserializeInt(final JsonReader reader) throws IOException
    {
        final int start = reader.scanNumber();
        final int end = reader.getCurrentIndex();
        final byte[] buf = reader.buffer;
        final byte ch = buf[start];
        if (ch == '-')
        {
            return parseNegativeInt(buf,
                                    reader,
                                    start,
                                    end
                                   );
        }
        return parsePositiveInt(buf,
                                reader,
                                start,
                                end,
                                0
                               );
    }

    private static int parsePositiveInt(final byte[] buf,
                                        final JsonReader reader,
                                        final int start,
                                        final int end,
                                        final int offset
                                       ) throws IOException
    {
        int value = 0;
        int i = start + offset;
        if (i == end) numberException(reader,
                                      start,
                                      end,
                                      "Digit not found"
                                     );
        for (; i < end; i++)
        {
            final int ind = buf[i] - 48;
            if (ind < 0 || ind > 9)
            {
                if (i > start + offset && reader.allWhitespace(i,
                                                               end
                                                              )) return value;
                else if (i == end - 1 && buf[i] == '.') numberException(reader,
                                                                        start,
                                                                        end,
                                                                        "Number ends with a dot"
                                                                       );
                final BigDecimal v = parseNumberGeneric(reader.prepareBuffer(start,
                                                                             end - start
                                                                            ),
                                                        end - start,
                                                        reader
                                                       );
                if (v.scale() > 0) numberException(reader,
                                                   start,
                                                   end,
                                                   "Expecting int but found decimal value",
                                                   v
                                                  );
                return v.intValue();

            }
            value = (value << 3) + (value << 1) + ind;
            if (value < 0)
            {
                numberException(reader,
                                start,
                                end,
                                "Integer overflow detected"
                               );
            }
        }
        return value;
    }

    private static int parseNegativeInt(final byte[] buf,
                                        final JsonReader reader,
                                        final int start,
                                        final int end
                                       ) throws IOException
    {
        int value = 0;
        int i = start + 1;
        if (i == end) numberException(reader,
                                      start,
                                      end,
                                      "Digit not found"
                                     );
        for (; i < end; i++)
        {
            final int ind = buf[i] - 48;
            if (ind < 0 || ind > 9)
            {
                if (i > start + 1 && reader.allWhitespace(i,
                                                          end
                                                         )) return value;
                else if (i == end - 1 && buf[i] == '.') numberException(reader,
                                                                        start,
                                                                        end,
                                                                        "Number ends with a dot"
                                                                       );
                final BigDecimal v = parseNumberGeneric(reader.prepareBuffer(start,
                                                                             end - start
                                                                            ),
                                                        end - start,
                                                        reader
                                                       );
                if (v.scale() > 0) numberException(reader,
                                                   start,
                                                   end,
                                                   "Expecting int but found decimal value",
                                                   v
                                                  );
                return v.intValue();
            }
            value = (value << 3) + (value << 1) - ind;
            if (value > 0)
            {
                numberException(reader,
                                start,
                                end,
                                "Integer overflow detected"
                               );
            }
        }
        return value;
    }


    public static long deserializeLong(final JsonReader reader) throws IOException
    {
        final int start = reader.scanNumber();
        final int end = reader.getCurrentIndex();
        final byte[] buf = reader.buffer;
        final byte ch = buf[start];
        int i = start;
        long value = 0;
        if (ch == '-')
        {
            i = start + 1;
            if (i == end) numberException(reader,
                                          start,
                                          end,
                                          "Digit not found"
                                         );
            for (; i < end; i++)
            {
                final int ind = buf[i] - 48;
                if (ind < 0 || ind > 9)
                {
                    if (i > start + 1 && reader.allWhitespace(i,
                                                              end
                                                             )) return value;
                    return parseLongGeneric(reader,
                                            start,
                                            end
                                           );
                }
                value = (value << 3) + (value << 1) - ind;
                if (value > 0)
                {
                    numberException(reader,
                                    start,
                                    end,
                                    "Long overflow detected"
                                   );
                }
            }
            return value;
        }
        if (i == end) numberException(reader,
                                      start,
                                      end,
                                      "Digit not found"
                                     );
        for (; i < end; i++)
        {
            final int ind = buf[i] - 48;
            if (ind < 0 || ind > 9)
            {
                if (ch == '+' && i > start + 1 && reader.allWhitespace(i,
                                                                       end
                                                                      )) return value;
                else if (ch != '+' && i > start && reader.allWhitespace(i,
                                                                        end
                                                                       )) return value;
                return parseLongGeneric(reader,
                                        start,
                                        end
                                       );
            }
            value = (value << 3) + (value << 1) + ind;
            if (value < 0)
            {
                numberException(reader,
                                start,
                                end,
                                "Long overflow detected"
                               );
            }
        }
        return value;
    }

    private static long parseLongGeneric(final JsonReader reader,
                                         final int start,
                                         final int end
                                        ) throws IOException
    {
        final int len = end - start;
        final char[] buf = reader.prepareBuffer(start,
                                                len
                                               );
        if (len > 0 && buf[len - 1] == '.') numberException(reader,
                                                            start,
                                                            end,
                                                            "Number ends with a dot"
                                                           );
        final BigDecimal v = parseNumberGeneric(buf,
                                                len,
                                                reader
                                               );
        if (v.scale() > 0) numberException(reader,
                                           start,
                                           end,
                                           "Expecting long, but found decimal value ",
                                           v
                                          );
        return v.longValue();
    }

    public static BigDecimal deserializeDecimal(final JsonReader reader) throws IOException
    {
        final int start = reader.scanNumber();
        int end = reader.getCurrentIndex();
        int len = end - start;
        if (len > 18)
        {
            end = reader.findNonWhitespace(end);
            len = end - start;
            if (end == reader.length())
            {
                final NumberInfo info = readLongNumber(reader,
                                                       start
                                                      );
                return parseNumberGeneric(info.buffer,
                                          info.length,
                                          reader
                                         );
            } else if (len > 18)
            {
                return parseNumberGeneric(reader.prepareBuffer(start,
                                                               len
                                                              ),
                                          len,
                                          reader
                                         );
            }
        }
        final byte[] buf = reader.buffer;
        final byte ch = buf[start];
        if (ch == '-')
        {
            return parseNegativeDecimal(buf,
                                        reader,
                                        start,
                                        end
                                       );
        }
        return parsePositiveDecimal(buf,
                                    reader,
                                    start,
                                    end,
                                    0
                                   );
    }

    private static BigDecimal parsePositiveDecimal(final byte[] buf,
                                                   final JsonReader reader,
                                                   final int start,
                                                   final int end,
                                                   final int offset
                                                  ) throws IOException
    {
        long value = 0;
        byte ch = ' ';
        int i = start + offset;
        for (; i < end; i++)
        {
            ch = buf[i];
            if (ch == '.' || ch == 'e' || ch == 'E') break;
            final int ind = ch - 48;
            if (ind < 0 || ind > 9)
            {
                if (i > start + offset && reader.allWhitespace(i,
                                                               end
                                                              )) return BigDecimal.valueOf(value);
                numberException(reader,
                                start,
                                end,
                                "Unknown digit",
                                (char) ch
                               );
            }
            value = (value << 3) + (value << 1) + ind;
        }
        if (i == start + offset) numberException(reader,
                                                 start,
                                                 end,
                                                 "Digit not found"
                                                );
        else if (i == end) return BigDecimal.valueOf(value);
        else if (ch == '.')
        {
            i++;
            if (i == end) numberException(reader,
                                          start,
                                          end,
                                          "Number ends with a dot"
                                         );
            int dp = i;
            for (; i < end; i++)
            {
                ch = buf[i];
                if (ch == 'e' || ch == 'E') break;
                final int ind = ch - 48;
                if (ind < 0 || ind > 9)
                {
                    if (reader.allWhitespace(i,
                                             end
                                            )) return BigDecimal.valueOf(value,
                                                                         i - dp
                                                                        );
                    numberException(reader,
                                    start,
                                    end,
                                    "Unknown digit",
                                    (char) ch
                                   );
                }
                value = (value << 3) + (value << 1) + ind;
            }
            if (i == end) return BigDecimal.valueOf(value,
                                                    end - dp
                                                   );
            else if (ch == 'e' || ch == 'E')
            {
                final int ep = i;
                i++;
                ch = buf[i];
                final int exp;
                if (ch == '-')
                {
                    exp = parseNegativeInt(buf,
                                           reader,
                                           i,
                                           end
                                          );
                } else if (ch == '+')
                {
                    exp = parsePositiveInt(buf,
                                           reader,
                                           i,
                                           end,
                                           1
                                          );
                } else
                {
                    exp = parsePositiveInt(buf,
                                           reader,
                                           i,
                                           end,
                                           0
                                          );
                }
                return BigDecimal.valueOf(value,
                                          ep - dp - exp
                                         );
            }
            return BigDecimal.valueOf(value,
                                      end - dp
                                     );
        } else if (ch == 'e' || ch == 'E')
        {
            i++;
            ch = buf[i];
            final int exp;
            if (ch == '-')
            {
                exp = parseNegativeInt(buf,
                                       reader,
                                       i,
                                       end
                                      );
            } else if (ch == '+')
            {
                exp = parsePositiveInt(buf,
                                       reader,
                                       i,
                                       end,
                                       1
                                      );
            } else
            {
                exp = parsePositiveInt(buf,
                                       reader,
                                       i,
                                       end,
                                       0
                                      );
            }
            return BigDecimal.valueOf(value,
                                      -exp
                                     );
        }
        return BigDecimal.valueOf(value);
    }

    private static BigDecimal parseNegativeDecimal(final byte[] buf,
                                                   final JsonReader reader,
                                                   final int start,
                                                   final int end
                                                  ) throws IOException
    {
        long value = 0;
        byte ch = ' ';
        int i = start + 1;
        for (; i < end; i++)
        {
            ch = buf[i];
            if (ch == '.' || ch == 'e' || ch == 'E') break;
            final int ind = ch - 48;
            if (ind < 0 || ind > 9)
            {
                if (i > start + 1 && reader.allWhitespace(i,
                                                          end
                                                         )) return BigDecimal.valueOf(value);
                numberException(reader,
                                start,
                                end,
                                "Unknown digit",
                                (char) ch
                               );
            }
            value = (value << 3) + (value << 1) - ind;
        }
        if (i == start + 1) numberException(reader,
                                            start,
                                            end,
                                            "Digit not found"
                                           );
        else if (i == end) return BigDecimal.valueOf(value);
        else if (ch == '.')
        {
            i++;
            if (i == end) numberException(reader,
                                          start,
                                          end,
                                          "Number ends with a dot"
                                         );
            int dp = i;
            for (; i < end; i++)
            {
                ch = buf[i];
                if (ch == 'e' || ch == 'E') break;
                final int ind = ch - 48;
                if (ind < 0 || ind > 9)
                {
                    if (reader.allWhitespace(i,
                                             end
                                            )) return BigDecimal.valueOf(value,
                                                                         i - dp
                                                                        );
                    numberException(reader,
                                    start,
                                    end,
                                    "Unknown digit",
                                    (char) ch
                                   );
                }
                value = (value << 3) + (value << 1) - ind;
            }
            if (i == end) return BigDecimal.valueOf(value,
                                                    end - dp
                                                   );
            else if (ch == 'e' || ch == 'E')
            {
                final int ep = i;
                i++;
                ch = buf[i];
                final int exp;
                if (ch == '-')
                {
                    exp = parseNegativeInt(buf,
                                           reader,
                                           i,
                                           end
                                          );
                } else if (ch == '+')
                {
                    exp = parsePositiveInt(buf,
                                           reader,
                                           i,
                                           end,
                                           1
                                          );
                } else
                {
                    exp = parsePositiveInt(buf,
                                           reader,
                                           i,
                                           end,
                                           0
                                          );
                }
                return BigDecimal.valueOf(value,
                                          ep - dp - exp
                                         );
            }
            return BigDecimal.valueOf(value,
                                      end - dp
                                     );
        } else if (ch == 'e' || ch == 'E')
        {
            i++;
            ch = buf[i];
            final int exp;
            if (ch == '-')
            {
                exp = parseNegativeInt(buf,
                                       reader,
                                       i,
                                       end
                                      );
            } else if (ch == '+')
            {
                exp = parsePositiveInt(buf,
                                       reader,
                                       i,
                                       end,
                                       1
                                      );
            } else
            {
                exp = parsePositiveInt(buf,
                                       reader,
                                       i,
                                       end,
                                       0
                                      );
            }
            return BigDecimal.valueOf(value,
                                      -exp
                                     );
        }
        return BigDecimal.valueOf(value);
    }



}