package de.obey.crownmc.util;
/*

    Author - Obey -> SkySlayer-v4
       19.10.2022 / 22:57

*/

import lombok.experimental.UtilityClass;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@UtilityClass
public final class MathUtil {

    public String getDaysAnHoursFromSeconds(long seconds) {
        int minutes = 0;
        int days = 0;
        int hours = 0;

        while (seconds >= 60) {
            seconds -= 60;
            minutes++;
        }

        while (minutes >= 60) {
            minutes -= 60;
            hours++;
        }

        while (hours >= 24) {
            hours -= 24;
            days++;
        }

        return (days > 0 ? days + "d " : "") + (hours > 0 ? hours + "h " : "");
    }

    public String getDaysAndHoursAndMinutesAndSecondsFromSeconds(long seconds) {
        int days = 0;
        int hours = 0;
        int minutes = 0;

        while (seconds >= 60) {
            seconds -= 60;
            minutes++;
        }

        while (minutes >= 60) {
            minutes -= 60;
            hours++;
        }

        while (hours >= 24) {
            hours -= 24;
            days++;
        }

        return (days > 0 ? days + "d " : "") + (hours > 0 ? hours + "h " : "") + (minutes > 0 ? minutes + "m " : "") + (seconds > 0 ? seconds + "s " : "");
    }

    public String getHoursAndMinutesAndSecondsFromSeconds(long seconds) {
        int hours = 0;
        int minutes = 0;

        while (seconds >= 60) {
            seconds -= 60;
            minutes++;
        }

        while (minutes >= 60) {
            minutes -= 60;
            hours++;
        }

        return (hours > 0 ? hours + "h " : "") + (minutes > 0 ? minutes + "m " : "") + (seconds > 0 ? seconds + "s " : "");
    }

    public String getMinutesAndSecondsFromSeconds(long seconds) {
        int minutes = 0;

        while (seconds >= 60) {
            seconds -= 60;
            minutes++;
        }

        return (minutes > 0 ? minutes + "m " : "") + (seconds > 0 ? seconds + "s " : "");
    }

    public String getMinutesFromSeconds(long seconds) {
        int minutes = 0;

        while (seconds >= 60) {
            seconds -= 60;
            minutes++;
        }

        return minutes + "min ";
    }

    public long getMillisFromString(final String string) {
        long millis = 0;

        final String[] splitted = string.split(" ");

        for (String s : splitted) {
            if (s.contains("h")) {
                millis += (long) Integer.parseInt(s.replace("h", "")) * 60 * 60 * 1000;
            }

            if (s.contains("m")) {
                millis += (long) Integer.parseInt(s.replace("m", "")) * 60 * 1000;
            }

            if (s.contains("s")) {
                millis += (long) Integer.parseInt(s.replace("s", "")) * 1000;
            }
        }

        return millis;
    }

    public long getLongFromStringwithSuffix(final String text) {
        if (text.contains("k")) {
            try {
                return Long.parseLong(text.replace("k", "")) * 1000;
            } catch (final NumberFormatException exception) {
                return -1;
            }
        }

        if (text.contains("mrd")) {
            try {
                return Long.parseLong(text.toLowerCase().replace("mrd", "")) * 1000000000;
            } catch (final NumberFormatException exception) {
                return -1;
            }
        }

        if (text.contains("brd")) {
            try {
                return Long.parseLong(text.toLowerCase().replace("brd", "")) * 1000000000000000L;
            } catch (final NumberFormatException exception) {
                return -1;
            }
        }

        if (text.contains("mio") || text.contains("m")) {
            try {
                return Long.parseLong(text.replace("m", "").replace("mio", "")) * 1000000;
            } catch (final NumberFormatException exception) {
                return -1;
            }
        }

        if (text.contains("b")) {
            try {
                return Long.parseLong(text.toLowerCase().replace("b", "")) * 1000000000000L;
            } catch (final NumberFormatException exception) {
                return -1;
            }
        }

        if (text.contains("t")) {
            try {
                return Long.parseLong(text.toLowerCase().replace("t", "")) * 1000000000000000000L;
            } catch (final NumberFormatException exception) {
                return -1;
            }
        }

        return -1;
    }

    public String replaceLongWithSuffix(long amount) {
        String bearbeite = "";

        final DecimalFormat df = new DecimalFormat("#,###.##",  new DecimalFormatSymbols(Locale.ENGLISH));

        bearbeite = df.format(amount);

        if (amount <= 999)
            return "" + amount;


        // 1,001,001,100,000,000

        if (bearbeite.length() == 5) {
            bearbeite = bearbeite.substring(0, 4) + "k";
        } else if (bearbeite.length() == 6) {
            bearbeite = bearbeite.substring(0, 5) + "k";
        } else if (bearbeite.length() == 7) {
            bearbeite = bearbeite.substring(0, 6) + "k";
        } else if (bearbeite.length() == 9) {
            bearbeite = bearbeite.substring(0, 4) + "M";
        } else if (bearbeite.length() == 10) {
            bearbeite = bearbeite.substring(0, 5) + "M";
        } else if (bearbeite.length() == 11) {
            bearbeite = bearbeite.substring(0, 6) + "M";
        } else if (bearbeite.length() == 13) {
            bearbeite = bearbeite.substring(0, 4) + "Mrd";
        } else if (bearbeite.length() == 14) {
            bearbeite = bearbeite.substring(0, 5) + "Mrd";
        } else if (bearbeite.length() == 15) {
            bearbeite = bearbeite.substring(0, 6) + "Mrd";
        } else if (bearbeite.length() == 17) {
            bearbeite = bearbeite.substring(0, 4) + "B";
        } else if (bearbeite.length() == 18) {
            bearbeite = bearbeite.substring(0, 5) + "B";
        } else if (bearbeite.length() == 19) {
            bearbeite = bearbeite.substring(0, 6) + "B";
        } else if (bearbeite.length() == 21) {
            bearbeite = bearbeite.substring(0, 4) + "Brd";
        } else if (bearbeite.length() == 22) {
            bearbeite = bearbeite.substring(0, 5) + "Brd";
        } else if (bearbeite.length() == 23) {
            bearbeite = bearbeite.substring(0, 6) + "Brd";
        } else if (bearbeite.length() == 25) {
            bearbeite = bearbeite.substring(0, 4) + "T";
        } else if (bearbeite.length() == 26) {
            bearbeite = bearbeite.substring(0, 5) + "T";
        } else if (bearbeite.length() == 27) {
            bearbeite = bearbeite.substring(0, 6) + "T";
        } else if (bearbeite.length() == 29) {
            bearbeite = bearbeite.substring(0, 4) + "Trd";
        } else if (bearbeite.length() == 30) {
            bearbeite = bearbeite.substring(0, 5) + "Trd";
        } else if (bearbeite.length() == 31) {
            bearbeite = bearbeite.substring(0, 6) + "Trd";
        } else if (bearbeite.length() == 33) {
            bearbeite = bearbeite.substring(0, 4) + "Q";
        } else if (bearbeite.length() == 34) {
            bearbeite = bearbeite.substring(0, 5) + "Q";
        } else if (bearbeite.length() == 35) {
            bearbeite = bearbeite.substring(0, 6) + "Q";
        } else {
            bearbeite = amount + "";
        }

        return bearbeite;
    }

}
