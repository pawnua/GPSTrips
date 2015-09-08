package com.pawnua.android.app.gpstrips;

import android.os.Build;
import android.os.Environment;

import com.pawnua.android.app.gpstrips.model.Trip;
import com.pawnua.android.app.gpstrips.model.TripLocation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by MiK on 05.08.2015.
 */
public class GpxTrackWriter {

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private final NumberFormat speedFormatter;
    private final NumberFormat elevationFormatter;
    private final NumberFormat coordinateFormatter;
    private final SimpleDateFormat timestampFormatter;
    private PrintWriter pw = null;

    public GpxTrackWriter(File f) throws FileNotFoundException {

        // GPX readers expect to see fractional numbers with US-style punctuation.
        // That is, they want periods for decimal points, rather than commas.

        pw = new PrintWriter(f);

        speedFormatter = NumberFormat.getInstance(Locale.US);
        speedFormatter.setMinimumFractionDigits(6);
        speedFormatter.setMaximumFractionDigits(6);
        speedFormatter.setMaximumIntegerDigits(2);
        speedFormatter.setGroupingUsed(false);

        elevationFormatter = NumberFormat.getInstance(Locale.US);
        elevationFormatter.setMaximumFractionDigits(1);
        elevationFormatter.setGroupingUsed(false);

        coordinateFormatter = NumberFormat.getInstance(Locale.US);
        coordinateFormatter.setMaximumFractionDigits(5);
        coordinateFormatter.setMaximumIntegerDigits(3);
        coordinateFormatter.setGroupingUsed(false);

        timestampFormatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
        timestampFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

    }

    private String formatLocation(TripLocation location) {
        return "lat=\"" + coordinateFormatter.format(location.getLatitude())
                + "\" lon=\"" + coordinateFormatter.format(location.getLongitude()) + "\"";
    }

    public void writeHeader() {
        if (pw != null) {
            pw.format("<?xml version=\"1.0\" encoding=\"%s\" standalone=\"yes\"?>\n",
                    Charset.defaultCharset().name());
            pw.println("<gpx xmlns=\"http://www.topografix.com/GPX/1/1\"");
            pw.println(" version=\"1.1\"");
            pw.format(" creator=\"%s\"\n", "GPS trips");
            pw.println(">");
        }
    }

    public void writeFooter() {
        if (pw != null) {
            pw.println("</gpx>");
        }
    }

    public void writeBeginTrack(String name, String desc) {
        if(pw!=null){
            pw.println("    <trk>");
            pw.println("        <name>" + name  + "</name>");
            pw.println("        <desc>" + desc  + "</desc>");
        }
    }

    public void writeEndTrack()
    {
        if (pw != null) {
            pw.println("    </trk>");
        }
    }
    public int writeSegment(List<TripLocation>  tripLocationList)
    {
        if(pw!=null)
        {
            writeOpenSegment();
            for(int i=0; i< tripLocationList.size(); i++)
            {
                TripLocation location = tripLocationList.get(i);
                writeLocation(location);
            }
            writeCloseSegment();
        }
        return tripLocationList.size();
    }

    public void writeOpenSegment() {
        pw.println("            <trkseg>");
    }

    public void writeCloseSegment() {
        pw.println("            </trkseg>");
    }

    public void writeTrack(Trip trip)
    {
        if (pw != null) {
            writeHeader();
            writeBeginTrack(trip.getName(), trip.getDescription());
//            int s;
//            for(s=0; s<t.segments.size(); s++)
//            writeSegment(t.segments.get(s));
            writeSegment(TripLocation.getAllLocations(trip));
            writeEndTrack();
            writeFooter();
            close();
        }
    }
    public void close() {
        if (pw != null) {
            pw.close();
            pw = null;
        }
    }
    public void writeLocation(TripLocation location) {
        if (pw != null) {
            if (location != null) {
                pw.println("                <trkpt " + formatLocation(location) + ">");
                pw.println("                    <speed>" + speedFormatter.format(location.getSpeed()) + "</speed>");
                pw.println("                    <ele>" + elevationFormatter.format(location.getAltitude()) + "</ele>");
                pw.println("                    <time>" + timestampFormatter.format(location.getTime()) + "</time>");
                pw.println("                </trkpt>");
            }
        }
    }

    public static File saveTrip(Trip trip) {
        try {
            File TracksDir =  new File(Environment.getExternalStorageDirectory() + "/GpsTrips/Tracks");
            if(!TracksDir.isDirectory())
                TracksDir.mkdirs();

//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            Calendar c = Calendar.getInstance();
//            String dateString =  sdf.format(c.getTime());//sdf.format(now);
//
//            File gpxfile = new File(TracksDir, dateString + ".gpx");
            File gpxfile = new File(TracksDir, trip.getName() + ".gpx");

            GpxTrackWriter gpx = new GpxTrackWriter(gpxfile);
            gpx.writeTrack(trip);

            return gpxfile;

        }
        catch(FileNotFoundException e){
        }
        return null;
    }
}
