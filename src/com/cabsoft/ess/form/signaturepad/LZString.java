package com.cabsoft.ess.form.signaturepad;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.cabsoft.utils.Base64Util;

public class LZString {


  static String keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";


  public static String compress(String uncompressed) {

    if (uncompressed == null)
      return "";
    int value;
    HashMap<String, Integer> context_dictionary = new HashMap<String, Integer>();
    HashSet<String> context_dictionaryToCreate = new HashSet<String>();
    String context_c = "";
    String context_wc = "";
    String context_w = "";
    double context_enlargeIn = 2d; // Compensate for the first entry which
    // should not count
    int context_dictSize = 3;
    int context_numBits = 2;
    String context_data_string = "";
    int context_data_val = 0;
    int context_data_position = 0;

    for (int ii = 0; ii < uncompressed.length(); ii += 1) {
      context_c = "" + (uncompressed.charAt(ii));
      if (!context_dictionary.containsKey(context_c)) {
        context_dictionary.put(context_c, context_dictSize++);
        context_dictionaryToCreate.add(context_c);
      }

      context_wc = context_w + context_c;

      if (context_dictionary.containsKey(context_wc)) {
        context_w = context_wc;
      } else {
        if (context_dictionaryToCreate.contains(context_w)) {

          if (((int)context_w.charAt(0)) < 256) {
            for (int i = 0; i < context_numBits; i++) {
              context_data_val = (context_data_val << 1);
              if (context_data_position == 15) {
                context_data_position = 0;
                context_data_string += (char) context_data_val;
                context_data_val = 0;
              } else {
                context_data_position++;
              }
            }
            value = (int) context_w.charAt(0);
            for (int i = 0; i < 8; i++) {
              context_data_val = (context_data_val << 1)
                  | (value & 1);
              if (context_data_position == 15) {
                context_data_position = 0;
                context_data_string += (char) context_data_val;
                context_data_val = 0;
              } else {
                context_data_position++;
              }
              value = value >> 1;
            }
          } else {
            value = 1;
            for (int i = 0; i < context_numBits; i++) {
              context_data_val = (context_data_val << 1) | value;
              if (context_data_position == 15) {
                context_data_position = 0;
                context_data_string += (char) context_data_val;
                context_data_val = 0;
              } else {
                context_data_position++;
              }
              value = 0;
            }
            value = (int) context_w.charAt(0);
            for (int i = 0; i < 16; i++) {
              context_data_val = (context_data_val << 1)
                  | (value & 1);
              if (context_data_position == 15) {
                context_data_position = 0;
                context_data_string += (char) context_data_val;
                context_data_val = 0;
              } else {
                context_data_position++;
              }
              value = value >> 1;
            }
          }
          context_enlargeIn--;
          if (Double.valueOf(context_enlargeIn).intValue() == 0) {
            context_enlargeIn = Math.pow(2, context_numBits);
            context_numBits++;
          }
          context_dictionaryToCreate.remove(context_w);
        } else {
          value = context_dictionary.get(context_w);
          for (int i = 0; i < context_numBits; i++) {
            context_data_val = (context_data_val << 1)
                | (value & 1);
            if (context_data_position == 15) {
              context_data_position = 0;
              context_data_string += (char) context_data_val;
              context_data_val = 0;
            } else {
              context_data_position++;
            }
            value = value >> 1;
          }

        }
        context_enlargeIn--;
        if (Double.valueOf(context_enlargeIn).intValue() == 0) {
          context_enlargeIn = Math.pow(2, context_numBits);
          context_numBits++;
        }
        // Add wc to the dictionary.
        context_dictionary.put(context_wc, context_dictSize++);
        context_w = new String(context_c);
      }
    }

    // Output the code for w.
    if (!"".equals(context_w)) {
      if (context_dictionaryToCreate.contains(context_w)) {
        if (((int)context_w.charAt(0)) < 256) {
          for (int i = 0; i < context_numBits; i++) {
            context_data_val = (context_data_val << 1);
            if (context_data_position == 15) {
              context_data_position = 0;
              context_data_string += (char) context_data_val;
              context_data_val = 0;
            } else {
              context_data_position++;
            }
          }
          value = (int) context_w.charAt(0);
          for (int i = 0; i < 8; i++) {
            context_data_val = (context_data_val << 1)
                | (value & 1);
            if (context_data_position == 15) {
              context_data_position = 0;
              context_data_string += (char) context_data_val;
              context_data_val = 0;
            } else {
              context_data_position++;
            }
            value = value >> 1;
          }
        } else {
          value = 1;
          for (int i = 0; i < context_numBits; i++) {
            context_data_val = (context_data_val << 1) | value;
            if (context_data_position == 15) {
              context_data_position = 0;
              context_data_string += (char) context_data_val;
              context_data_val = 0;
            } else {
              context_data_position++;
            }
            value = 0;
          }
          value = (int) context_w.charAt(0);
          for (int i = 0; i < 16; i++) {
            context_data_val = (context_data_val << 1)
                | (value & 1);
            if (context_data_position == 15) {
              context_data_position = 0;
              context_data_string += (char) context_data_val;
              context_data_val = 0;
            } else {
              context_data_position++;
            }
            value = value >> 1;
          }
        }
        context_enlargeIn--;
        if (Double.valueOf(context_enlargeIn).intValue() == 0) {
          context_enlargeIn = Math.pow(2, context_numBits);
          context_numBits++;
        }
        context_dictionaryToCreate.remove(context_w);
      } else {
        value = context_dictionary.get(context_w);
        for (int i = 0; i < context_numBits; i++) {
          context_data_val = (context_data_val << 1) | (value & 1);
          if (context_data_position == 15) {
            context_data_position = 0;
            context_data_string += (char) context_data_val;
            context_data_val = 0;
          } else {
            context_data_position++;
          }
          value = value >> 1;
        }

      }
      context_enlargeIn--;
      if (Double.valueOf(context_enlargeIn).intValue() == 0) {
        context_enlargeIn = Math.pow(2, context_numBits);
        context_numBits++;
      }
    }

    // Mark the end of the stream
    value = 2;
    for (int i = 0; i < context_numBits; i++) {
      context_data_val = (context_data_val << 1) | (value & 1);
      if (context_data_position == 15) {
        context_data_position = 0;
        context_data_string += (char) context_data_val;
        context_data_val = 0;
      } else {
        context_data_position++;
      }
      value = value >> 1;
    }

    // Flush the last char
    while (true) {
      context_data_val = (context_data_val << 1);
      if (context_data_position == 15) {
        context_data_string += (char) context_data_val;
        break;
      } else
        context_data_position++;
    }
    return context_data_string;
  }

  public static String decompressHexString(String hexString) {

    if(hexString==null) {
      return "";
    }

    if(hexString.length()%2 !=0) {
      throw new RuntimeException("Input string length should be divisible by two");
    }

    int []intArr = new int[hexString.length()/2];


    for(int i = 0, k=0;i<hexString.length();i+=2, k++){
      intArr[k] = Integer.parseInt("" + hexString.charAt(i) + hexString.charAt(i+1), 16);
    }

    StringBuilder sb = new StringBuilder();
    for (int j = 0 ; j < intArr.length ; j+=2) {
      sb.append(Character.toChars(intArr[j] | intArr[j+1] << 8));
    }

    return decompress(sb.toString()) ;
  }


  public static String decompress(String compressed) {

    if (compressed == null)
      return "";
    if (compressed == "")
      return null;
    List<String> dictionary = new ArrayList<String>(200);
    double enlargeIn = 4;
    int dictSize = 4;
    int numBits = 3;
    String entry = "";
    StringBuilder result;
    String w;
    int bits;
    int resb;
    double maxpower;
    int power;
    String c = "";
    int d;
    Data data = Data.getInstance();
    data.string = compressed;
    data.val = (int) compressed.charAt(0);
    data.position = 32768;
    data.index = 1;

    for (int i = 0; i < 3; i += 1) {
      dictionary.add(i, "");
    }

    bits = 0;
    maxpower = Math.pow(2, 2);
    power = 1;
    while (power != Double.valueOf(maxpower).intValue()) {
      resb = data.val & data.position;
      data.position >>= 1;
      if (data.position == 0) {
        data.position = 32768;
        data.val = (int) data.string.charAt(data.index++);
      }
      bits |= (resb > 0 ? 1 : 0) * power;
      power <<= 1;
    }

    switch (bits) {
      case 0:
        bits = 0;
        maxpower = Math.pow(2, 8);
        power = 1;
        while (power != Double.valueOf(maxpower).intValue()) {
          resb = data.val & data.position;
          data.position >>= 1;
          if (data.position == 0) {
            data.position = 32768;
            data.val = (int) data.string.charAt(data.index++);
          }
          bits |= (resb > 0 ? 1 : 0) * power;
          power <<= 1;
        }
        c += (char) bits;
        break;
      case 1:
        bits = 0;
        maxpower = Math.pow(2, 16);
        power = 1;
        while (power != Double.valueOf(maxpower).intValue()) {
          resb = data.val & data.position;
          data.position >>= 1;
          if (data.position == 0) {
            data.position = 32768;
            data.val = (int) data.string.charAt(data.index++);
          }
          bits |= (resb > 0 ? 1 : 0) * power;
          power <<= 1;
        }
        c += (char) bits;
        break;
      case 2:
        return "";

    }

    dictionary.add(3, c);
    w = c;
    result = new StringBuilder(200);
    result.append(c);

   // w = result = c;

    while (true) {
      if (data.index > data.string.length()) {
        return "";
      }

      bits = 0;
      maxpower = Math.pow(2, numBits);
      power = 1;
      while (power != Double.valueOf(maxpower).intValue()) {
        resb = data.val & data.position;
        data.position >>= 1;
        if (data.position == 0) {
          data.position = 32768;
          data.val = (int) data.string.charAt(data.index++);
        }
        bits |= (resb > 0 ? 1 : 0) * power;
        power <<= 1;
      }

      switch (d = bits) {
        case 0:
          bits = 0;
          maxpower = Math.pow(2, 8);
          power = 1;
          while (power != Double.valueOf(maxpower).intValue()) {
            resb = data.val & data.position;
            data.position >>= 1;
            if (data.position == 0) {
              data.position = 32768;
              data.val = (int) data.string.charAt(data.index++);
            }
            bits |= (resb > 0 ? 1 : 0) * power;
            power <<= 1;
          }

          String temp = "";
          temp += (char) bits;
          dictionary.add(dictSize++, temp);

          d = dictSize - 1;

          enlargeIn--;

          break;
        case 1:
          bits = 0;
          maxpower = Math.pow(2, 16);
          power = 1;
          while (power != Double.valueOf(maxpower).intValue()) {
            resb = data.val & data.position;
            data.position >>= 1;
            if (data.position == 0) {
              data.position = 32768;
              data.val = (int) data.string.charAt(data.index++);
            }
            bits |= (resb > 0 ? 1 : 0) * power;
            power <<= 1;
          }

          temp = "";
          temp += (char) bits;

          dictionary.add(dictSize++, temp);

          d = dictSize - 1;

          enlargeIn--;

          break;
        case 2:
          return result.toString();
      }

      if (Double.valueOf(enlargeIn).intValue() == 0) {
        enlargeIn = Math.pow(2, numBits);
        numBits++;
      }

      if (d < dictionary.size() && dictionary.get(d) != null) {
        entry = dictionary.get(d);
      } else {
        if (d == dictSize) {
          entry = w + w.charAt(0);
        } else {
          return null;
        }
      }

      result.append(entry);

      // Add w+entry[0] to the dictionary.
      dictionary.add(dictSize++, w + entry.charAt(0));
      enlargeIn--;

      w = entry;

      if (Double.valueOf(enlargeIn).intValue() == 0) {
        enlargeIn = Math.pow(2, numBits);
        numBits++;
      }

    }
  }

