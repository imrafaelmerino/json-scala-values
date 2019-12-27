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
                                    end
                                   );
    }

    private static BigDecimal parsePositiveDecimal(final byte[] buf,
                                                   final JsonReader reader,
                                                   final int start,
                                                   final int end
                                                  ) throws IOException
    {
        long value = 0;
        byte ch = ' ';
        int i = start;
        for (; i < end; i++)
        {
            ch = buf[i];
            if (ch == '.' || ch == 'e' || ch == 'E') break;
            final int ind = ch - 48;
            if (ind < 0 || ind > 9)
            {
                if (i > start && reader.allWhitespace(i,
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
        if (i == start) numberException(reader,
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