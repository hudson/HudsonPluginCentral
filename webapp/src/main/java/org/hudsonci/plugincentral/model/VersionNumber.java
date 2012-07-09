
package org.hudsonci.plugincentral.model;

import java.util.Locale;
import java.util.StringTokenizer;

public class VersionNumber implements Comparable<VersionNumber> {
    private final int[] digits;

    /**
     * Parses a string like "1.0.2" into the version number.
     *
     * @throws IllegalArgumentException
     *      if the parsing fails.
     */
    public VersionNumber( String num ) {
        StringTokenizer tokens = new StringTokenizer(num,".-_");
        digits = new int[tokens.countTokens()];
        if(digits.length<2)
            throw new IllegalArgumentException("Failed to parse "+num+" as version number");

        int i=0;
        while( tokens.hasMoreTokens() ) {
            String token = tokens.nextToken().toLowerCase(Locale.ENGLISH);
            if(token.equals("*")) {
                digits[i++] = 1000;
            } else
            if(token.startsWith("snapshot")) {
                digits[i-1]--;
                digits[i++] = 1000;
                break;
            } else
            if(token.startsWith("ea")) {
                if(token.length()==2)
                    digits[i++] = -1000;    // just "ea"
                else
                    digits[i++] = -1000 + Integer.parseInt(token.substring(2)); // "eaNNN"
            } else {
                digits[i++] = Integer.parseInt(token);
            }
        }
    }

    public int digit(int idx) {
        return digits[idx];
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for( int i=0; i<digits.length; i++ ) {
            if(i!=0)    buf.append('.');
            buf.append( Integer.toString(digits[i]) );
        }
        return buf.toString();
    }

    public boolean isOlderThan( VersionNumber rhs ) {
        return compareTo(rhs)<0;
    }

    public boolean isNewerThan( VersionNumber rhs ) {
        return compareTo(rhs)>0;
    }


    @Override
    public boolean equals( Object o ) {
        if (!(o instanceof VersionNumber))  return false;
        return compareTo((VersionNumber)o)==0;
    }

    @Override
    public int hashCode() {
        int x=0;
        for (int i : digits)
            x = (x << 1) | i;
        return x;
    }

    public int compareTo(VersionNumber rhs) {
        for( int i=0; ; i++ ) {
            if( i==this.digits.length && i==rhs.digits.length )
                return 0;   // equals
            if( i==this.digits.length )
                return -1;  // rhs is larger
            if( i==rhs.digits.length )
                return 1;

            int r = this.digits[i] - rhs.digits[i];
            if(r!=0)    return r;
        }
    }
}
