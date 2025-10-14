package com.ozdece.gheasy.ui

import spock.lang.Specification

import java.awt.Color

class ColorConverterSpec extends Specification {

    def "should parse and create Color object from a valid Hexadecimal string"() {
        given: 'A hexadecimal color'
        final String colorHex = "AABB00"

        when: 'Converting the string to Color'
        final Color color = ColorConverter.convertFromHex(colorHex)

        then: 'Color should be created successfully with the given color range'
        color.getRed() == 0xAA
        color.getGreen() == 0xBB
        color.getBlue() == 0x00
    }

    def "should parse and create Color object from a valid Hexadecimal string that starts with #"() {
        given: 'A hexadecimal color'
        final String colorHex = "#AABB00"

        when: 'Converting the string to Color'
        final Color color = ColorConverter.convertFromHex(colorHex)

        then: 'Color should be created successfully with the given color range'
        color.getRed() == 0xAA
        color.getGreen() == 0xBB
        color.getBlue() == 0x00
    }

    def "should throw IllegalArgumentException if the length of the string is less than 6"() {
        given: 'A hexadecimal color'
        final String colorHex = "ABB00"

        when: 'Converting the string to Color'
        ColorConverter.convertFromHex(colorHex)

        then: 'IllegalArgumentException should be thrown'
        final IllegalArgumentException ex = thrown(IllegalArgumentException.class)
        ex.getMessage() == "Color Hex String length must be either 6 or 7 characters"
    }

    def "should throw IllegalArgumentException if the length of the string is more than 7"() {
        given: 'A hexadecimal color'
        final String colorHex = "0000ABB00"

        when: 'Converting the string to Color'
        ColorConverter.convertFromHex(colorHex)

        then: 'IllegalArgumentException should be thrown'
        final IllegalArgumentException ex = thrown(IllegalArgumentException.class)
        ex.getMessage() == "Color Hex String length must be either 6 or 7 characters"
    }

    def "should throw IllegalArgumentException if the length of the string is more than 7"() {
        given: 'A hexadecimal color'
        final String colorHex = "#00T000"

        when: 'Converting the string to Color'
        ColorConverter.convertFromHex(colorHex)

        then: 'IllegalArgumentException should be thrown'
        final IllegalArgumentException ex = thrown(IllegalArgumentException.class)
        ex.getMessage() == "T is not a hexadecimal character at index 2"
    }

}
