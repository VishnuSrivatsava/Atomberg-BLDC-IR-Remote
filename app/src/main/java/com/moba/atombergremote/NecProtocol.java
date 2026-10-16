package com.moba.atombergremote;

/** Repository codes, sent as 32 bits least significant bit first. */
final class NecProtocol {
    static final int FREQUENCY = 38_000;

    enum Command {
        POWER(0x6E91F300L), SPEED1(0x748BF300L), SPEED2(0x6F90F300L),
        SPEED3(0x758AF300L), SPEED4(0x6C93F300L), SPEED5(0x7788F300L),
        BOOST(0x708FF300L), TIMER(0x6996F300L), SLEEP(0x718EF300L),
        // Published MSB-first 0x00CFE916, bit-reversed for this LSB-first encoder.
        // The old imported profile byte-swapped it instead (0xE916CF00).
        LED(0x6897F300L);

        final long code;

        Command(long code) {
            this.code = code;
        }
    }

    private NecProtocol() {}

    static int[] encode(long code) {
        if (code < 0 || code > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("NEC code must be an unsigned 32-bit value");
        }
        int[] pattern = new int[67];
        pattern[0] = 9000;
        pattern[1] = 4500;
        for (int bit = 0; bit < 32; bit++) {
            pattern[2 + bit * 2] = 560;
            pattern[3 + bit * 2] = ((code >>> bit) & 1) == 1 ? 1690 : 560;
        }
        pattern[66] = 560;
        return pattern;
    }
}