  public static String compressToUTF16(String input) {
    if (input == null)
      return "";
    String output = "";
    int c;
    int current = 0;
    int status = 0;

    input = LZString.compress(input);

    for (int i = 0; i < input.length(); i++) {
      c = (int) input.charAt(i);
      switch (status++) {
        case 0:
          output += (char) ((c >> 1) + 32);
          current = (c & 1) << 14;
          break;
        case 1:
          output += (char) ((current + (c >> 2)) + 32);
          current = (c & 3) << 13;
          break;
        case 2:
          output += (char) ((current + (c >> 3)) + 32);
          current = (c & 7) << 12;
          break;
        case 3:
          output += (char) ((current + (c >> 4)) + 32);
          current = (c & 15) << 11;
          break;
        case 4:
          output += (char) ((current + (c >> 5)) + 32);
          current = (c & 31) << 10;
          break;
        case 5:
          output += (char) ((current + (c >> 6)) + 32);
          current = (c & 63) << 9;
          break;
        case 6:
          output += (char) ((current + (c >> 7)) + 32);
          current = (c & 127) << 8;
          break;
        case 7:
          output += (char) ((current + (c >> 8)) + 32);
          current = (c & 255) << 7;
          break;
        case 8:
          output += (char) ((current + (c >> 9)) + 32);
          current = (c & 511) << 6;
          break;
        case 9:
          output += (char) ((current + (c >> 10)) + 32);
          current = (c & 1023) << 5;
          break;
        case 10:
          output += (char) ((current + (c >> 11)) + 32);
          current = (c & 2047) << 4;
          break;
        case 11:
          output += (char) ((current + (c >> 12)) + 32);
          current = (c & 4095) << 3;
          break;
        case 12:
          output += (char) ((current + (c >> 13)) + 32);
          current = (c & 8191) << 2;
          break;
        case 13:
          output += (char) ((current + (c >> 14)) + 32);
          current = (c & 16383) << 1;
          break;
        case 14:
          output += (char) ((current + (c >> 15)) + 32);
          output += (char) ((c & 32767) + 32);

          status = 0;
          break;
      }
    }

    output += (char) (current + 32);

    return output;
  }

  public static String decompressFromUTF16(String input) {
    if (input == null)
      return "";
    StringBuilder output = new StringBuilder(200);
    int current = 0, c, status = 0, i = 0;

    while (i < input.length()) {
      c = (((int)input.charAt(i)) - 32);

      switch (status++) {
        case 0:
          current = c << 1;
          break;
        case 1:
          output.append((char) (current | (c >> 14)));
          current = (c & 16383) << 2;
          break;
        case 2:
          output.append((char) (current | (c >> 13)));
          current = (c & 8191) << 3;
          break;
        case 3:
          output.append((char) (current | (c >> 12)));
          current = (c & 4095) << 4;
          break;
        case 4:
          output.append((char) (current | (c >> 11)));
          current = (c & 2047) << 5;
          break;
        case 5:
          output.append((char) (current | (c >> 10)));
          current = (c & 1023) << 6;
          break;
        case 6:
          output.append((char) (current | (c >> 9)));
          current = (c & 511) << 7;
          break;
        case 7:
          output.append((char) (current | (c >> 8)));
          current = (c & 255) << 8;
          break;
        case 8:
          output.append((char) (current | (c >> 7)));
          current = (c & 127) << 9;
          break;
        case 9:
          output.append((char) (current | (c >> 6)));
          current = (c & 63) << 10;
          break;
        case 10:
          output.append((char) (current | (c >> 5)));
          current = (c & 31) << 11;
          break;
        case 11:
          output.append((char) (current | (c >> 4)));
          current = (c & 15) << 12;
          break;
        case 12:
          output.append((char) (current | (c >> 3)));
          current = (c & 7) << 13;
          break;
        case 13:
          output.append((char) (current | (c >> 2)));
          current = (c & 3) << 14;
          break;
        case 14:
          output.append((char) (current | (c >> 1)));
          current = (c & 1) << 15;
          break;
        case 15:
          output.append((char) (current | c));

          status = 0;
          break;
      }

      i++;
    }

    return LZString.decompress(output.toString());
    // return output;

  }

  public static String decompressFromBase64(String input) throws Exception {
    return LZString.decompress(decode64(input));
  }


  // implemented from JS version
  @SuppressWarnings("unused")
public static String decode64(String input) throws Exception {
      //return new String(Base64Util.decode(input.getBytes()));
      
    StringBuilder str = new StringBuilder(200);

    int ol = 0;
    int output_=0;
    int chr1, chr2, chr3;
    int enc1, enc2, enc3, enc4;
    int i = 0; int j=0;

    while (i < input.length()) {

      enc1 = keyStr.indexOf(input.charAt(i++));
      enc2 = keyStr.indexOf(input.charAt(i++));
      enc3 = keyStr.indexOf(input.charAt(i++));
      enc4 = keyStr.indexOf(input.charAt(i++));

      chr1 = (enc1 << 2) | (enc2 >> 4);
      chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
      chr3 = ((enc3 & 3) << 6) | enc4;

      if (ol%2==0) {
        output_ = chr1 << 8;

        if (enc3 != 64) {
          str.append((char) (output_ | chr2));
        }
        if (enc4 != 64) {
          output_ = chr3 << 8;
        }
      } else {
        str.append((char) (output_ | chr1));

        if (enc3 != 64) {
          output_ = chr2 << 8;
        }
        if (enc4 != 64) {
          str.append((char) (output_ | chr3));
        }
      }
      ol+=3;
    }

    return str.toString();
  }
  
  public static String encode64(String input) throws Exception {
      return new String(Base64Util.encode(input.getBytes()));
  }

