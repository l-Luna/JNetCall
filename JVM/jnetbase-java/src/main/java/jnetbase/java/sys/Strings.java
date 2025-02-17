package jnetbase.java.sys;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class Strings {

    public static int countMatches(CharSequence text, char letter) {
        if (text.isEmpty())
            return 0;
        var count = 0;
        for (var i = 0; i < text.length(); ++i)
            if (letter == text.charAt(i))
                ++count;
        return count;
    }

    public static String getStackTrace(Throwable error) {
        var bld = new StringWriter();
        error.printStackTrace(new PrintWriter(bld, true));
        return bld.getBuffer().toString();
    }

    public static String repeat(int num, String c) {
        return new String(new char[num]).replace("\0", c);
    }

    public static String readToEnd(InputStream stream) throws IOException {
        return new String(stream.readAllBytes());
    }
}
