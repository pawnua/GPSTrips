package com.pawnua.android.app.gpstrips;

import android.location.Location;
import android.media.ExifInterface;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by MiK on 11.08.2015.
 */
public class GeoTagImage {

    /**
     * Read location information from image.
     *
     * @param imagePath : image absolute path
     * @return : loation information
     */
    public static Location readGeoTagImage(String imagePath) {
        Location location = null;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            float[] latlong = new float[2];
            if (exif.getLatLong(latlong)) {
                location = new Location("");
                location.setLatitude(latlong[0]);
                location.setLongitude(latlong[1]);
            } else {
                return location; // null
            }
            String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
            SimpleDateFormat fmt_Exif = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            location.setTime(fmt_Exif.parse(date).getTime());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * Write Location information to image.
     *
     * @param imagePath : image absolute path
     * @return : location information
     */
    public static void MarkGeoTagImage(String imagePath, Location location) {
        if (location== null) return;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, GPS.convert(location.getLatitude()));
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, GPS.latitudeRef(location.getLatitude()));
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, GPS.convert(location.getLongitude()));
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, GPS.longitudeRef(location.getLongitude()));
            SimpleDateFormat fmt_Exif = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            exif.setAttribute(ExifInterface.TAG_DATETIME, fmt_Exif.format(new Date(location.getTime())));
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


//Code to convert  Degrees to DMS unit

class GPS {
        private static StringBuilder sb = new StringBuilder(20);
        /**
         * returns ref for latitude which is S or N.
         *
         * @param latitude
         * @return S or N
         */
        public static String latitudeRef(final double latitude) {
            return latitude < 0.0d ? "S" : "N";
        }

        /**
         * returns ref for latitude which is S or N.
         *
         * @param longitude
         * @return S or N
         */
        public static String longitudeRef(final double longitude) {
            return longitude < 0.0d ? "W" : "E";
        }
        /**
         * convert latitude into DMS (degree minute second) format. For instance<br/>
         * -79.948862 becomes<br/>
         * 79/1,56/1,55903/1000<br/>
         * It works for latitude and longitude<br/>
         *
         * @param latitude could be longitude.
         * @return
         */
        public static final String convert(double latitude) {
            latitude = Math.abs(latitude);
            final int degree = (int)latitude;
            latitude *= 60;
            latitude -= degree * 60.0d;
            final int minute = (int)latitude;
            latitude *= 60;
            latitude -= minute * 60.0d;
            final int second = (int)(latitude * 1000.0d);

            sb.setLength(0);
            sb.append(degree);
            sb.append("/1,");
            sb.append(minute);
            sb.append("/1,");
            sb.append(second);
            sb.append("/1000,");
            return sb.toString();
        }
}
