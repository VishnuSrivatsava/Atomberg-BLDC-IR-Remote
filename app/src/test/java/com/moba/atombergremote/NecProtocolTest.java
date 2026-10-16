package com.moba.atombergremote;

import org.junit.Test;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilderFactory;
import static org.junit.Assert.*;

public class NecProtocolTest {
    @Test
    public void everyCommandMatchesExistingRawProfile() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        try (InputStream input = getClass().getResourceAsStream("/Atomberg_Fan.irplus")) {
            assertNotNull("Existing raw profile must be on the test classpath", input);
            NodeList buttons = factory.newDocumentBuilder().parse(input).getElementsByTagName("button");
            NecProtocol.Command[] commands = {
                    NecProtocol.Command.POWER, NecProtocol.Command.SPEED1,
                    NecProtocol.Command.SPEED2, NecProtocol.Command.SPEED3,
                    NecProtocol.Command.SPEED4, NecProtocol.Command.SPEED5,
                    NecProtocol.Command.BOOST, NecProtocol.Command.TIMER,
                    NecProtocol.Command.LED, NecProtocol.Command.SLEEP
            };
            assertEquals(commands.length, buttons.getLength());
            for (int i = 0; i < commands.length; i++) {
                int[] expected = Arrays.stream(buttons.item(i).getTextContent().trim().split("\\s+"))
                        .mapToInt(Integer::parseInt).toArray();
                assertArrayEquals(commands[i].name(), expected, NecProtocol.encode(commands[i].code));
            }
        }
    }

    @Test
    public void ledUsesPublishedBitOrderInsteadOfByteSwap() {
        // Source: Pramod Ghuge's 2025-02-22 capture comment at
        // https://glenzac.wordpress.com/2020/03/25/atomberg-gorilla-efficio-bldc-fan-ir-remote-codes/
        // Power from that same source is MSB-first 0x00CF8976, which the
        // already working app sends as LSB-first 0x6E91F300.
        assertEquals(NecProtocol.Command.POWER.code,
                Integer.toUnsignedLong(Integer.reverse(0x00CF8976)));
        assertEquals(NecProtocol.Command.TIMER.code,
                Integer.toUnsignedLong(Integer.reverse(0x00CF6996)));
        assertEquals(NecProtocol.Command.LED.code,
                Integer.toUnsignedLong(Integer.reverse(0x00CFE916)));
        assertEquals(0xF300L, NecProtocol.Command.LED.code & 0xFFFF);
        assertNotEquals(0xE916CF00L, NecProtocol.Command.LED.code);
    }

    @Test
    public void commandsHaveBoundedDurationAndComplementaryCommandBytes() {
        for (NecProtocol.Command command : NecProtocol.Command.values()) {
            assertEquals(0xFF, ((command.code >>> 16) ^ (command.code >>> 24)) & 0xFF);
            int[] signal = NecProtocol.encode(command.code);
            assertEquals(67, signal.length);
            assertTrue(Arrays.stream(signal).allMatch(duration -> duration > 0));
            assertTrue(Arrays.stream(signal).sum() < 100_000);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNegativeCodes() {
        NecProtocol.encode(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsCodesWiderThan32Bits() {
        NecProtocol.encode(0x100000000L);
    }
}