  public static String compressToBase64(String input) throws Exception {
    //return encode64(compress(input));
      
    StringBuilder result = new StringBuilder((input.length() * 8 + 1) / 3);
    for (int i = 0, max = input.length() << 1; i < max;) {
      int left = max - i;
      if (left >= 3) {
        int ch1 = (input.charAt(i >> 1) >> ((1 - (i & 1)) << 3)) & 0xff;
        i++;
        int ch2 = (input.charAt(i >> 1) >> ((1 - (i & 1)) << 3)) & 0xff;
        i++;
        int ch3 = (input.charAt(i >> 1) >> ((1 - (i & 1)) << 3)) & 0xff;
        i++;
        result.append(keyStr.charAt((ch1 >> 2) & 0x3f));
        result.append(keyStr.charAt(((ch1 << 4) + (ch2 >> 4)) & 0x3f));
        result.append(keyStr.charAt(((ch2 << 2) + (ch3 >> 6)) & 0x3f));
        result.append(keyStr.charAt(ch3 & 0x3f));
      } else if (left == 2) {
        int ch1 = (input.charAt(i >> 1) >> ((1 - (i & 1)) << 3)) & 0xff;
        i++;
        int ch2 = (input.charAt(i >> 1) >> ((1 - (i & 1)) << 3)) & 0xff;
        i++;
        result.append(keyStr.charAt((ch1 >> 2) & 0x3f));
        result.append(keyStr.charAt(((ch1 << 4) + (ch2 >> 4)) & 0x3f));
        result.append(keyStr.charAt(((ch2 << 2)) & 0x3f));
        result.append('=');
      } else if (left == 1) {
        int ch1 = (input.charAt(i >> 1) >> ((1 - (i & 1)) << 3)) & 0xff;
        i++;
        result.append(keyStr.charAt((ch1 >> 2) & 0x3f));
        result.append(keyStr.charAt(((ch1 << 4)) & 0x3f));
        result.append('=');
        result.append('=');
      }
    }
    return result.toString();
  }


/*
  public static void main(String args[]) throws Exception {

    String testBase64 = "eyJwcm9kdWN0Ijoi67m16r6465il6r64IiwicHJvZHVjdElEIjoiYWJjZC1lZmdoaWoiLCJjb2xvciI6IiMxNDUzOTQiLCJ2YXJpYWJsZVN0cm9rZVdpZHRoIjp0cnVlLCJwZW5XaWR0aCI6MiwicGFkV2lkdGgiOjM1MCwicGFkSGVpZ2h0IjoxMDAsImRhdGEiOlt7Imx4IjoyMywibHkiOjc1LCJteCI6MjMsIm15Ijo3NH0seyJseCI6MjQsImx5Ijo3NCwibXgiOjIzLCJteSI6NzV9LHsibHgiOjI0LCJseSI6NzMsIm14IjoyNCwibXkiOjc0fSx7Imx4IjoyNSwibHkiOjczLCJteCI6MjQsIm15Ijo3M30seyJseCI6MjUsImx5Ijo3MiwibXgiOjI1LCJteSI6NzN9LHsibHgiOjI1LCJseSI6NzEsIm14IjoyNSwibXkiOjcyfSx7Imx4IjoyNiwibHkiOjcxLCJteCI6MjUsIm15Ijo3MX0seyJseCI6MjcsImx5Ijo3MSwibXgiOjI2LCJteSI6NzF9LHsibHgiOjI3LCJseSI6NzAsIm14IjoyNywibXkiOjcxfSx7Imx4IjoyOCwibHkiOjcwLCJteCI6MjcsIm15Ijo3MH0seyJseCI6MjgsImx5Ijo2OSwibXgiOjI4LCJteSI6NzB9LHsibHgiOjI4LCJseSI6NjgsIm14IjoyOCwibXkiOjY5fSx7Imx4IjoyOSwibHkiOjY4LCJteCI6MjgsIm15Ijo2OH0seyJseCI6MjksImx5Ijo2NywibXgiOjI5LCJteSI6Njh9LHsibHgiOjI5LCJseSI6NjYsIm14IjoyOSwibXkiOjY3fSx7Imx4IjoyOSwibHkiOjY1LCJteCI6MjksIm15Ijo2Nn0seyJseCI6MjksImx5Ijo2NCwibXgiOjI5LCJteSI6NjV9LHsibHgiOjI5LCJseSI6NjMsIm14IjoyOSwibXkiOjY0fSx7Imx4IjoyOSwibHkiOjYyLCJteCI6MjksIm15Ijo2M30seyJseCI6MjksImx5Ijo2MSwibXgiOjI5LCJteSI6NjJ9LHsibHgiOjI5LCJseSI6NjAsIm14IjoyOSwibXkiOjYxfSx7Imx4IjoyOSwibHkiOjU5LCJteCI6MjksIm15Ijo2MH0seyJseCI6MjksImx5Ijo1OCwibXgiOjI5LCJteSI6NTl9LHsibHgiOjI5LCJseSI6NTcsIm14IjoyOSwibXkiOjU4fSx7Imx4IjoyOSwibHkiOjU2LCJteCI6MjksIm15Ijo1N30seyJseCI6MjksImx5Ijo1NSwibXgiOjI5LCJteSI6NTZ9LHsibHgiOjI5LCJseSI6NTQsIm14IjoyOSwibXkiOjU1fSx7Imx4IjoyOSwibHkiOjUzLCJteCI6MjksIm15Ijo1NH0seyJseCI6MjksImx5Ijo1MiwibXgiOjI5LCJteSI6NTN9LHsibHgiOjI5LCJseSI6NTEsIm14IjoyOSwibXkiOjUyfSx7Imx4IjoyOSwibHkiOjUwLCJteCI6MjksIm15Ijo1MX0seyJseCI6MzAsImx5Ijo1MCwibXgiOjI5LCJteSI6NTB9LHsibHgiOjMwLCJseSI6NDksIm14IjozMCwibXkiOjUwfSx7Imx4IjozMCwibHkiOjQ4LCJteCI6MzAsIm15Ijo0OX0seyJseCI6MzAsImx5Ijo0NywibXgiOjMwLCJteSI6NDh9LHsibHgiOjMxLCJseSI6NDcsIm14IjozMCwibXkiOjQ3fSx7Imx4IjozMSwibHkiOjQ2LCJteCI6MzEsIm15Ijo0N30seyJseCI6MzIsImx5Ijo0NiwibXgiOjMxLCJteSI6NDZ9LHsibHgiOjMyLCJseSI6NDUsIm14IjozMiwibXkiOjQ2fSx7Imx4IjozMiwibHkiOjQ0LCJteCI6MzIsIm15Ijo0NX0seyJseCI6MzMsImx5Ijo0NCwibXgiOjMyLCJteSI6NDR9LHsibHgiOjMzLCJseSI6NDMsIm14IjozMywibXkiOjQ0fSx7Imx4IjozNCwibHkiOjQzLCJteCI6MzMsIm15Ijo0M30seyJseCI6MzUsImx5Ijo0MiwibXgiOjM0LCJteSI6NDN9LHsibHgiOjM1LCJseSI6NDEsIm14IjozNSwibXkiOjQyfSx7Imx4IjozNiwibHkiOjQxLCJteCI6MzUsIm15Ijo0MX0seyJseCI6MzcsImx5Ijo0MSwibXgiOjM2LCJteSI6NDF9LHsibHgiOjM4LCJseSI6NDEsIm14IjozNywibXkiOjQxfSx7Imx4IjozOCwibHkiOjQwLCJteCI6MzgsIm15Ijo0MX0seyJseCI6MzksImx5Ijo0MCwibXgiOjM4LCJteSI6NDB9LHsibHgiOjQwLCJseSI6NDAsIm14IjozOSwibXkiOjQwfSx7Imx4Ijo0MSwibHkiOjQxLCJteCI6NDAsIm15Ijo0MH0seyJseCI6NDIsImx5Ijo0MiwibXgiOjQxLCJteSI6NDF9LHsibHgiOjQyLCJseSI6NDMsIm14Ijo0MiwibXkiOjQyfSx7Imx4Ijo0MywibHkiOjQzLCJteCI6NDIsIm15Ijo0M30seyJseCI6NDMsImx5Ijo0NCwibXgiOjQzLCJteSI6NDN9LHsibHgiOjQzLCJseSI6NDUsIm14Ijo0MywibXkiOjQ0fSx7Imx4Ijo0NCwibHkiOjQ1LCJteCI6NDMsIm15Ijo0NX0seyJseCI6NDQsImx5Ijo0NiwibXgiOjQ0LCJteSI6NDV9LHsibHgiOjQ0LCJseSI6NDcsIm14Ijo0NCwibXkiOjQ2fSx7Imx4Ijo0NSwibHkiOjQ3LCJteCI6NDQsIm15Ijo0N30seyJseCI6NDUsImx5Ijo0OCwibXgiOjQ1LCJteSI6NDd9LHsibHgiOjQ2LCJseSI6NDgsIm14Ijo0NSwibXkiOjQ4fSx7Imx4Ijo0NiwibHkiOjQ5LCJteCI6NDYsIm15Ijo0OH0seyJseCI6NDYsImx5Ijo1MCwibXgiOjQ2LCJteSI6NDl9LHsibHgiOjQ3LCJseSI6NTAsIm14Ijo0NiwibXkiOjUwfSx7Imx4Ijo0NywibHkiOjUxLCJteCI6NDcsIm15Ijo1MH0seyJseCI6NDcsImx5Ijo1MiwibXgiOjQ3LCJteSI6NTF9LHsibHgiOjQ3LCJseSI6NTMsIm14Ijo0NywibXkiOjUyfSx7Imx4Ijo0NywibHkiOjU0LCJteCI6NDcsIm15Ijo1M30seyJseCI6NDgsImx5Ijo1NCwibXgiOjQ3LCJteSI6NTR9LHsibHgiOjQ4LCJseSI6NTUsIm14Ijo0OCwibXkiOjU0fSx7Imx4Ijo0OCwibHkiOjU2LCJteCI6NDgsIm15Ijo1NX0seyJseCI6NDksImx5Ijo1NiwibXgiOjQ4LCJteSI6NTZ9LHsibHgiOjQ5LCJseSI6NTcsIm14Ijo0OSwibXkiOjU2fSx7Imx4Ijo0OSwibHkiOjU4LCJteCI6NDksIm15Ijo1N30seyJseCI6NDksImx5Ijo1OSwibXgiOjQ5LCJteSI6NTh9LHsibHgiOjQ5LCJseSI6NjAsIm14Ijo0OSwibXkiOjU5fSx7Imx4Ijo1MCwibHkiOjYwLCJteCI6NDksIm15Ijo2MH0seyJseCI6NTAsImx5Ijo2MSwibXgiOjUwLCJteSI6NjB9LHsibHgiOjUwLCJseSI6NjIsIm14Ijo1MCwibXkiOjYxfSx7Imx4Ijo1MCwibHkiOjYzLCJteCI6NTAsIm15Ijo2Mn0seyJseCI6NTEsImx5Ijo2NCwibXgiOjUwLCJteSI6NjN9LHsibHgiOjUxLCJseSI6NjUsIm14Ijo1MSwibXkiOjY0fSx7Imx4Ijo1MSwibHkiOjY2LCJteCI6NTEsIm15Ijo2NX0seyJseCI6NTEsImx5Ijo2NywibXgiOjUxLCJteSI6NjZ9LHsibHgiOjUyLCJseSI6NjcsIm14Ijo1MSwibXkiOjY3fSx7Imx4Ijo1MiwibHkiOjY4LCJteCI6NTIsIm15Ijo2N30seyJseCI6NTMsImx5Ijo2OSwibXgiOjUyLCJteSI6Njh9LHsibHgiOjUzLCJseSI6NzAsIm14Ijo1MywibXkiOjY5fSx7Imx4Ijo1NCwibHkiOjcxLCJteCI6NTMsIm15Ijo3MH0seyJseCI6NTQsImx5Ijo3MiwibXgiOjU0LCJteSI6NzF9LHsibHgiOjU0LCJseSI6NzMsIm14Ijo1NCwibXkiOjcyfSx7Imx4Ijo1NSwibHkiOjczLCJteCI6NTQsIm15Ijo3M30seyJseCI6NTYsImx5Ijo3MywibXgiOjU1LCJteSI6NzN9LHsibHgiOjU2LCJseSI6NzQsIm14Ijo1NiwibXkiOjczfSx7Imx4Ijo1NywibHkiOjc0LCJteCI6NTYsIm15Ijo3NH0seyJseCI6NTgsImx5Ijo3NCwibXgiOjU3LCJteSI6NzR9LHsibHgiOjU4LCJseSI6NzUsIm14Ijo1OCwibXkiOjc0fSx7Imx4Ijo1OSwibHkiOjc1LCJteCI6NTgsIm15Ijo3NX0seyJseCI6NjAsImx5Ijo3NSwibXgiOjU5LCJteSI6NzV9LHsibHgiOjYxLCJseSI6NzUsIm14Ijo2MCwibXkiOjc1fSx7Imx4Ijo2MiwibHkiOjc1LCJteCI6NjEsIm15Ijo3NX0seyJseCI6NjMsImx5Ijo3NSwibXgiOjYyLCJteSI6NzV9LHsibHgiOjY0LCJseSI6NzUsIm14Ijo2MywibXkiOjc1fSx7Imx4Ijo2NSwibHkiOjc0LCJteCI6NjQsIm15Ijo3NX0seyJseCI6NjYsImx5Ijo3NCwibXgiOjY1LCJteSI6NzR9LHsibHgiOjY3LCJseSI6NzQsIm14Ijo2NiwibXkiOjc0fSx7Imx4Ijo2NywibHkiOjczLCJteCI6NjcsIm15Ijo3NH0seyJseCI6NjgsImx5Ijo3MywibXgiOjY3LCJteSI6NzN9LHsibHgiOjY4LCJseSI6NzIsIm14Ijo2OCwibXkiOjczfSx7Imx4Ijo2OSwibHkiOjcyLCJteCI6NjgsIm15Ijo3Mn0seyJseCI6NzAsImx5Ijo3MSwibXgiOjY5LCJteSI6NzJ9LHsibHgiOjcxLCJseSI6NzEsIm14Ijo3MCwibXkiOjcxfSx7Imx4Ijo3MSwibHkiOjcwLCJteCI6NzEsIm15Ijo3MX0seyJseCI6NzIsImx5Ijo2OSwibXgiOjcxLCJteSI6NzB9LHsibHgiOjcyLCJseSI6NjgsIm14Ijo3MiwibXkiOjY5fSx7Imx4Ijo3MywibHkiOjY4LCJteCI6NzIsIm15Ijo2OH0seyJseCI6NzQsImx5Ijo2NywibXgiOjczLCJteSI6Njh9LHsibHgiOjc1LCJseSI6NjYsIm14Ijo3NCwibXkiOjY3fSx7Imx4Ijo3NSwibHkiOjY1LCJteCI6NzUsIm15Ijo2Nn0seyJseCI6NzYsImx5Ijo2NSwibXgiOjc1LCJteSI6NjV9LHsibHgiOjc2LCJseSI6NjQsIm14Ijo3NiwibXkiOjY1fSx7Imx4Ijo3NywibHkiOjY0LCJteCI6NzYsIm15Ijo2NH0seyJseCI6NzcsImx5Ijo2MywibXgiOjc3LCJteSI6NjR9LHsibHgiOjc3LCJseSI6NjIsIm14Ijo3NywibXkiOjYzfSx7Imx4Ijo3NywibHkiOjYxLCJteCI6NzcsIm15Ijo2Mn0seyJseCI6NzgsImx5Ijo2MSwibXgiOjc3LCJteSI6NjF9LHsibHgiOjc4LCJseSI6NjAsIm14Ijo3OCwibXkiOjYxfSx7Imx4Ijo3OCwibHkiOjU5LCJteCI6NzgsIm15Ijo2MH0seyJseCI6NzksImx5Ijo1OSwibXgiOjc4LCJteSI6NTl9LHsibHgiOjc5LCJseSI6NTgsIm14Ijo3OSwibXkiOjU5fSx7Imx4Ijo4MCwibHkiOjU4LCJteCI6NzksIm15Ijo1OH0seyJseCI6ODAsImx5Ijo1NywibXgiOjgwLCJteSI6NTh9LHsibHgiOjgwLCJseSI6NTYsIm14Ijo4MCwibXkiOjU3fSx7Imx4Ijo4MCwibHkiOjU1LCJteCI6ODAsIm15Ijo1Nn0seyJseCI6ODAsImx5Ijo1NCwibXgiOjgwLCJteSI6NTV9LHsibHgiOjgwLCJseSI6NTMsIm14Ijo4MCwibXkiOjU0fSx7Imx4Ijo4MCwibHkiOjUyLCJteCI6ODAsIm15Ijo1M30seyJseCI6ODAsImx5Ijo1MSwibXgiOjgwLCJteSI6NTJ9LHsibHgiOjgxLCJseSI6NTEsIm14Ijo4MCwibXkiOjUxfSx7Imx4Ijo4MSwibHkiOjUwLCJteCI6ODEsIm15Ijo1MX0seyJseCI6ODEsImx5Ijo0OSwibXgiOjgxLCJteSI6NTB9LHsibHgiOjgxLCJseSI6NDgsIm14Ijo4MSwibXkiOjQ5fSx7Imx4Ijo4MSwibHkiOjQ3LCJteCI6ODEsIm15Ijo0OH0seyJseCI6ODEsImx5Ijo0NiwibXgiOjgxLCJteSI6NDd9LHsibHgiOjgxLCJseSI6NDUsIm14Ijo4MSwibXkiOjQ2fSx7Imx4Ijo4MiwibHkiOjQ1LCJteCI6ODEsIm15Ijo0NX0seyJseCI6ODIsImx5Ijo0NCwibXgiOjgyLCJteSI6NDV9LHsibHgiOjgzLCJseSI6NDQsIm14Ijo4MiwibXkiOjQ0fSx7Imx4Ijo4MywibHkiOjQzLCJteCI6ODMsIm15Ijo0NH0seyJseCI6ODQsImx5Ijo0MywibXgiOjgzLCJteSI6NDN9LHsibHgiOjg0LCJseSI6NDIsIm14Ijo4NCwibXkiOjQzfSx7Imx4Ijo4NSwibHkiOjQxLCJteCI6ODQsIm15Ijo0Mn0seyJseCI6ODYsImx5Ijo0MSwibXgiOjg1LCJteSI6NDF9LHsibHgiOjg3LCJseSI6NDEsIm14Ijo4NiwibXkiOjQxfSx7Imx4Ijo4OCwibHkiOjQxLCJteCI6ODcsIm15Ijo0MX0seyJseCI6ODgsImx5Ijo0MiwibXgiOjg4LCJteSI6NDF9LHsibHgiOjg5LCJseSI6NDMsIm14Ijo4OCwibXkiOjQyfSx7Imx4Ijo5MCwibHkiOjQ0LCJteCI6ODksIm15Ijo0M30seyJseCI6OTAsImx5Ijo0NSwibXgiOjkwLCJteSI6NDR9LHsibHgiOjkxLCJseSI6NDUsIm14Ijo5MCwibXkiOjQ1fSx7Imx4Ijo5MSwibHkiOjQ2LCJteCI6OTEsIm15Ijo0NX0seyJseCI6OTIsImx5Ijo0NywibXgiOjkxLCJteSI6NDZ9LHsibHgiOjkzLCJseSI6NDgsIm14Ijo5MiwibXkiOjQ3fSx7Imx4Ijo5NCwibHkiOjQ5LCJteCI6OTMsIm15Ijo0OH0seyJseCI6OTQsImx5Ijo1MCwibXgiOjk0LCJteSI6NDl9LHsibHgiOjk0LCJseSI6NTEsIm14Ijo5NCwibXkiOjUwfSx7Imx4Ijo5NSwibHkiOjUxLCJteCI6OTQsIm15Ijo1MX0seyJseCI6OTUsImx5Ijo1MiwibXgiOjk1LCJteSI6NTF9LHsibHgiOjk1LCJseSI6NTMsIm14Ijo5NSwibXkiOjUyfSx7Imx4Ijo5NiwibHkiOjUzLCJteCI6OTUsIm15Ijo1M30seyJseCI6OTYsImx5Ijo1NCwibXgiOjk2LCJteSI6NTN9LHsibHgiOjk3LCJseSI6NTUsIm14Ijo5NiwibXkiOjU0fSx7Imx4Ijo5NywibHkiOjU2LCJteCI6OTcsIm15Ijo1NX0seyJseCI6OTgsImx5Ijo1NywibXgiOjk3LCJteSI6NTZ9LHsibHgiOjk4LCJseSI6NTgsIm14Ijo5OCwibXkiOjU3fSx7Imx4Ijo5OCwibHkiOjU5LCJteCI6OTgsIm15Ijo1OH0seyJseCI6OTksImx5Ijo1OSwibXgiOjk4LCJteSI6NTl9LHsibHgiOjk5LCJseSI6NjAsIm14Ijo5OSwibXkiOjU5fSx7Imx4Ijo5OSwibHkiOjYxLCJteCI6OTksIm15Ijo2MH0seyJseCI6MTAwLCJseSI6NjIsIm14Ijo5OSwibXkiOjYxfSx7Imx4IjoxMDAsImx5Ijo2MywibXgiOjEwMCwibXkiOjYyfSx7Imx4IjoxMDEsImx5Ijo2MywibXgiOjEwMCwibXkiOjYzfSx7Imx4IjoxMDEsImx5Ijo2NCwibXgiOjEwMSwibXkiOjYzfSx7Imx4IjoxMDEsImx5Ijo2NSwibXgiOjEwMSwibXkiOjY0fSx7Imx4IjoxMDIsImx5Ijo2NSwibXgiOjEwMSwibXkiOjY1fSx7Imx4IjoxMDIsImx5Ijo2NiwibXgiOjEwMiwibXkiOjY1fSx7Imx4IjoxMDMsImx5Ijo2NywibXgiOjEwMiwibXkiOjY2fSx7Imx4IjoxMDMsImx5Ijo2OSwibXgiOjEwMywibXkiOjY3fSx7Imx4IjoxMDQsImx5Ijo2OSwibXgiOjEwMywibXkiOjY5fSx7Imx4IjoxMDUsImx5Ijo3MCwibXgiOjEwNCwibXkiOjY5fSx7Imx4IjoxMDYsImx5Ijo3MSwibXgiOjEwNSwibXkiOjcwfSx7Imx4IjoxMDYsImx5Ijo3MiwibXgiOjEwNiwibXkiOjcxfSx7Imx4IjoxMDcsImx5Ijo3MiwibXgiOjEwNiwibXkiOjcyfSx7Imx4IjoxMDgsImx5Ijo3MywibXgiOjEwNywibXkiOjcyfSx7Imx4IjoxMDksImx5Ijo3MywibXgiOjEwOCwibXkiOjczfSx7Imx4IjoxMDksImx5Ijo3NCwibXgiOjEwOSwibXkiOjczfSx7Imx4IjoxMTAsImx5Ijo3NSwibXgiOjEwOSwibXkiOjc0fSx7Imx4IjoxMTIsImx5Ijo3NSwibXgiOjExMCwibXkiOjc1fSx7Imx4IjoxMTIsImx5Ijo3NiwibXgiOjExMiwibXkiOjc1fSx7Imx4IjoxMTMsImx5Ijo3NiwibXgiOjExMiwibXkiOjc2fSx7Imx4IjoxMTQsImx5Ijo3NiwibXgiOjExMywibXkiOjc2fSx7Imx4IjoxMTUsImx5Ijo3NywibXgiOjExNCwibXkiOjc2fSx7Imx4IjoxMTYsImx5Ijo3NywibXgiOjExNSwibXkiOjc3fSx7Imx4IjoxMTcsImx5Ijo3NywibXgiOjExNiwibXkiOjc3fSx7Imx4IjoxMTcsImx5Ijo3OCwibXgiOjExNywibXkiOjc3fSx7Imx4IjoxMTgsImx5Ijo3OCwibXgiOjExNywibXkiOjc4fSx7Imx4IjoxMTksImx5Ijo3OCwibXgiOjExOCwibXkiOjc4fSx7Imx4IjoxMjAsImx5Ijo3OCwibXgiOjExOSwibXkiOjc4fSx7Imx4IjoxMjEsImx5Ijo3OCwibXgiOjEyMCwibXkiOjc4fSx7Imx4IjoxMjIsImx5Ijo3OCwibXgiOjEyMSwibXkiOjc4fSx7Imx4IjoxMjQsImx5Ijo3OCwibXgiOjEyMiwibXkiOjc4fSx7Imx4IjoxMjUsImx5Ijo3OCwibXgiOjEyNCwibXkiOjc4fSx7Imx4IjoxMjYsImx5Ijo3OCwibXgiOjEyNSwibXkiOjc4fSx7Imx4IjoxMjcsImx5Ijo3OCwibXgiOjEyNiwibXkiOjc4fSx7Imx4IjoxMjgsImx5Ijo3OCwibXgiOjEyNywibXkiOjc4fSx7Imx4IjoxMjksImx5Ijo3OCwibXgiOjEyOCwibXkiOjc4fSx7Imx4IjoxMzAsImx5Ijo3OCwibXgiOjEyOSwibXkiOjc4fSx7Imx4IjoxMzAsImx5Ijo3NywibXgiOjEzMCwibXkiOjc4fSx7Imx4IjoxMzEsImx5Ijo3NywibXgiOjEzMCwibXkiOjc3fSx7Imx4IjoxMzIsImx5Ijo3NywibXgiOjEzMSwibXkiOjc3fSx7Imx4IjoxMzIsImx5Ijo3NiwibXgiOjEzMiwibXkiOjc3fSx7Imx4IjoxMzMsImx5Ijo3NiwibXgiOjEzMiwibXkiOjc2fSx7Imx4IjoxMzQsImx5Ijo3NiwibXgiOjEzMywibXkiOjc2fSx7Imx4IjoxMzQsImx5Ijo3NSwibXgiOjEzNCwibXkiOjc2fSx7Imx4IjoxMzUsImx5Ijo3NSwibXgiOjEzNCwibXkiOjc1fSx7Imx4IjoxMzUsImx5Ijo3NCwibXgiOjEzNSwibXkiOjc1fSx7Imx4IjoxMzYsImx5Ijo3MywibXgiOjEzNSwibXkiOjc0fSx7Imx4IjoxMzcsImx5Ijo3MywibXgiOjEzNiwibXkiOjczfSx7Imx4IjoxMzcsImx5Ijo3MiwibXgiOjEzNywibXkiOjczfSx7Imx4IjoxMzgsImx5Ijo3MSwibXgiOjEzNywibXkiOjcyfSx7Imx4IjoxMzgsImx5Ijo3MCwibXgiOjEzOCwibXkiOjcxfSx7Imx4IjoxMzgsImx5Ijo2OSwibXgiOjEzOCwibXkiOjcwfSx7Imx4IjoxMzksImx5Ijo2OCwibXgiOjEzOCwibXkiOjY5fSx7Imx4IjoxMzksImx5Ijo2NywibXgiOjEzOSwibXkiOjY4fSx7Imx4IjoxMzksImx5Ijo2NiwibXgiOjEzOSwibXkiOjY3fSx7Imx4IjoxNDAsImx5Ijo2NCwibXgiOjEzOSwibXkiOjY2fSx7Imx4IjoxNDEsImx5Ijo2NCwibXgiOjE0MCwibXkiOjY0fSx7Imx4IjoxNDEsImx5Ijo2MywibXgiOjE0MSwibXkiOjY0fSx7Imx4IjoxNDEsImx5Ijo2MSwibXgiOjE0MSwibXkiOjYzfSx7Imx4IjoxNDIsImx5Ijo2MSwibXgiOjE0MSwibXkiOjYxfSx7Imx4IjoxNDIsImx5Ijo2MCwibXgiOjE0MiwibXkiOjYxfSx7Imx4IjoxNDIsImx5Ijo1OCwibXgiOjE0MiwibXkiOjYwfSx7Imx4IjoxNDMsImx5Ijo1OCwibXgiOjE0MiwibXkiOjU4fSx7Imx4IjoxNDMsImx5Ijo1NywibXgiOjE0MywibXkiOjU4fSx7Imx4IjoxNDQsImx5Ijo1NiwibXgiOjE0MywibXkiOjU3fSx7Imx4IjoxNDQsImx5Ijo1NSwibXgiOjE0NCwibXkiOjU2fSx7Imx4IjoxNDUsImx5Ijo1NCwibXgiOjE0NCwibXkiOjU1fSx7Imx4IjoxNDUsImx5Ijo1MywibXgiOjE0NSwibXkiOjU0fSx7Imx4IjoxNDUsImx5Ijo1MiwibXgiOjE0NSwibXkiOjUzfSx7Imx4IjoxNDYsImx5Ijo1MSwibXgiOjE0NSwibXkiOjUyfSx7Imx4IjoxNDcsImx5Ijo1MSwibXgiOjE0NiwibXkiOjUxfSx7Imx4IjoxNDcsImx5Ijo1MCwibXgiOjE0NywibXkiOjUxfSx7Imx4IjoxNDcsImx5Ijo0OSwibXgiOjE0NywibXkiOjUwfSx7Imx4IjoxNDgsImx5Ijo0OSwibXgiOjE0NywibXkiOjQ5fSx7Imx4IjoxNDgsImx5Ijo0OCwibXgiOjE0OCwibXkiOjQ5fSx7Imx4IjoxNDksImx5Ijo0NywibXgiOjE0OCwibXkiOjQ4fSx7Imx4IjoxNTAsImx5Ijo0NiwibXgiOjE0OSwibXkiOjQ3fSx7Imx4IjoxNTAsImx5Ijo0NSwibXgiOjE1MCwibXkiOjQ2fSx7Imx4IjoxNTEsImx5Ijo0NCwibXgiOjE1MCwibXkiOjQ1fSx7Imx4IjoxNTIsImx5Ijo0MywibXgiOjE1MSwibXkiOjQ0fSx7Imx4IjoxNTMsImx5Ijo0MywibXgiOjE1MiwibXkiOjQzfSx7Imx4IjoxNTMsImx5Ijo0MiwibXgiOjE1MywibXkiOjQzfSx7Imx4IjoxNTQsImx5Ijo0MiwibXgiOjE1MywibXkiOjQyfSx7Imx4IjoxNTUsImx5Ijo0MSwibXgiOjE1NCwibXkiOjQyfSx7Imx4IjoxNTYsImx5Ijo0MSwibXgiOjE1NSwibXkiOjQxfSx7Imx4IjoxNTcsImx5Ijo0MSwibXgiOjE1NiwibXkiOjQxfSx7Imx4IjoxNTgsImx5Ijo0MSwibXgiOjE1NywibXkiOjQxfSx7Imx4IjoxNTksImx5Ijo0MCwibXgiOjE1OCwibXkiOjQxfSx7Imx4IjoxNjAsImx5Ijo0MCwibXgiOjE1OSwibXkiOjQwfSx7Imx4IjoxNjEsImx5Ijo0MCwibXgiOjE2MCwibXkiOjQwfSx7Imx4IjoxNjIsImx5Ijo0MCwibXgiOjE2MSwibXkiOjQwfSx7Imx4IjoxNjMsImx5Ijo0MCwibXgiOjE2MiwibXkiOjQwfSx7Imx4IjoxNjQsImx5Ijo0MCwibXgiOjE2MywibXkiOjQwfSx7Imx4IjoxNjUsImx5Ijo0MSwibXgiOjE2NCwibXkiOjQwfSx7Imx4IjoxNjYsImx5Ijo0MSwibXgiOjE2NSwibXkiOjQxfSx7Imx4IjoxNjcsImx5Ijo0MSwibXgiOjE2NiwibXkiOjQxfSx7Imx4IjoxNjcsImx5Ijo0MiwibXgiOjE2NywibXkiOjQxfSx7Imx4IjoxNjgsImx5Ijo0MywibXgiOjE2NywibXkiOjQyfSx7Imx4IjoxNjksImx5Ijo0MywibXgiOjE2OCwibXkiOjQzfSx7Imx4IjoxNjksImx5Ijo0NCwibXgiOjE2OSwibXkiOjQzfSx7Imx4IjoxNzAsImx5Ijo0NCwibXgiOjE2OSwibXkiOjQ0fSx7Imx4IjoxNzAsImx5Ijo0NSwibXgiOjE3MCwibXkiOjQ0fSx7Imx4IjoxNzEsImx5Ijo0NiwibXgiOjE3MCwibXkiOjQ1fSx7Imx4IjoxNzIsImx5Ijo0NiwibXgiOjE3MSwibXkiOjQ2fSx7Imx4IjoxNzIsImx5Ijo0NywibXgiOjE3MiwibXkiOjQ2fSx7Imx4IjoxNzMsImx5Ijo0NywibXgiOjE3MiwibXkiOjQ3fSx7Imx4IjoxNzMsImx5Ijo0OCwibXgiOjE3MywibXkiOjQ3fSx7Imx4IjoxNzMsImx5Ijo0OSwibXgiOjE3MywibXkiOjQ4fSx7Imx4IjoxNzQsImx5Ijo0OSwibXgiOjE3MywibXkiOjQ5fSx7Imx4IjoxNzQsImx5Ijo1MCwibXgiOjE3NCwibXkiOjQ5fSx7Imx4IjoxNzQsImx5Ijo1MSwibXgiOjE3NCwibXkiOjUwfSx7Imx4IjoxNzUsImx5Ijo1MSwibXgiOjE3NCwibXkiOjUxfSx7Imx4IjoxNzUsImx5Ijo1MiwibXgiOjE3NSwibXkiOjUxfSx7Imx4IjoxNzUsImx5Ijo1MywibXgiOjE3NSwibXkiOjUyfSx7Imx4IjoxNzUsImx5Ijo1NCwibXgiOjE3NSwibXkiOjUzfSx7Imx4IjoxNzYsImx5Ijo1NSwibXgiOjE3NSwibXkiOjU0fSx7Imx4IjoxNzYsImx5Ijo1NywibXgiOjE3NiwibXkiOjU1fSx7Imx4IjoxNzcsImx5Ijo1OCwibXgiOjE3NiwibXkiOjU3fSx7Imx4IjoxNzgsImx5Ijo1OSwibXgiOjE3NywibXkiOjU4fSx7Imx4IjoxNzgsImx5Ijo2MCwibXgiOjE3OCwibXkiOjU5fSx7Imx4IjoxNzgsImx5Ijo2MSwibXgiOjE3OCwibXkiOjYwfSx7Imx4IjoxNzksImx5Ijo2MiwibXgiOjE3OCwibXkiOjYxfSx7Imx4IjoxNzksImx5Ijo2MywibXgiOjE3OSwibXkiOjYyfSx7Imx4IjoxNzksImx5Ijo2NCwibXgiOjE3OSwibXkiOjYzfSx7Imx4IjoxODAsImx5Ijo2NCwibXgiOjE3OSwibXkiOjY0fSx7Imx4IjoxODEsImx5Ijo2NSwibXgiOjE4MCwibXkiOjY0fSx7Imx4IjoxODEsImx5Ijo2NiwibXgiOjE4MSwibXkiOjY1fSx7Imx4IjoxODEsImx5Ijo2NywibXgiOjE4MSwibXkiOjY2fSx7Imx4IjoxODIsImx5Ijo2NywibXgiOjE4MSwibXkiOjY3fSx7Imx4IjoxODIsImx5Ijo2OCwibXgiOjE4MiwibXkiOjY3fSx7Imx4IjoxODIsImx5Ijo2OSwibXgiOjE4MiwibXkiOjY4fSx7Imx4IjoxODMsImx5Ijo2OSwibXgiOjE4MiwibXkiOjY5fSx7Imx4IjoxODMsImx5Ijo3MCwibXgiOjE4MywibXkiOjY5fSx7Imx4IjoxODMsImx5Ijo3MSwibXgiOjE4MywibXkiOjcwfSx7Imx4IjoxODQsImx5Ijo3MiwibXgiOjE4MywibXkiOjcxfSx7Imx4IjoxODUsImx5Ijo3MiwibXgiOjE4NCwibXkiOjcyfSx7Imx4IjoxODUsImx5Ijo3MywibXgiOjE4NSwibXkiOjcyfSx7Imx4IjoxODUsImx5Ijo3NCwibXgiOjE4NSwibXkiOjczfSx7Imx4IjoxODYsImx5Ijo3NCwibXgiOjE4NSwibXkiOjc0fSx7Imx4IjoxODYsImx5Ijo3NSwibXgiOjE4NiwibXkiOjc0fSx7Imx4IjoxODcsImx5Ijo3NSwibXgiOjE4NiwibXkiOjc1fSx7Imx4IjoxODgsImx5Ijo3NiwibXgiOjE4NywibXkiOjc1fSx7Imx4IjoxODksImx5Ijo3NywibXgiOjE4OCwibXkiOjc2fSx7Imx4IjoxOTAsImx5Ijo3OCwibXgiOjE4OSwibXkiOjc3fSx7Imx4IjoxOTEsImx5Ijo3OCwibXgiOjE5MCwibXkiOjc4fSx7Imx4IjoxOTIsImx5Ijo3OCwibXgiOjE5MSwibXkiOjc4fSx7Imx4IjoxOTIsImx5Ijo3OSwibXgiOjE5MiwibXkiOjc4fSx7Imx4IjoxOTMsImx5Ijo3OSwibXgiOjE5MiwibXkiOjc5fSx7Imx4IjoxOTQsImx5Ijo3OSwibXgiOjE5MywibXkiOjc5fSx7Imx4IjoxOTUsImx5Ijo3OSwibXgiOjE5NCwibXkiOjc5fSx7Imx4IjoxOTYsImx5Ijo3OSwibXgiOjE5NSwibXkiOjc5fSx7Imx4IjoxOTYsImx5Ijo4MCwibXgiOjE5NiwibXkiOjc5fSx7Imx4IjoxOTksImx5Ijo4MSwibXgiOjE5NiwibXkiOjgwfSx7Imx4IjoyMDAsImx5Ijo4MSwibXgiOjE5OSwibXkiOjgxfSx7Imx4IjoyMDIsImx5Ijo4MSwibXgiOjIwMCwibXkiOjgxfSx7Imx4IjoyMDMsImx5Ijo4MSwibXgiOjIwMiwibXkiOjgxfSx7Imx4IjoyMDQsImx5Ijo4MSwibXgiOjIwMywibXkiOjgxfSx7Imx4IjoyMDUsImx5Ijo4MSwibXgiOjIwNCwibXkiOjgxfSx7Imx4IjoyMDYsImx5Ijo4MSwibXgiOjIwNSwibXkiOjgxfSx7Imx4IjoyMDcsImx5Ijo4MSwibXgiOjIwNiwibXkiOjgxfSx7Imx4IjoyMDgsImx5Ijo4MSwibXgiOjIwNywibXkiOjgxfSx7Imx4IjoyMDksImx5Ijo4MSwibXgiOjIwOCwibXkiOjgxfSx7Imx4IjoyMTAsImx5Ijo4MSwibXgiOjIwOSwibXkiOjgxfSx7Imx4IjoyMTEsImx5Ijo4MSwibXgiOjIxMCwibXkiOjgxfSx7Imx4IjoyMTIsImx5Ijo4MSwibXgiOjIxMSwibXkiOjgxfSx7Imx4IjoyMTIsImx5Ijo4MCwibXgiOjIxMiwibXkiOjgxfSx7Imx4IjoyMTMsImx5Ijo3OSwibXgiOjIxMiwibXkiOjgwfSx7Imx4IjoyMTQsImx5Ijo3OSwibXgiOjIxMywibXkiOjc5fSx7Imx4IjoyMTQsImx5Ijo3OCwibXgiOjIxNCwibXkiOjc5fSx7Imx4IjoyMTQsImx5Ijo3NywibXgiOjIxNCwibXkiOjc4fSx7Imx4IjoyMTUsImx5Ijo3NywibXgiOjIxNCwibXkiOjc3fSx7Imx4IjoyMTUsImx5Ijo3NiwibXgiOjIxNSwibXkiOjc3fSx7Imx4IjoyMTYsImx5Ijo3NiwibXgiOjIxNSwibXkiOjc2fSx7Imx4IjoyMTYsImx5Ijo3NSwibXgiOjIxNiwibXkiOjc2fSx7Imx4IjoyMTcsImx5Ijo3NCwibXgiOjIxNiwibXkiOjc1fSx7Imx4IjoyMTcsImx5Ijo3MywibXgiOjIxNywibXkiOjc0fSx7Imx4IjoyMTcsImx5Ijo3MiwibXgiOjIxNywibXkiOjczfSx7Imx4IjoyMTgsImx5Ijo3MiwibXgiOjIxNywibXkiOjcyfSx7Imx4IjoyMTgsImx5Ijo3MSwibXgiOjIxOCwibXkiOjcyfSx7Imx4IjoyMTksImx5Ijo3MSwibXgiOjIxOCwibXkiOjcxfSx7Imx4IjoyMTksImx5Ijo3MCwibXgiOjIxOSwibXkiOjcxfSx7Imx4IjoyMTksImx5Ijo2OSwibXgiOjIxOSwibXkiOjcwfSx7Imx4IjoyMjAsImx5Ijo2OSwibXgiOjIxOSwibXkiOjY5fSx7Imx4IjoyMjAsImx5Ijo2OCwibXgiOjIyMCwibXkiOjY5fSx7Imx4IjoyMjAsImx5Ijo2NywibXgiOjIyMCwibXkiOjY4fSx7Imx4IjoyMjAsImx5Ijo2NiwibXgiOjIyMCwibXkiOjY3fSx7Imx4IjoyMjEsImx5Ijo2NSwibXgiOjIyMCwibXkiOjY2fSx7Imx4IjoyMjEsImx5Ijo2NCwibXgiOjIyMSwibXkiOjY1fSx7Imx4IjoyMjEsImx5Ijo2MywibXgiOjIyMSwibXkiOjY0fSx7Imx4IjoyMjIsImx5Ijo2MiwibXgiOjIyMSwibXkiOjYzfSx7Imx4IjoyMjIsImx5Ijo2MSwibXgiOjIyMiwibXkiOjYyfSx7Imx4IjoyMjMsImx5Ijo2MCwibXgiOjIyMiwibXkiOjYxfSx7Imx4IjoyMjMsImx5Ijo1OSwibXgiOjIyMywibXkiOjYwfSx7Imx4IjoyMjMsImx5Ijo1OCwibXgiOjIyMywibXkiOjU5fSx7Imx4IjoyMjMsImx5Ijo1NywibXgiOjIyMywibXkiOjU4fSx7Imx4IjoyMjMsImx5Ijo1NiwibXgiOjIyMywibXkiOjU3fSx7Imx4IjoyMjQsImx5Ijo1NSwibXgiOjIyMywibXkiOjU2fSx7Imx4IjoyMjQsImx5Ijo1NCwibXgiOjIyNCwibXkiOjU1fSx7Imx4IjoyMjQsImx5Ijo1MywibXgiOjIyNCwibXkiOjU0fSx7Imx4IjoyMjQsImx5Ijo1MiwibXgiOjIyNCwibXkiOjUzfSx7Imx4IjoyMjUsImx5Ijo1MiwibXgiOjIyNCwibXkiOjUyfSx7Imx4IjoyMjUsImx5Ijo1MSwibXgiOjIyNSwibXkiOjUyfSx7Imx4IjoyMjUsImx5Ijo1MCwibXgiOjIyNSwibXkiOjUxfSx7Imx4IjoyMjUsImx5Ijo0OSwibXgiOjIyNSwibXkiOjUwfSx7Imx4IjoyMjYsImx5Ijo0OCwibXgiOjIyNSwibXkiOjQ5fSx7Imx4IjoyMjcsImx5Ijo0OCwibXgiOjIyNiwibXkiOjQ4fSx7Imx4IjoyMjcsImx5Ijo0NywibXgiOjIyNywibXkiOjQ4fSx7Imx4IjoyMjcsImx5Ijo0NiwibXgiOjIyNywibXkiOjQ3fSx7Imx4IjoyMjgsImx5Ijo0NiwibXgiOjIyNywibXkiOjQ2fSx7Imx4IjoyMjgsImx5Ijo0NSwibXgiOjIyOCwibXkiOjQ2fSx7Imx4IjoyMjgsImx5Ijo0NCwibXgiOjIyOCwibXkiOjQ1fSx7Imx4IjoyMjksImx5Ijo0NCwibXgiOjIyOCwibXkiOjQ0fSx7Imx4IjoyMjksImx5Ijo0MywibXgiOjIyOSwibXkiOjQ0fSx7Imx4IjoyMzAsImx5Ijo0MiwibXgiOjIyOSwibXkiOjQzfSx7Imx4IjoyMzEsImx5Ijo0MSwibXgiOjIzMCwibXkiOjQyfSx7Imx4IjoyMzIsImx5Ijo0MCwibXgiOjIzMSwibXkiOjQxfSx7Imx4IjoyMzMsImx5IjozOSwibXgiOjIzMiwibXkiOjQwfSx7Imx4IjoyMzQsImx5IjozOSwibXgiOjIzMywibXkiOjM5fSx7Imx4IjoyMzQsImx5IjozOCwibXgiOjIzNCwibXkiOjM5fSx7Imx4IjoyMzUsImx5IjozOCwibXgiOjIzNCwibXkiOjM4fSx7Imx4IjoyMzYsImx5IjozNywibXgiOjIzNSwibXkiOjM4fSx7Imx4IjoyMzcsImx5IjozNywibXgiOjIzNiwibXkiOjM3fSx7Imx4IjoyMzgsImx5IjozNywibXgiOjIzNywibXkiOjM3fSx7Imx4IjoyMzksImx5IjozNywibXgiOjIzOCwibXkiOjM3fSx7Imx4IjoyMzksImx5IjozNiwibXgiOjIzOSwibXkiOjM3fSx7Imx4IjoyNDAsImx5IjozNiwibXgiOjIzOSwibXkiOjM2fSx7Imx4IjoyNDEsImx5IjozNiwibXgiOjI0MCwibXkiOjM2fSx7Imx4IjoyNDIsImx5IjozNiwibXgiOjI0MSwibXkiOjM2fSx7Imx4IjoyNDMsImx5IjozNiwibXgiOjI0MiwibXkiOjM2fSx7Imx4IjoyNDQsImx5IjozNiwibXgiOjI0MywibXkiOjM2fSx7Imx4IjoyNDUsImx5IjozNywibXgiOjI0NCwibXkiOjM2fSx7Imx4IjoyNDcsImx5IjozOCwibXgiOjI0NSwibXkiOjM3fSx7Imx4IjoyNDcsImx5IjozOSwibXgiOjI0NywibXkiOjM4fSx7Imx4IjoyNDgsImx5Ijo0MCwibXgiOjI0NywibXkiOjM5fSx7Imx4IjoyNDksImx5Ijo0MSwibXgiOjI0OCwibXkiOjQwfSx7Imx4IjoyNDksImx5Ijo0MiwibXgiOjI0OSwibXkiOjQxfSx7Imx4IjoyNTAsImx5Ijo0MiwibXgiOjI0OSwibXkiOjQyfSx7Imx4IjoyNTEsImx5Ijo0MywibXgiOjI1MCwibXkiOjQyfSx7Imx4IjoyNTEsImx5Ijo0NCwibXgiOjI1MSwibXkiOjQzfSx7Imx4IjoyNTIsImx5Ijo0NCwibXgiOjI1MSwibXkiOjQ0fSx7Imx4IjoyNTIsImx5Ijo0NSwibXgiOjI1MiwibXkiOjQ0fSx7Imx4IjoyNTIsImx5Ijo0NiwibXgiOjI1MiwibXkiOjQ1fSx7Imx4IjoyNTMsImx5Ijo0NiwibXgiOjI1MiwibXkiOjQ2fSx7Imx4IjoyNTMsImx5Ijo0NywibXgiOjI1MywibXkiOjQ2fSx7Imx4IjoyNTQsImx5Ijo0NywibXgiOjI1MywibXkiOjQ3fSx7Imx4IjoyNTQsImx5Ijo0OSwibXgiOjI1NCwibXkiOjQ3fSx7Imx4IjoyNTUsImx5Ijo1MCwibXgiOjI1NCwibXkiOjQ5fSx7Imx4IjoyNTUsImx5Ijo1MSwibXgiOjI1NSwibXkiOjUwfSx7Imx4IjoyNTUsImx5Ijo1MiwibXgiOjI1NSwibXkiOjUxfSx7Imx4IjoyNTYsImx5Ijo1MiwibXgiOjI1NSwibXkiOjUyfSx7Imx4IjoyNTcsImx5Ijo1MywibXgiOjI1NiwibXkiOjUyfSx7Imx4IjoyNTcsImx5Ijo1NCwibXgiOjI1NywibXkiOjUzfSx7Imx4IjoyNTcsImx5Ijo1NSwibXgiOjI1NywibXkiOjU0fSx7Imx4IjoyNTgsImx5Ijo1NSwibXgiOjI1NywibXkiOjU1fSx7Imx4IjoyNTgsImx5Ijo1NiwibXgiOjI1OCwibXkiOjU1fSx7Imx4IjoyNTgsImx5Ijo1NywibXgiOjI1OCwibXkiOjU2fSx7Imx4IjoyNTksImx5Ijo1NywibXgiOjI1OCwibXkiOjU3fSx7Imx4IjoyNTksImx5Ijo1OCwibXgiOjI1OSwibXkiOjU3fSx7Imx4IjoyNTksImx5Ijo1OSwibXgiOjI1OSwibXkiOjU4fSx7Imx4IjoyNTksImx5Ijo2MCwibXgiOjI1OSwibXkiOjU5fSx7Imx4IjoyNjAsImx5Ijo2MCwibXgiOjI1OSwibXkiOjYwfSx7Imx4IjoyNjAsImx5Ijo2MSwibXgiOjI2MCwibXkiOjYwfSx7Imx4IjoyNjAsImx5Ijo2MiwibXgiOjI2MCwibXkiOjYxfSx7Imx4IjoyNjEsImx5Ijo2MiwibXgiOjI2MCwibXkiOjYyfSx7Imx4IjoyNjEsImx5Ijo2MywibXgiOjI2MSwibXkiOjYyfSx7Imx4IjoyNjEsImx5Ijo2NCwibXgiOjI2MSwibXkiOjYzfSx7Imx4IjoyNjEsImx5Ijo2NSwibXgiOjI2MSwibXkiOjY0fSx7Imx4IjoyNjIsImx5Ijo2NSwibXgiOjI2MSwibXkiOjY1fSx7Imx4IjoyNjIsImx5Ijo2NiwibXgiOjI2MiwibXkiOjY1fSx7Imx4IjoyNjIsImx5Ijo2NywibXgiOjI2MiwibXkiOjY2fSx7Imx4IjoyNjIsImx5Ijo2OCwibXgiOjI2MiwibXkiOjY3fSx7Imx4IjoyNjMsImx5Ijo2OCwibXgiOjI2MiwibXkiOjY4fSx7Imx4IjoyNjQsImx5Ijo2OCwibXgiOjI2MywibXkiOjY4fSx7Imx4IjoyNjQsImx5Ijo2OSwibXgiOjI2NCwibXkiOjY4fSx7Imx4IjoyNjQsImx5Ijo3MCwibXgiOjI2NCwibXkiOjY5fSx7Imx4IjoyNjUsImx5Ijo3MSwibXgiOjI2NCwibXkiOjcwfSx7Imx4IjoyNjUsImx5Ijo3MiwibXgiOjI2NSwibXkiOjcxfSx7Imx4IjoyNjYsImx5Ijo3MiwibXgiOjI2NSwibXkiOjcyfSx7Imx4IjoyNjYsImx5Ijo3MywibXgiOjI2NiwibXkiOjcyfSx7Imx4IjoyNjcsImx5Ijo3MywibXgiOjI2NiwibXkiOjczfSx7Imx4IjoyNjcsImx5Ijo3NCwibXgiOjI2NywibXkiOjczfSx7Imx4IjoyNjcsImx5Ijo3NSwibXgiOjI2NywibXkiOjc0fSx7Imx4IjoyNjgsImx5Ijo3NSwibXgiOjI2NywibXkiOjc1fSx7Imx4IjoyNjgsImx5Ijo3NiwibXgiOjI2OCwibXkiOjc1fSx7Imx4IjoyNjksImx5Ijo3NiwibXgiOjI2OCwibXkiOjc2fSx7Imx4IjoyNjksImx5Ijo3NywibXgiOjI2OSwibXkiOjc2fSx7Imx4IjoyNzAsImx5Ijo3NywibXgiOjI2OSwibXkiOjc3fSx7Imx4IjoyNzEsImx5Ijo3NywibXgiOjI3MCwibXkiOjc3fSx7Imx4IjoyNzEsImx5Ijo3OCwibXgiOjI3MSwibXkiOjc3fSx7Imx4IjoyNzIsImx5Ijo3OSwibXgiOjI3MSwibXkiOjc4fSx7Imx4IjoyNzMsImx5Ijo3OSwibXgiOjI3MiwibXkiOjc5fSx7Imx4IjoyNzMsImx5Ijo4MCwibXgiOjI3MywibXkiOjc5fSx7Imx4IjoyNzQsImx5Ijo4MCwibXgiOjI3MywibXkiOjgwfSx7Imx4IjoyNzUsImx5Ijo4MCwibXgiOjI3NCwibXkiOjgwfSx7Imx4IjoyNzYsImx5Ijo4MCwibXgiOjI3NSwibXkiOjgwfSx7Imx4IjoyNzYsImx5Ijo4MSwibXgiOjI3NiwibXkiOjgwfSx7Imx4IjoyNzcsImx5Ijo4MSwibXgiOjI3NiwibXkiOjgxfSx7Imx4IjoyNzgsImx5Ijo4MSwibXgiOjI3NywibXkiOjgxfSx7Imx4IjoyNzksImx5Ijo4MiwibXgiOjI3OCwibXkiOjgxfSx7Imx4IjoyODAsImx5Ijo4MiwibXgiOjI3OSwibXkiOjgyfSx7Imx4IjoyODEsImx5Ijo4MiwibXgiOjI4MCwibXkiOjgyfSx7Imx4IjoyODIsImx5Ijo4MiwibXgiOjI4MSwibXkiOjgyfSx7Imx4IjoyODIsImx5Ijo4MywibXgiOjI4MiwibXkiOjgyfSx7Imx4IjoyODMsImx5Ijo4MywibXgiOjI4MiwibXkiOjgzfSx7Imx4IjoyODQsImx5Ijo4MywibXgiOjI4MywibXkiOjgzfSx7Imx4IjoyODUsImx5Ijo4MywibXgiOjI4NCwibXkiOjgzfSx7Imx4IjoyODYsImx5Ijo4MywibXgiOjI4NSwibXkiOjgzfSx7Imx4IjoyODcsImx5Ijo4MywibXgiOjI4NiwibXkiOjgzfSx7Imx4IjoyODgsImx5Ijo4MywibXgiOjI4NywibXkiOjgzfSx7Imx4IjoyODksImx5Ijo4MywibXgiOjI4OCwibXkiOjgzfSx7Imx4IjoyOTAsImx5Ijo4MywibXgiOjI4OSwibXkiOjgzfSx7Imx4IjoyOTEsImx5Ijo4MywibXgiOjI5MCwibXkiOjgzfSx7Imx4IjoyOTIsImx5Ijo4MywibXgiOjI5MSwibXkiOjgzfSx7Imx4IjoyOTMsImx5Ijo4MywibXgiOjI5MiwibXkiOjgzfSx7Imx4IjoyOTQsImx5Ijo4MywibXgiOjI5MywibXkiOjgzfSx7Imx4IjoyOTUsImx5Ijo4MiwibXgiOjI5NCwibXkiOjgzfSx7Imx4IjoyOTYsImx5Ijo4MiwibXgiOjI5NSwibXkiOjgyfSx7Imx4IjoyOTYsImx5Ijo4MSwibXgiOjI5NiwibXkiOjgyfSx7Imx4IjoyOTcsImx5Ijo4MSwibXgiOjI5NiwibXkiOjgxfSx7Imx4IjoyOTcsImx5Ijo4MCwibXgiOjI5NywibXkiOjgxfSx7Imx4IjoyOTgsImx5Ijo4MCwibXgiOjI5NywibXkiOjgwfSx7Imx4IjoyOTgsImx5Ijo3OSwibXgiOjI5OCwibXkiOjgwfSx7Imx4IjoyOTksImx5Ijo3OCwibXgiOjI5OCwibXkiOjc5fSx7Imx4IjozMDAsImx5Ijo3NywibXgiOjI5OSwibXkiOjc4fSx7Imx4IjozMDAsImx5Ijo3NiwibXgiOjMwMCwibXkiOjc3fSx7Imx4IjozMDEsImx5Ijo3NiwibXgiOjMwMCwibXkiOjc2fSx7Imx4IjozMDEsImx5Ijo3NSwibXgiOjMwMSwibXkiOjc2fSx7Imx4IjozMDIsImx5Ijo3NSwibXgiOjMwMSwibXkiOjc1fSx7Imx4IjozMDIsImx5Ijo3NCwibXgiOjMwMiwibXkiOjc1fSx7Imx4IjozMDMsImx5Ijo3NCwibXgiOjMwMiwibXkiOjc0fSx7Imx4IjozMDMsImx5Ijo3MywibXgiOjMwMywibXkiOjc0fSx7Imx4IjozMDMsImx5Ijo3MiwibXgiOjMwMywibXkiOjczfSx7Imx4IjozMDQsImx5Ijo3MSwibXgiOjMwMywibXkiOjcyfSx7Imx4IjozMDQsImx5Ijo3MCwibXgiOjMwNCwibXkiOjcxfSx7Imx4IjozMDUsImx5Ijo3MCwibXgiOjMwNCwibXkiOjcwfSx7Imx4IjozMDYsImx5Ijo3MCwibXgiOjMwNSwibXkiOjcwfSx7Imx4IjozMDYsImx5Ijo2OSwibXgiOjMwNiwibXkiOjcwfSx7Imx4IjozMDYsImx5Ijo2OCwibXgiOjMwNiwibXkiOjY5fSx7Imx4IjozMDcsImx5Ijo2OCwibXgiOjMwNiwibXkiOjY4fSx7Imx4IjozMDcsImx5Ijo2NywibXgiOjMwNywibXkiOjY4fSx7Imx4IjozMDcsImx5Ijo2NiwibXgiOjMwNywibXkiOjY3fSx7Imx4IjozMDgsImx5Ijo2NiwibXgiOjMwNywibXkiOjY2fSx7Imx4IjozMDgsImx5Ijo2NSwibXgiOjMwOCwibXkiOjY2fSx7Imx4IjozMDgsImx5Ijo2NCwibXgiOjMwOCwibXkiOjY1fSx7Imx4IjozMDksImx5Ijo2NCwibXgiOjMwOCwibXkiOjY0fSx7Imx4IjozMTAsImx5Ijo2MywibXgiOjMwOSwibXkiOjY0fSx7Imx4IjozMTAsImx5Ijo2MiwibXgiOjMxMCwibXkiOjYzfSx7Imx4IjozMTEsImx5Ijo2MSwibXgiOjMxMCwibXkiOjYyfSx7Imx4IjozMTEsImx5Ijo2MCwibXgiOjMxMSwibXkiOjYxfSx7Imx4IjozMTEsImx5Ijo1OSwibXgiOjMxMSwibXkiOjYwfSx7Imx4IjozMTIsImx5Ijo1OSwibXgiOjMxMSwibXkiOjU5fSx7Imx4IjozMTIsImx5Ijo1OCwibXgiOjMxMiwibXkiOjU5fSx7Imx4IjozMTMsImx5Ijo1NywibXgiOjMxMiwibXkiOjU4fSx7Imx4IjozMTMsImx5Ijo1NiwibXgiOjMxMywibXkiOjU3fSx7Imx4IjozMTQsImx5Ijo1NiwibXgiOjMxMywibXkiOjU2fSx7Imx4IjozMTQsImx5Ijo1NSwibXgiOjMxNCwibXkiOjU2fSx7Imx4IjozMTQsImx5Ijo1NCwibXgiOjMxNCwibXkiOjU1fSx7Imx4IjozMTUsImx5Ijo1MywibXgiOjMxNCwibXkiOjU0fSx7Imx4IjozMTUsImx5Ijo1MiwibXgiOjMxNSwibXkiOjUzfSx7Imx4IjozMTUsImx5Ijo1MSwibXgiOjMxNSwibXkiOjUyfSx7Imx4IjozMTYsImx5Ijo1MCwibXgiOjMxNSwibXkiOjUxfSx7Imx4IjozMTcsImx5Ijo1MCwibXgiOjMxNiwibXkiOjUwfSx7Imx4IjozMTcsImx5Ijo0OSwibXgiOjMxNywibXkiOjUwfSx7Imx4IjozMTcsImx5Ijo0OCwibXgiOjMxNywibXkiOjQ5fSx7Imx4IjozMTgsImx5Ijo0OCwibXgiOjMxNywibXkiOjQ4fSx7Imx4IjozMTgsImx5Ijo0NywibXgiOjMxOCwibXkiOjQ4fV19";
    testBase64 = "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";
    System.out.println("testBase64: " + testBase64);
    System.out.println(testBase64.length());

    String decompressedBase64 = LZString.decompressFromBase64(testBase64);

    System.out.println("Decompressed: " + decompressedBase64);
  }
*/
  
}

class Data {
  public int val;
  public String string;
  public int position;
  public int index;

  public static Data getInstance() {
    return new Data();
  }
}
