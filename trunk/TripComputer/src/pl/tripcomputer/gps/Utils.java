package pl.tripcomputer.gps;

import java.util.StringTokenizer;


//This is method extracted from Android sources for bugfixing (fix was still not available)  
public class Utils
{
	
  /**
   * Converts a String in one of the formats described by
   * FORMAT_DEGREES, FORMAT_MINUTES, or FORMAT_SECONDS into a
   * double.
   *
   * @throws NullPointerException if coordinate is null
   * @throws IllegalArgumentException if the coordinate is not
   * in one of the valid formats.
   */
  public static double convert(String coordinate)
  {
      // IllegalArgumentException if bad syntax
      if (coordinate == null) {
          throw new NullPointerException("coordinate");
      }

      boolean negative = false;
      if (coordinate.charAt(0) == '-') {
          coordinate = coordinate.substring(1);
          negative = true;
      }

      StringTokenizer st = new StringTokenizer(coordinate, ":");
      int tokens = st.countTokens();
      if (tokens < 1) {
          throw new IllegalArgumentException("coordinate=" + coordinate);
      }
      try {
          String degrees = st.nextToken();
          double val;
          if (tokens == 1) {
              val = Double.parseDouble(degrees);
              return negative ? -val : val;
          }

          String minutes = st.nextToken();
          int deg = Integer.parseInt(degrees);
          double min;
          double sec = 0.0;

          if (st.hasMoreTokens()) {
              min = Integer.parseInt(minutes);
              String seconds = st.nextToken();
              sec = Double.parseDouble(seconds);
          } else {
              min = Double.parseDouble(minutes);
          }

          boolean isNegative180 = negative && (deg == 180) &&
              (min == 0) && (sec == 0);

          // deg must be in [0, 179] except for the case of -180 degrees
          if ((deg < 0.0) || (deg > 179 && !isNegative180))
          {
              throw new IllegalArgumentException("coordinate=" + coordinate);
          }

          if (min < 0 || min > 59)
          {
              throw new IllegalArgumentException("coordinate=" +
                      coordinate);
          }
          
          if (sec < 0 || sec >= 60)
          {
              throw new IllegalArgumentException("coordinate=" +
                      coordinate);
          }

          val = deg*3600.0 + min*60.0 + sec;
          val /= 3600.0;
          return negative ? -val : val;
      } catch (NumberFormatException nfe) {
          throw new IllegalArgumentException("coordinate=" + coordinate);
      }
  }	

}
