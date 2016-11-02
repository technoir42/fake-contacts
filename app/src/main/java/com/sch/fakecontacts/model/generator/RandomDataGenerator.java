package com.sch.fakecontacts.model.generator;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;

public class RandomDataGenerator {
    private final Random random = new Random();
    private final Paint textPaint = new TextPaint();

    public String phoneNumber() {
        return "+79" + RandomStringUtils.randomNumeric(9);
    }

    public String email() {
        return randomAlphabeticString(5, 10).toLowerCase() + "@" +
                randomAlphabeticString(4, 7).toLowerCase() + ".com";
    }

    public PostalAddress postalAddress() {
        final PostalAddress.Builder builder = PostalAddress.builder()
                .setCountry(StringUtils.capitalize(randomAlphabeticString(5, 9).toLowerCase()))
                .setCity(StringUtils.capitalize(randomAlphabeticString(4, 9).toLowerCase()))
                .setStreet(StringUtils.capitalize(randomAlphabeticString(10, 20).toLowerCase()));
        if (random.nextBoolean()) {
            builder.setRegion(StringUtils.capitalize(randomAlphabeticString(5, 9).toLowerCase()));
        }
        if (random.nextBoolean()) {
            builder.setPostcode(String.valueOf(random.nextInt(1000000)));
        }
        return builder.build();
    }

    public Bitmap avatar(int width, int height, String initials) {
        final int red = random.nextInt(256);
        final int green = random.nextInt(256);
        final int blue = random.nextInt(256);
        final int bgColor = Color.rgb(red, green, blue);
        final int textColor;

        if (red * 299 + green * 587 + blue * 114 < 186000) {
            textColor = Color.WHITE;
        } else {
            textColor = Color.BLACK;
        }

        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(bgColor);

        textPaint.setColor(textColor);
        textPaint.setTextSize(width / 3);
        textPaint.setAntiAlias(true);

        final int x = (int) (canvas.getWidth() - textPaint.measureText(initials)) / 2;
        final int y = (int) (canvas.getHeight() / 2 - (textPaint.descent() + textPaint.ascent()) / 2) ;
        canvas.drawText(initials, x, y, textPaint);
        return bitmap;
    }

    @SuppressWarnings("unchecked")
    public final <T> T elementOf(T... values) {
        return values[random.nextInt(values.length)];
    }

    private String randomAlphabeticString(int minLength, int maxLength) {
        final int length = minLength + random.nextInt(maxLength - minLength + 1);
        return RandomStringUtils.randomAlphabetic(length);
    }
}
